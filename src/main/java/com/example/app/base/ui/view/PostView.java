package com.example.app.base.ui.view;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;



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

import java.util.List;

@Route(value="home",layout = MainLayout.class)
@RolesAllowed("USER")
public class PostView extends VerticalLayout {

    private final PostService postService;

    @Autowired
    public PostView(PostService postService, UserService userService) {
        this.postService = postService;
        OidcUser authuser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = authuser.getEmail();
        System.out.println(email);
        User user = userService.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);

            user.setUsername(email.split("@")[0]);


            userService.save(user);
        }


        Button addPostButton = new Button(" +");
        addPostButton.getStyle().set("margin-bottom", "20px").set("background-color", "black").set("color", "white").set("z-index", "1000").set("position", "fixed")
                .set("bottom", "50px")
                .set("right", "100px")
                .set("font-size", "35px").set("border-radius", "20px").set("height", "50px").set("width", "50px");
        addPostButton.addClickListener(e ->
                addPostButton.getUI().ifPresent(ui -> ui.navigate("create-post"))
        );

        HorizontalLayout toolbar = new HorizontalLayout(addPostButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);

        // Post Grid layout
        Div postGrid = new Div();
        postGrid.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("gap", "20px")
                .set("justify-content", "center")
                .set("padding", "10px");

        List<Post> posts = postService.getAllPosts();

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
                    .set("max-width","30%");


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


            card.add(title, preview, dateWrapper, readMore);
            postGrid.add(card);
        }

        setPadding(true);
        setAlignItems(Alignment.CENTER);
        add(toolbar, postGrid);
    }
}