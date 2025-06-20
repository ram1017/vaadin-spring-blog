package com.example.app.base.ui.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("landing")
@PermitAll
public class LandingView extends VerticalLayout {

    public LandingView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#F5F7FA");

        // Title
        H1 title = new H1("Welcome to Blogy");
        title.getStyle().set("font-size", "96px").set("margin-bottom", "40px");

        // Thin arrow using Unicode
        Span arrow = new Span("→");
        arrow.getStyle()
                .set("font-size", "60px")
                .set("color", "black")
                .set("cursor", "pointer");

        arrow.addClickListener(e ->
                getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/auth0"))
        );

        add(title, arrow);
    }
}
