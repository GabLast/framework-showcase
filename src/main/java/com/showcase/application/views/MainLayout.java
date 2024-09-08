package com.showcase.application.views;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.views.general.AboutView;
import com.showcase.application.views.general.HomeView;
import com.showcase.application.views.generics.navigation.MySideNavItem;
import com.showcase.application.views.module.TabTestData;
import com.showcase.application.views.myview.MyViewView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private final AppInfo appInfo;

    public MainLayout(AppInfo appInfo) {
        this.appInfo = appInfo;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Framework Showcase");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);
        header.addClickListener(event -> {
            UI.getCurrent().navigate(HomeView.class);
        });

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.setLabel("Label");
        nav.setCollapsible(true);
        nav.setVisible(true);
        nav.addItem(new MySideNavItem("My View", MyViewView.class, LineAwesomeIcon.PENCIL_RULER_SOLID.create(), true));
        nav.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.testdata"), TabTestData.class, LineAwesomeIcon.BOOK_SOLID.create(), true));
        nav.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.about"), AboutView.class, LineAwesomeIcon.ADDRESS_CARD.create(), true));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    private void setTheme(boolean dark) {
        var js = "document.documentElement.setAttribute('theme', $0)";

        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
