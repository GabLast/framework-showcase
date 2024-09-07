package com.showcase.application.views.generics;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.Objects;

public class GenericNotification extends Notification {

    public GenericNotification(String text, NotificationVariant variant, Position position) {
        super();

        addThemeVariants(variant);
        setPosition(Objects.requireNonNullElse(position, Position.MIDDLE));

        Div divText = new Div(new Text(text));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            close();
        });

        HorizontalLayout layout = new HorizontalLayout(divText, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        setDuration(5000);

        add(layout);
    }

    public GenericNotification(String text, NotificationVariant variant, Position position, int timeInSeconds) {
        super();

        addThemeVariants(variant);
        setPosition(Objects.requireNonNullElse(position, Position.MIDDLE));

        Div divText = new Div(new Text(text));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            close();
        });

        HorizontalLayout layout = new HorizontalLayout(divText, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        setDuration(timeInSeconds * 1000);

        add(layout);
    }
}
