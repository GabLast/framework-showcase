package com.showcase.application.views.myview;

import com.showcase.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("My View")
@Route(value = "view", layout = MainLayout.class)
@RouteAlias(value = "view", layout = MainLayout.class)
public class MyViewView extends Composite<VerticalLayout> {

    public MyViewView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}
