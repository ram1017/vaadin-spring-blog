package com.example.app.base.ui.view;

import com.example.app.model.Post;
import com.example.app.service.PostService;
import com.example.app.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;


@Route(value = "edit-post", layout = MainLayout.class)
@PermitAll
public class EditPostView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final PostService postService;
    private final UserService userService;

    private final TextField title = new TextField("Title");
    private final TextArea content = new TextArea("Content");
    private final TextField imageUrl = new TextField("Image URL");
    private Post currentPost;

    public EditPostView(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        H2 heading = new H2("Edit Post");

        content.setHeight("250px");
        styleInput(title);
        styleInput(content);
        styleInput(imageUrl);

        Button saveButton = new Button("Save Changes", e -> {
            if (currentPost == null || title.isEmpty() || content.isEmpty()) {
                Notification.show("Please fill all fields.");
                return;
            }

            currentPost.setTitle(title.getValue());
            currentPost.setContent(content.getValue());
            currentPost.setImageUrl(imageUrl.getValue());

            postService.save(currentPost);
            Notification.show("Post updated!");
            getUI().ifPresent(ui -> ui.navigate("myview"));
        });

        saveButton.getStyle()
                .set("background-color", "#4CAF50")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "5px");

        VerticalLayout formLayout = new VerticalLayout(title, content, imageUrl, saveButton);
        formLayout.setWidth("600px");
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        formLayout.getStyle().set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)");

        add(heading, formLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer id) {
        if (id != null) {
            currentPost = postService.getById(id);
            if (currentPost != null) {
                title.setValue(currentPost.getTitle());
                content.setValue(currentPost.getContent());
                imageUrl.setValue(currentPost.getImageUrl() != null ? currentPost.getImageUrl() : "");
            } else {
                Notification.show("Post not found");
            }
        } else {
            Notification.show("No ID provided");
        }
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
