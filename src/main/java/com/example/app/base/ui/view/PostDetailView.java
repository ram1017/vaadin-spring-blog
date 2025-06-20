package com.example.app.base.ui.view;

import com.example.app.model.Post;
import com.example.app.model.Comment;
import com.example.app.model.User;
import com.example.app.service.PostService;
import com.example.app.service.CommentService;
import com.example.app.service.UserService;
import com.example.app.service.GeminiService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Route(value = "post", layout = MainLayout.class)
@PermitAll
public class PostDetailView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;
    private final GeminiService geminiService;

    private final VerticalLayout commentSection = new VerticalLayout();
    private Integer currentPostId;
    private StompSession stompSession;

    public PostDetailView(PostService postService, CommentService commentService, UserService userService, GeminiService geminiService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.geminiService = geminiService;

        OidcUser authuser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = authuser.getEmail();
        User user = userService.findByEmail(email);

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        getStyle().set("padding", "20px");
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer postId) {
        OidcUser authuser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = authuser.getEmail();
        User user = userService.findByEmail(email);
        if (postId != null) {
            currentPostId = postId;
            Post post = postService.getById(postId);
            Integer authorId =post.getUserId();
            Optional<User> author=userService.findById(authorId);

            if (post != null) {
                removeAll();

                if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                    Image image = new Image(post.getImageUrl(), "Post image");
                    image.setWidth("60%");
                    image.getStyle().set("object-fit", "cover").set("border-radius", "8px").set("margin-bottom", "20px");
                    add(image);
                }

                H1 title = new H1(post.getTitle());
                H5 authorname= new H5("@"+author.get().getUsername());
                    authorname.getStyle().set("font-weight", "bold").set("font-size", "18px").set("color", "#595959");
                title.getStyle().set("margin-top", "20px");
                Paragraph date = new Paragraph("Posted on: " + post.getCreatedAt().toLocalDate());
                Paragraph content = new Paragraph(post.getContent());
                content.setWidth("60%");

                add(title,authorname, date, content);

                Button summarizeButton = new Button("Summarize Post");
                Div summaryDiv = new Div();
                summaryDiv.getStyle().set("margin-top", "20px").set("white-space", "pre-wrap").set("width", "60%");

                summarizeButton.addClickListener(e -> {
                    String summary = geminiService.summarize(post.getContent());
                    summaryDiv.setText(summary);
                });
                summarizeButton.getStyle().set("background-color", "black")
                        .set("color", "white")
                        .set("padding", "20px 10px")
                        .set("border-radius", "8px")
                        .set("height", "50px")
                        .set("width", "150px")
                        .set("border", "1px solid #ccc");

                add(summarizeButton, summaryDiv);

                Hr separator = new Hr();
                separator.getStyle().set("width", "100%").set("margin", "30px 0");

                TextArea commentInput = new TextArea();
                commentInput.setPlaceholder("Write your comment...");
                commentInput.setWidth("950px");
                commentInput.setHeight("40px");
                commentInput.getStyle()
                        .set("background-color", "#ffffff")
                        .set("border", "1px solid #ccc")
                        .set("border-radius", "8px");

                Button sendButton = new Button("Add Comment", e -> {
                    if (!commentInput.getValue().isBlank()) {
                        Comment comment = new Comment();
                        comment.setComment(commentInput.getValue());
                        comment.setPostId(currentPostId);
                        comment.setUserId(user.getId());

                        commentService.save(comment);

                        if (stompSession != null && stompSession.isConnected()) {
                            stompSession.send("/app/comment", comment);
                        }
                        commentInput.clear();
                    }
                });
                sendButton.getStyle().set("color", "white")
                        .set("height", "50px")
                        .set("width", "150px")
                        .set("margin-top", "20px")
                        .set("background-color", "#000000");

                HorizontalLayout inputArea = new HorizontalLayout(commentInput, sendButton);
                inputArea.setAlignItems(Alignment.BASELINE);
                inputArea.setSpacing(true);

                H2 commentHeading = new H2("Comments");
                commentHeading.getStyle().set("margin-top", "20px");

                commentSection.setWidth("60%");
                commentSection.setHeight("250px");
                commentSection.getStyle()
                        .set("overflow-y", "auto")
                        .set("border", "1px solid #ccc")
                        .set("border-radius", "8px")
                        .set("padding", "10px")
                        .set("background-color", "#fff")
                        .set("margin-top", "10px");

                add(separator, commentHeading, inputArea, commentSection);

                loadExistingComments(postId);
                connectWebSocket(postId);
            } else {
                add(new Paragraph("Post not found."));
            }
        } else {
            add(new Paragraph("Invalid post ID."));
        }
    }

    private void loadExistingComments(Integer postId) {
        List<Comment> comments = commentService.getByPostId(postId);
        commentSection.removeAll();
        for (Comment c : comments) {
            String username = userService.getUsernameById(c.getUserId());
            commentSection.add(createStyledComment("@" + username, c.getComment()));
        }
    }


    private Component createStyledComment(String username, String content) {
        Div wrapper = new Div();
        wrapper.getStyle()
                .set("padding", "10px")
                .set("border-radius", "2px")
                .set("width", "98%")
                .set("border-bottom", "1px solid #e0e0e0");

        Span userSpan = new Span(username + ": ");
        userSpan.getStyle().set("font-weight", "bold");

        Paragraph commentText = new Paragraph(content);
        commentText.getStyle().set("margin", "5px 0 0 0");

        wrapper.add(userSpan, commentText);
        return wrapper;
    }



    private void connectWebSocket(Integer postId) {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        UI ui = UI.getCurrent();

        stompClient.connect("ws://localhost:8080/ws-comments", new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                stompSession = session;

                session.subscribe("/topic/comments/" + postId, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Comment.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        Comment newComment = (Comment) payload;
                        ui.access(() -> {
                            String username = userService.getUsernameById(newComment.getUserId());
                            commentSection.add(
                                    createStyledComment("@" + username, newComment.getComment())
                            );
                        });
                    }

                });
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("Transport error: " + exception.getMessage());
            }
        });



    }
}