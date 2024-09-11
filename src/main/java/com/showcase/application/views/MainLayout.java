package com.showcase.application.views;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.config.security.AuthenticatedUser;
import com.showcase.application.config.security.MyVaadinSession;
import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.UserService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.TranslationProvider;
import com.showcase.application.views.general.AboutView;
import com.showcase.application.views.general.FormUserSetting;
import com.showcase.application.views.general.HomeView;
import com.showcase.application.views.generics.navigation.MySideNavItem;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.showcase.application.views.generics.notifications.WarningNotification;
import com.showcase.application.views.module.TabTestData;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout implements AfterNavigationObserver {

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

//        SideNav menuModule = new SideNav();
//        menuModule.setLabel("Label");
//        menuModule.setCollapsible(true);
//        menuModule.setVisible(SecurityUtils.isAccessGranted(Permit.MENU_TEST_DATA));
        SideNavItem menuModule = new SideNavItem(UI.getCurrent().getTranslation("title.module"), "", VaadinIcon.ENVELOPE.create());
        menuModule.setVisible(
                SecurityUtils.isAccessGranted(Permit.MENU_TEST_DATA) ||
                        SecurityUtils.isAccessGranted(Permit.MENU_TEST_DATA)
        );
        menuModule.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.testdata"), TabTestData.class, LineAwesomeIcon.BOOK_SOLID.create(), accessChecker.hasAccess(TabTestData.class)));
//        menuModule.addItem(new MySideNavItem(UI.getCurrent().getTranslation("title.testdata"), TabTestData.class, LineAwesomeIcon.BOOK_SOLID.create(), accessChecker.hasAccess(TabTestData.class)));
        div.add(menuModule);

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

    private void setTheme2(boolean isDark) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        themeList.remove(Lumo.DARK);
        themeList.remove(Lumo.LIGHT);

        if (isDark) {
            themeList.add(Lumo.DARK);
        } else {
            themeList.add(Lumo.LIGHT);
        }
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

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setTheme(settings.isDarkMode());
    }
}
