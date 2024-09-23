package com.showcase.application.models.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.Base;
import com.showcase.application.models.security.User;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.utils.TranslationProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.TimeZone;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@Audited
//@AuditOverride(forClass = Base.class)
//@NamedEntityGraph(name = "UserSetting.name", attributeNodes = {
//        @NamedAttributeNode("user"),
///*        @NamedAttributeNode("otherField"),
//        @NamedAttributeNode("otherField"),
//        @NamedAttributeNode("otherField")*/
//})
//For objects inside objects:
//@NamedEntityGraph(name = "UserSetting.name", attributeNodes = {
//        @NamedAttributeNode(value = "user", subgraph = "user-subgraph"),
//        @NamedAttributeNode("otherField"),
//}, subgraphs = {@NamedSubgraph(name = "user-subgraph", attributeNodes = {
//        @NamedAttributeNode("userField"),
//})})
public class UserSetting extends Base {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false)
    private String timeZoneString = "America/Santo_Domingo";
    @Column(nullable = false)
    private boolean darkMode = false;
    @Column(nullable = false)
    private String dateFormat = "dd/MM/yy";
    @Column(nullable = false)
    private String dateTimeFormat = "dd/MM/yyyy hh:mm a";
    @Column(nullable = false)
    private String language = "en";

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
