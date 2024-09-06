package com.showcase.application.utils;

import com.vaadin.flow.i18n.I18NProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
@Slf4j
public class TranslationProvider implements I18NProvider {
    public static final Locale ESPANOL = Locale.forLanguageTag("es");
    public static final Locale ENGLISH = Locale.ENGLISH;

    private Map<String, ResourceBundle> locales;

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(ENGLISH, ESPANOL);
    }

    @PostConstruct
    public void bootstrap() {
        locales = new HashMap<>();

        // Read translations file for each locale
        for (final Locale locale : getProvidedLocales()) {
            final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
            locales.put(locale.getLanguage(), resourceBundle);
        }
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        String translation = null;
        try {
            translation = locales.get(locale.getLanguage()).getString(key);
            return MessageFormat.format(translation, params);
        } catch (final MissingResourceException e) {
            // Translation not found, return error message instead of null as per API
            System.out.printf("No translation found for key {%s}%n", key);
            return String.format("!{%s}", key);
        } catch (final IllegalArgumentException e) {
            log.error(e.getMessage(), e); // for devs to find where this happened
            // Incorrect parameters
            return translation;
        }
    }

}
