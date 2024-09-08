package com.showcase.application.views.generics;

import com.showcase.application.utils.Utilities;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class SecurityForm extends VerticalLayout {

    private TextField tfId, tfCreatedBy, tfModifiedBy, tfCreationDate, tfModifiedDate, tfVersion;
    private Checkbox checkEnabled;

    SecurityForm() {
        tfId = new TextField(UI.getCurrent().getTranslation("databaseID"));
        tfId.setReadOnly(true);

        tfCreatedBy = new TextField(UI.getCurrent().getTranslation("createdby"));
        tfCreatedBy.setReadOnly(true);

        tfModifiedBy = new TextField(UI.getCurrent().getTranslation("modifiedby"));
        tfModifiedBy.setReadOnly(true);

        tfCreationDate = new TextField(UI.getCurrent().getTranslation("creationdate"));
        tfCreationDate.setReadOnly(true);

        tfModifiedDate = new TextField(UI.getCurrent().getTranslation("lastmodificationdate"));
        tfModifiedDate.setReadOnly(true);

        tfVersion = new TextField(UI.getCurrent().getTranslation("version"));
        tfVersion.setReadOnly(true);

        checkEnabled = new Checkbox(UI.getCurrent().getTranslation("enabled"));
        checkEnabled.setReadOnly(true);

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.add(tfId);
        formLayout.add(tfCreatedBy);
        formLayout.add(tfModifiedBy);
        formLayout.add(tfCreationDate);
        formLayout.add(tfModifiedDate);
        formLayout.add(checkEnabled);
        formLayout.add(tfVersion);

        setMargin(false);
        setPadding(false);
        setSpacing(true);
        addAndExpand(formLayout);
    }

    SecurityForm(Object object) {
        this();
        fillFields(object);
    }

    private void fillFields(Object object) {
        if (object == null) {
            return;
        }

        try {
            fillId(Utilities.getFieldValue(object, "id").toString());
        } catch (Exception e) {
            fillId("-1");
        }

        try {
            fillCreatedBy(Utilities.getFieldValue(object, "createdBy").toString());
        } catch (Exception e) {
            fillCreatedBy("-1");
        }

        try {
            fillModifiedBy(Utilities.getFieldValue(object, "modifiedBy").toString());
        } catch (Exception e) {
            fillModifiedBy("-1");
        }

        try {
            fillCreationDate(Utilities.getFieldValue(object, "dateCreated").toString());
        } catch (Exception e) {
            fillCreationDate("-1");
        }

        try {
            fillLastModificationDate(Utilities.getFieldValue(object, "lastUpdated").toString());
        } catch (Exception e) {
            fillLastModificationDate("-1");
        }

        try {
            fillEnabled((Boolean) Utilities.getFieldValue(object, "enabled"));
        } catch (Exception e) {
            fillEnabled(false);
        }

        try {
            fillVersion(Utilities.getFieldValue(object, "version").toString());
        } catch (Exception e) {
            fillVersion("-1");
        }
    }

    private void fillId(String value) {
        tfId.setValue(value);
    }

    private void fillCreatedBy(String value) {
        tfCreatedBy.setValue(value);
    }

    private void fillModifiedBy(String value) {
        tfModifiedBy.setValue(value);
    }

    private void fillCreationDate(String value) {
        tfCreationDate.setValue(value);
    }

    private void fillLastModificationDate(String value) {
        tfModifiedDate.setValue(value);
    }

    private void fillEnabled(boolean value) {
        checkEnabled.setValue(value);
    }

    private void fillVersion(String value) {
        tfVersion.setValue(value);
    }
}
