package com.showcase.application.views.generics.dialog;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class ConfirmWindow extends ConfirmDialog {

    public ConfirmWindow(String message, Runnable callbackYes) {
        this(
                UI.getCurrent().getTranslation("action.confirm"),
                message,
                callbackYes);
    }

    public ConfirmWindow(String title, String message, Runnable callbackYes) {
        setCloseOnEsc(true);
        setCancelable(true);

        setHeader(title);
        setText(message);

        setConfirmText(UI.getCurrent().getTranslation("yes"));
        addConfirmListener(event -> {
            callbackYes.run();
            close();
        });

        setCancelText(UI.getCurrent().getTranslation("no"));
        addCancelListener(cancelEvent -> close());
    }
}
