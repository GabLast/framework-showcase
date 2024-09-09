package com.showcase.application.views.general;

import com.showcase.application.config.security.AuthenticatedUser;
import com.showcase.application.config.security.MyVaadinSession;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;

import java.util.TimeZone;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout implements BeforeEnterListener, HasDynamicTitle {

    private final AuthenticatedUser authenticatedUser;

    private final UserSettingService userSettingService;
    private final CustomUserDetailsService userDetailsService;
    private String timeZone;

    public HomeView(AuthenticatedUser authenticatedUser, UserSettingService userSettingService, CustomUserDetailsService userDetailsService) {
        this.authenticatedUser = authenticatedUser;
        this.userSettingService = userSettingService;
        this.userDetailsService = userDetailsService;

        //VaadinSession variables init
        setSession();

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

    private void setSession() {
        //Set user
        UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USER.toString(), authenticatedUser.get().orElse(null));

        //Set settings
        User user = (User) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        UserSetting userSetting = userSettingService.getSettingByUserAndEnabled(user, true);
        if (userSetting == null) {
            userSetting = new UserSetting();
            userSetting.setUser(user);
            userSetting.setTimeZoneString(StringUtils.isBlank(timeZone) || (TimeZone.getTimeZone(timeZone) != null) ? GlobalConstants.DEFAULT_TIMEZONE : timeZone);
        }
        userSetting = userSettingService.save(userSetting);
        UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString(), userSetting);

        userDetailsService.grantAuthorities(user);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getUI().getPage().retrieveExtendedClientDetails(detail -> {
            timeZone = detail.getTimeZoneId();
        });
    }

    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("title.home");
    }
}
