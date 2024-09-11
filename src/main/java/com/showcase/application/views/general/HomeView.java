package com.showcase.application.views.general;

import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout implements HasDynamicTitle, BeforeEnterListener{


    public HomeView() {
        Image imgLogo = new Image(GlobalConstants.LOGO, "LOGO");
        imgLogo.setWidth(50, Unit.PERCENTAGE);
        imgLogo.setHeight(50, Unit.PERCENTAGE);

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setPadding(false);
        setMargin(false);
        setSpacing(false);
        add(imgLogo);
    }


    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("title.home");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }
}
