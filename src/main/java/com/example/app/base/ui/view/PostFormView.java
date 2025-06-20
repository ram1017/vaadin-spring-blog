package com.example.app.base.ui.view;

import com.example.app.model.Post;
import com.example.app.model.User;
import com.example.app.service.PostService;
import com.example.app.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.LocalDateTime;

@Route(value = "create-post", layout = MainLayout.class)
@PermitAll
public class PostFormView extends VerticalLayout {

    public PostFormView(PostService postService, UserService userService) {
        OidcUser authuser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = authuser.getEmail();
        User user = userService.findByEmail(email);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        setSpacing(true);
        setPadding(true);
        getStyle().set("background-color", "#f9f9f9");

        H2 heading = new H2("Create New Post");
        heading.getStyle().set("margin-bottom", "20px");

        TextField title = new TextField("Title");
        styleInput(title);

        TextArea content = new TextArea("Content");
        content.setHeight("250px");
        styleInput(content);

        TextField imageUrlField = new TextField("Image URL");
        styleInput(imageUrlField);

        Integer userIdField = user.getId();

        Button submit = new Button("Submit", e -> {
            if (title.isEmpty() || content.isEmpty() ) {
                Notification.show("Please fill in all fields");
                return;
            }

            Post post = new Post();
            post.setTitle(title.getValue());
            post.setContent(content.getValue());
            post.setImageUrl(imageUrlField.getValue());
            post.setUserId(userIdField.intValue());
            post.setCreatedAt(LocalDateTime.now());

            postService.save(post);

            Notification.show("Post saved!");
            getUI().ifPresent(ui -> ui.navigate("home"));

        });

        submit.getStyle()
                .set("background-color", "#4CAF50")
                .set("color", "white")
                .set("border", "none")
                .set("border-radius", "5px")
                .set("padding", "10px 20px");

        VerticalLayout formLayout = new VerticalLayout(title, content, imageUrlField, submit);
        formLayout.setWidth("600px");
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        formLayout.getStyle().set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)")
                .set("padding", "30px");

        add(heading, formLayout);
    }

    private void styleInput(com.vaadin.flow.component.Component component) {
        component.getElement().getStyle()
                .set("background-color", "#ffffff")
                .set("border", "1px solid #ccc")
                .set("border-radius", "6px")
                .set("width", "100%")
                .set("padding", "8px");
    }
}
