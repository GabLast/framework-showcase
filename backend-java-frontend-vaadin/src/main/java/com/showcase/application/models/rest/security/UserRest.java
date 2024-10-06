package com.showcase.application.models.rest.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.security.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRest {
    private Long id;
    private String username;
    private String name;

    public UserRest(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
    }
}
