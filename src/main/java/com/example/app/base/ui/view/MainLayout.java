package com.example.app.base.ui.view;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class MainLayout extends AppLayout {

    public MainLayout() {
        String currentPath = VaadinService.getCurrentRequest().getPathInfo();
        OidcUser authuser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = authuser.getEmail();
        String username = email.split("@")[0];

        RouterLink blogsLink = new RouterLink("Blogs", PostView.class);
        RouterLink myBlogsLink = new RouterLink("My Blogs", PostMyView.class);


        if (currentPath != null && currentPath.contains("home")) {
            blogsLink.getStyle().set("font-weight", "bold").set("border-bottom", "2px solid black");
        } else if (currentPath != null && currentPath.contains("my-blogs")) {
            myBlogsLink.getStyle().set("font-weight", "bold").set("border-bottom", "2px solid black");
        }


        blogsLink.getStyle()
                .set("margin", "0 15px")
                .set("text-decoration", "none")
                .set("color", "black")
                .set("font-size", "24px");

        myBlogsLink.getStyle()
                .set("margin", "0 15px")
                .set("text-decoration", "none")
                .set("color", "black")
                .set("font-size", "24px");

        HorizontalLayout centerLayout = new HorizontalLayout(blogsLink, myBlogsLink);
        centerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centerLayout.setSpacing(true);
        centerLayout.setWidthFull();

        Button logout = new Button("Logout", e ->
                UI.getCurrent().getPage().setLocation("/logout")
        );

        H5 h5 = new H5("Hello!"+username);
        h5.getStyle().set("color", "black")
                .set("border-radius", "8px")
                .set("font-size", "24px")
                .set("padding", "8px 16px")
                .set("text-decoration", "none")

                .set("font-weight", "200");

        logout.getStyle()
                .set("background-color", "transparent")
                .set("color", "black")
                .set("border-radius", "8px")
                .set("font-size", "24px")
                .set("padding", "8px 16px")
                .set("text-decoration", "none")

                .set("font-weight", "200");

        HorizontalLayout logoutLayout = new HorizontalLayout(logout);
        HorizontalLayout nameLayout = new HorizontalLayout(h5);

        nameLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        nameLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        nameLayout.setWidth("auto");

        logoutLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoutLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        logoutLayout.setWidth("auto");

        HorizontalLayout navBar = new HorizontalLayout();
        navBar.setWidthFull();
        navBar.setAlignItems(FlexComponent.Alignment.CENTER);
        navBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navBar.add(nameLayout,centerLayout, logoutLayout);

        addToNavbar(navBar);
    }
}
