package com.showcase.application.views;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.config.security.AuthenticatedUser;
import com.showcase.application.config.security.MyVaadinSession;
import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.TranslationProvider;
import com.showcase.application.views.general.AboutView;
import com.showcase.application.views.general.FormUserSetting;
import com.showcase.application.views.general.HomeView;
import com.showcase.application.views.generics.navigation.MySideNavItem;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.showcase.application.views.generics.notifications.WarningNotification;
import com.showcase.application.views.module.TabTestData;
import com.showcase.application.views.reports.module.TabReportTestData;
import com.showcase.application.views.security.TabProfile;
import com.showcase.application.views.security.TabUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private final AppInfo appInfo;
    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    private UserSetting settings;
    private final TranslationProvider translationProvider;
    private final UserSettingService userSettingService;

    private H2 viewTitle;

    public MainLayout(AppInfo appInfo, AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, TranslationProvider translationProvider, UserSettingService userSettingService) {
        this.appInfo = appInfo;
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.translationProvider = translationProvider;
        this.userSettingService = userSettingService;

        this.settings = (UserSetting) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {

        //if there is a good logo:
//        Image imgLogo = new Image(GlobalConstants.LOGO, "LOGO");
//        imgLogo.setWidth("auto");
//        imgLogo.setHeight("50px");
//        imgLogo.addClickListener(event -> UI.getCurrent().navigate(HomeView.class));
//
//        HorizontalLayout hlLogo = new HorizontalLayout();
//        hlLogo.setAlignItems(FlexComponent.Alignment.CENTER);
//        hlLogo.setMargin(true);
//        hlLogo.setPadding(false);
//        hlLogo.setSpacing(false);
//        hlLogo.add(imgLogo);
//
//        Header header = new Header(hlLogo);

        Span appName = new Span("Framework Showcase");
        appName.getStyle().set("padding-left", "20px");

        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);
        header.addClickListener(event -> {
            UI.getCurrent().navigate(HomeView.class);
        });

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private Div createNavigation() {
        Div div = new Div();

        SideNav processesModule = new SideNav();
        processesModule.setCollapsible(true);
        processesModule.setExpanded(true);
        processesModule.setLabel(UI.getCurrent().getTranslation("title.module"));
        processesModule.setVisible(SecurityUtils.isAccessGranted(Permit.PROCESSES_MODULE));
        processesModule.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.testdata"),
                TabTestData.class, LineAwesomeIcon.FILE_ALT.create(), accessChecker.hasAccess(TabTestData.class)));
        div.add(processesModule);

        //***************************************************************

        SideNav reportsModule = new SideNav(UI.getCurrent().getTranslation("title.reports"));
        reportsModule.setCollapsible(true);
        reportsModule.setExpanded(false);
        reportsModule.setVisible(SecurityUtils.isAccessGranted(Permit.PROCESSES_MODULE));
        reportsModule.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.testdata"),
                TabReportTestData.class, LineAwesomeIcon.FILE_ALT.create(), accessChecker.hasAccess(TabReportTestData.class)));
        div.add(reportsModule);

        //***************************************************************

        SideNav securityModule = new SideNav(UI.getCurrent().getTranslation("title.security"));
        securityModule.setCollapsible(true);
        securityModule.setExpanded(false);
        securityModule.setVisible(SecurityUtils.isAccessGranted(Permit.SECURITY_MODULE));
        securityModule.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.profile"),
                TabProfile.class, LineAwesomeIcon.ID_BADGE.create(), accessChecker.hasAccess(TabProfile.class)));
        securityModule.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.user"),
                TabUser.class, LineAwesomeIcon.USER.create(), accessChecker.hasAccess(TabUser.class)));
        div.add(securityModule);

        //***************************************************************
        SideNav aboutNav = new SideNav();
        aboutNav.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.about"), AboutView.class, LineAwesomeIcon.ADDRESS_CARD.create(), accessChecker.hasAccess(AboutView.class)));
        div.add(aboutNav);

        return div;
    }

    private VerticalLayout createFooter() {
        VerticalLayout footer = new VerticalLayout();

        if (authenticatedUser.get().isEmpty()) {
            Anchor loginLink = new Anchor("login", UI.getCurrent().getTranslation("login.title"));
            footer.add(loginLink);
            footer.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, loginLink);
        }

        User user = authenticatedUser.get().get();

        Avatar avatar = new Avatar(user.getName());
        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");

        MenuBar menuHolder = new MenuBar();
        menuHolder.setThemeName("tertiary-inline contrast");

        MenuItem menu = menuHolder.addItem("");
        Div div = new Div();
        div.add(avatar);
        div.add(user.getName());
        div.add(new Icon("lumo", "dropdown"));
        div.getElement().getStyle().set("display", "flex");
        div.getElement().getStyle().set("align-items", "center");
        div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
        menu.add(div);

        menu.getSubMenu().addItem(
                UI.getCurrent().getTranslation("settings"),
                event -> {
                    new FormUserSetting(translationProvider, userSettingService, userSetting -> {
                        UI.getCurrent().getPage().reload();
                        try {
                            if (userSetting != null) {
                                UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString(), userSetting);
                                UI.getCurrent().getPage().reload();
                                this.settings = (UserSetting) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
                            }
                        } catch (MyException e) {
                            new ErrorNotification(e.getMessage());
                        }

                    });
                }
        );

        menu.getSubMenu().addItem(
                UI.getCurrent().getTranslation("changepassword"),
                event -> new WarningNotification("In development")
        );

        menu.getSubMenu().addItem(UI.getCurrent().getTranslation("signout"), e -> {
            authenticatedUser.logout();
        });
        menu.getSubMenu().getItems().getLast().getStyle().set("color", "#de3b3b");

        footer.add(menuHolder);
        footer.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, menuHolder);

        return footer;
    }

    private void setTheme(boolean dark) {
        var js = "document.documentElement.setAttribute('theme', $0)";

        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        setTheme(settings.isDarkMode());
    }

    private String getCurrentPageTitle() {
        String title = UI.getCurrent().getTranslation("app.title");

        Component content = getContent();
        if (content instanceof HasDynamicTitle) {
            title = ((HasDynamicTitle) content).getPageTitle();
        }

        return title;
    }
}
