package com.showcase.application.views.generics;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;

import java.util.List;

public abstract class CustomFormFactory<T> extends DefaultCrudFormFactory<T> {

    public CustomFormFactory(Class<T> domainType) {
        super(domainType);
    }

    public abstract Component applyChanges(CrudOperation operation, boolean readOnly, FormLayout campos, Component footer, VerticalLayout mainLayout);

    @Override
    public Component buildNewForm(CrudOperation operation, T domainObject, boolean readOnly, ComponentEventListener<ClickEvent<Button>> cancelButtonClickListener, ComponentEventListener<ClickEvent<Button>> operationButtonClickListener) {
        //Form
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        List<HasValueAndElement> fields = this.buildFields(operation, domainObject, readOnly);
        fields.forEach((field) -> {
            formLayout.getElement().appendChild(field.getElement());
        });

        //Footer
        Component footerLayout = this.buildFooter(operation, domainObject, cancelButtonClickListener, operationButtonClickListener);

        //Security form
        SecurityForm securityForm = new SecurityForm(domainObject);

        Div contentTab = new Div();
        contentTab.setWidthFull();
        contentTab.add(formLayout);

        Tab tabGeneral = new Tab(UI.getCurrent().getTranslation("general"));
        Tab tabSecurity = new Tab(UI.getCurrent().getTranslation("security"));
        if (operation.equals(CrudOperation.ADD)) {
            tabSecurity.setVisible(false);
        }

        Tabs tabs = new Tabs(tabGeneral, tabSecurity);
        tabs.setWidthFull();
        tabs.setAutoselect(true);
        tabs.addSelectedChangeListener(event ->{
            contentTab.removeAll();

            if (event.getSelectedTab().equals(tabGeneral)) {
                contentTab.add(formLayout);
            } else if (event.getSelectedTab().equals(tabSecurity)) {
                contentTab.add(securityForm);
            }
        });

        VerticalLayout mainLayout = new VerticalLayout(tabs, contentTab, footerLayout);
        mainLayout.setFlexGrow(1.0D, contentTab);
        mainLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, footerLayout);
        mainLayout.setMargin(false);
        mainLayout.setPadding(false);
        mainLayout.setSpacing(true);

        this.configureForm(formLayout, fields);
        return applyChanges(operation, readOnly, formLayout, footerLayout, mainLayout);
    }
}
