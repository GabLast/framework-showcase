package com.showcase.application.views.login;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.config.security.AuthenticatedUser;
import com.showcase.application.utils.GlobalConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@AnonymousAllowed
@Route(value = "login")
@CssImport(value = "./themes/framework-showcase/components/vaadin-login.css", themeFor = "vaadin-login-overlay-wrapper")
public class LoginView extends LoginOverlay implements BeforeEnterObserver, HasDynamicTitle {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AppInfo appInfo, AuthenticatedUser authenticatedUser) {
        this.setId("LoginView");
        this.authenticatedUser = authenticatedUser;

        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        //Error Handler
        LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle(UI.getCurrent().getTranslation("login.error.title"));
        errorMessage.setMessage(UI.getCurrent().getTranslation("login.error.message"));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setErrorMessage(errorMessage);
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("");
        i18n.getHeader().setDescription("");

        i18n.getForm().setTitle(UI.getCurrent().getTranslation("login.title"));
        i18n.getForm().setUsername(UI.getCurrent().getTranslation("login.username") + " - admin");
        i18n.getForm().setPassword(UI.getCurrent().getTranslation("login.password") + "- 123");
        i18n.getForm().setForgotPassword(UI.getCurrent().getTranslation("login.forgotpassword"));
        i18n.getForm().setSubmit(UI.getCurrent().getTranslation("login.submit"));
        i18n.setAdditionalInformation(UI.getCurrent().getTranslation("login.appinfo", appInfo.getAppVersion(), "Gabriel Marte"));

        setI18n(i18n);

        Image logo = new Image(GlobalConstants.LOGO, "LOGO");
        logo.setWidth("100%");
        logo.setHeight("100%");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        hl.getStyle().set("padding-left", "0.9rem");
        hl.addAndExpand(logo);

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        vl.addAndExpand(hl);

        setTitle(vl);
        setDescription(null);
        addRememberMeCheckbox();

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(forgotPasswordEvent -> UI.getCurrent().navigate(RecoverPasswordView.class));

        getElement().setAttribute("autocomplete", "off");
        setOpened(true);
    }

    public void addRememberMeCheckbox() {
        Checkbox checkRememberMe = new Checkbox(UI.getCurrent().getTranslation("rememberme"));
        checkRememberMe.setId("checkRememberMe");
        checkRememberMe.getElement().setAttribute("name", "remember-me");

        Element loginFormElement = this.getElement();
        Element element = checkRememberMe.getElement();
        loginFormElement.appendChild(element);
        UI.getCurrent().getPage().executeJs(
                "const check = document.getElementById(\"checkRememberMe\");" +
                        "\nconst password = document.getElementById(\"vaadinLoginPassword\");" +
                        "\npassword.after(check);");

        String executeJsForFieldString = "const field = document.getElementById($0);" +
                "if(field) {" +
                "   field.after($1)" +
                "} else {" +
                "   //console.error('could not find field', $0);" +
                "}";
        getElement().executeJs(executeJsForFieldString, "vaadinLoginPassword", checkRememberMe);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        //Error message
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("login.title");
    }
}
