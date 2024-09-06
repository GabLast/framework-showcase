package com.showcase.application.config.security;

import com.showcase.application.models.security.User;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Data
@NoArgsConstructor
@ToString
@Component
@VaadinSessionScope
public class VaadinSession {

    public enum SessionVariables {
        USER("VAADINUSER");

        private final String name;

        SessionVariables(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private User user;
}
