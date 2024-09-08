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

public class SuccessNotification extends Notification {

    public SuccessNotification(String message) {
        super();

        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Position.BOTTOM_END);
        setDuration(5 * 1000);

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

    public SuccessNotification(String message, int timeInSeconds) {
        super();

        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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


