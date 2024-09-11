package com.showcase.application.config.security;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.utils.TranslationProvider;
import com.showcase.application.views.general.HomeView;
import com.showcase.application.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;


@Configuration
@RequiredArgsConstructor
public class UIServiceListener implements VaadinServiceInitListener {

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    private final CustomUserDetailsService userDetailsService;
    private final UserSettingService userSettingService;
    private String timeZone;
    private Locale locale;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();

            //add beforeEnter to validate settings and other configurations
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    private void beforeEnter(BeforeEnterEvent event) {
        event.getUI().getPage().retrieveExtendedClientDetails(detail -> timeZone = detail.getTimeZoneId());

        UserSetting userSetting = (UserSetting) event.getUI().getSession().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        Locale locale = event.getUI().getLocale();
        if (locale == null || (userSetting != null && StringUtils.isNotBlank(userSetting.getLanguage()) && !userSetting.getLanguage().equalsIgnoreCase(locale.getLanguage()))) {
            setLanguage(event.getUI(), userSetting);
        }

        if (authenticatedUser.isUserLoggedIn()) {
            setPermits(authenticatedUser.get().orElse(null));
            setSettings(event.getUI(), authenticatedUser.get().orElse(null));
            if (event.getNavigationTarget() == LoginView.class) {
                event.rerouteTo(HomeView.class);
            }
        }

        if (!accessChecker.hasAccess(event.getNavigationTarget())) {
            if (authenticatedUser.isUserLoggedIn()) {
                event.rerouteTo(HomeView.class);
            } else {
                event.rerouteTo(LoginView.class);
            }
        }
    }

    private void setLanguage(UI ui, UserSetting userSetting) {
        if (userSetting == null || StringUtils.isBlank(userSetting.getLanguage())) {
            Optional<Cookie> localeCookie = Optional.empty();

            Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
            if (cookies != null) {
                localeCookie = Arrays.stream(cookies).filter(cookie -> "locale".equals(cookie.getName())).findFirst();
            }

            if (localeCookie.isPresent() && !"".equals(localeCookie.get().getValue())) {
                // Cookie found, use that
                locale = Locale.forLanguageTag(localeCookie.get().getValue());
            } else {
                // Try to use Vaadin's browser locale detection
                locale = VaadinService.getCurrentRequest().getLocale();
            }

            // If the detection fails, default to the first language we support.
            if (locale.getLanguage().isEmpty()) {
                locale = TranslationProvider.ENGLISH;
            }
        } else {
            locale = Locale.forLanguageTag(userSetting.getLanguage());
        }

        ui.setLocale(locale);
    }

    private void setSettings(UI ui, User user) {
        UserSetting settings = (UserSetting) ui.getSession().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        if (settings == null) {
            settings = new UserSetting();
            settings.setUser(user);
            settings.setTimeZoneString(StringUtils.isBlank(timeZone) || (TimeZone.getTimeZone(timeZone) != null) ? GlobalConstants.DEFAULT_TIMEZONE : timeZone);
        }
        UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString(), settings);
    }

    private void setPermits(User user) {
        if(user == null) {
            return;
        }
        UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USER.toString(), user);
        userDetailsService.grantAuthorities(user);
    }

}

