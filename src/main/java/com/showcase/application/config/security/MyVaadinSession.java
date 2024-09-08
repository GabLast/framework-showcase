package com.showcase.application.config.security;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.User;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@ToString
@Component
@VaadinSessionScope
public class MyVaadinSession {

    public enum SessionVariables {
        USER("VAADINUSER"),
        USERSETTINGS("USERSETTINGS");

        private final String name;

        SessionVariables(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    private User user;
    private UserSetting userSetting;
}
