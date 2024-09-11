package com.showcase.application.views.generics.notifications;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class WarningNotification extends Notification {

    public WarningNotification(String message) {
        super();

        addThemeVariants(NotificationVariant.LUMO_WARNING);
        setPosition(Position.MIDDLE);
        setDuration(4 * 1000);

        Div divText = new Div(new Text(message));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            close();
        });

        HorizontalLayout layout = new HorizontalLayout(divText, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        add(layout);
        open();
    }

    public WarningNotification(String message, int timeInSeconds) {
        super();

        addThemeVariants(NotificationVariant.LUMO_WARNING);
        setPosition(Position.MIDDLE);
        setDuration(timeInSeconds * 1000);

        Div divText = new Div(new Text(message));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            close();
        });

        HorizontalLayout layout = new HorizontalLayout(divText, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        add(layout);
        open();
    }
}


