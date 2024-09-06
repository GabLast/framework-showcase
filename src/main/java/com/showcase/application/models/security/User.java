package com.showcase.application.models.security;

import com.showcase.application.models.Base;
import com.showcase.application.utils.TranslationProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@Audited
//@AuditOverride(forClass = Base.class)
@Table(name = "user_class")
public class User extends Base {

    @Column(unique = true)
    private String username;
    private String password;

    @Column(unique = true)
    private String mail;

    private String language;
    private boolean lightMode = true;

    private boolean admin = false;

    public Locale getLocale() {
        Locale locale = Locale.forLanguageTag(this.language);
        if(locale == null) {
            locale = TranslationProvider.ENGLISH;
        }
        return locale;
    }
}
