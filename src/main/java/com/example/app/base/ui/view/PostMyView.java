package com.example.app.base.ui.view;

import com.example.app.model.Post;
import com.example.app.model.User;
import com.example.app.service.PostService;
import com.example.app.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;

@Route(value = "myview", layout = MainLayout.class)
@RolesAllowed("USER")
public class PostMyView extends VerticalLayout {

    private final PostService postService;

    @Autowired
    public PostMyView(PostService postService, UserService userService) {
        this.postService = postService;

        OidcUser authuser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = authuser.getEmail();
        User user = userService.findByEmail(email);
        int userId = user.getId();

        Button addPostButton = new Button(" +");
        addPostButton.getStyle()
                .set("margin-bottom", "20px")
                .set("background-color", "black")
                .set("color", "white")
                .set("z-index", "1000")
                .set("position", "fixed")
                .set("bottom", "50px")
                .set("right", "100px")
                .set("font-size", "35px")
                .set("border-radius", "20px")
                .set("height", "50px")
                .set("width", "50px");

        addPostButton.addClickListener(e ->
                addPostButton.getUI().ifPresent(ui -> ui.navigate("create-post"))
        );

        HorizontalLayout toolbar = new HorizontalLayout(addPostButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);

        Div postGrid = new Div();
        postGrid.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("gap", "20px")
                .set("justify-content", "center")
                .set("padding", "10px");

        List<Post> posts = postService.getPostById(userId);
        if (posts.isEmpty()) {
            H4 text = new H4("No posts found");
            add(text);
            return;
        }

        for (Post post : posts) {
            VerticalLayout card = new VerticalLayout();
            card.setWidth("400px");
            card.getStyle()
                    .set("border", "1px solid #e0e0e0")
                    .set("padding", "12px")
                    .set("border-radius", "5px")
                    .set("box-shadow", "0 4px 10px rgba(0,0,0,0.06)")
                    .set("background-color", "#F5F7FA")
                    .set("flex-grow", "1")
                    .set("gap", "8px")
                    .set("max-width", "30%");

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                Image image = new Image(post.getImageUrl(), "Post Image");
                image.setWidth("100%");
                image.setHeight("220px");
                image.getStyle()
                        .set("object-fit", "cover")
                        .set("border-radius", "5px");
                card.add(image);
            }

            H3 title = new H3(post.getTitle());
            title.getStyle()
                    .set("margin", "0")
                    .set("font-size", "25px")
                    .set("font-weight", "bold");

            String snippet = post.getContent().length() > 100
                    ? post.getContent().substring(0, 100) + "..."
                    : post.getContent();

            Paragraph preview = new Paragraph(snippet);
            preview.getStyle().set("font-size", "15px").set("margin", "0");

            Div dateWrapper = new Div();
            dateWrapper.setWidthFull();
            dateWrapper.getStyle().set("text-align", "right");

            Span date = new Span(post.getCreatedAt().toLocalDate().toString());
            date.getStyle()
                    .set("font-size", "13px")
                    .set("color", "#777")
                    .set("font-weight", "500");

            dateWrapper.add(date);


            Button readMore = new Button("Read More →", e ->
                    e.getSource().getUI().ifPresent(ui -> ui.navigate("post/" + post.getId()))
            );
            readMore.getStyle()
                    .set("margin-top", "8px")
                    .set("background-color", "#F5F7FA")
                    .set("color", "#000")
                    .set("font-weight", "bold");

            Button editButton = new Button("Edit", e ->
                    e.getSource().getUI().ifPresent(ui -> ui.navigate("edit-post/" + post.getId()))
            );
            editButton.getStyle()
                    .set("background-color", "transparent")
                    .set("border", "1px solid #ccc")
                    .set("color", "black")
                    .set("font-weight", "500")
                    .set("border-radius", "5px")
                    .set("padding", "6px 12px");

            Button deleteButton = new Button("Delete", e -> {
                getUI().ifPresent(ui -> {
                    ui.getPage().executeJs("return confirm('Are you sure you want to delete this post?');")
                            .then(Boolean.class, confirmed -> {
                                if (confirmed) {
                                    postService.deleteById(post.getId());
                                    postGrid.remove(card);
                                }
                            });
                });
            });
            deleteButton.getStyle()
                    .set("background-color", "transparent")
                    .set("border", "1px solid #ccc")
                    .set("color", "black")
                    .set("font-weight", "500")
                    .set("border-radius", "5px")
                    .set("padding", "6px 12px");

            HorizontalLayout actionRow = new HorizontalLayout(editButton, deleteButton);
            actionRow.setSpacing(true);
            actionRow.setJustifyContentMode(JustifyContentMode.START);

            card.add(title, preview, dateWrapper, readMore, actionRow);
            postGrid.add(card);
        }

        setPadding(true);
        setAlignItems(Alignment.CENTER);
        add(toolbar, postGrid);
    }
}
