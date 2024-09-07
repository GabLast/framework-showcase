package com.showcase.application.config.security;

import com.showcase.application.models.security.User;
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


@Configuration
@RequiredArgsConstructor
public class UIServiceListener implements VaadinServiceInitListener {

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
//    private final TranslationProvider translationProvider;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();

            //Agregamos before listener para seguridad
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    private void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.isUserLoggedIn()) {
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

        Locale locale = event.getUI().getLocale();
        User user = (User) event.getUI().getSession().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        if (locale == null || (user != null && StringUtils.isNotBlank(user.getLanguage()) && !user.getLanguage().equalsIgnoreCase(locale.getLanguage()))) {
            setLanguage(event.getUI());
        }
    }

    private void setLanguage(UI ui) {
        Locale locale;
        User user = (User) ui.getSession().getAttribute(MyVaadinSession.SessionVariables.USER.toString());

        if (user == null || StringUtils.isBlank(user.getLanguage())) {
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
            locale = Locale.forLanguageTag(user.getLanguage());
        }

        ui.setLocale(locale);
    }
}

