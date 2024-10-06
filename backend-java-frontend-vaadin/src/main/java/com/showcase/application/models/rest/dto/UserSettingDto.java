package com.showcase.application.models.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.utils.TranslationProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.TimeZone;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSettingDto {

    private Long id;
    private String timeZoneString = "America/Santo_Domingo";
    private boolean darkMode = false;
    private String dateFormat = "dd/MM/yy";
    private String dateTimeFormat = "dd/MM/yyyy hh:mm a";
    private String language = "en";

    public UserSettingDto(UserSetting userSetting) {
        this.id = userSetting.getId();
        this.timeZoneString = userSetting.getTimeZoneString();
        this.darkMode = userSetting.isDarkMode();
        this.dateFormat = userSetting.getDateFormat();
        this.language = userSetting.getLanguage();
    }

    public Locale getLocale() {
        Locale locale = Locale.forLanguageTag(StringUtils.isBlank(this.language) ? "es" : this.language);
        if(locale == null) {
            locale = TranslationProvider.ENGLISH;
        }
        return locale;
    }

    public TimeZone getTimezone() {
        TimeZone timeZone = TimeZone.getTimeZone(this.timeZoneString);
        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return timeZone;
    }
}
