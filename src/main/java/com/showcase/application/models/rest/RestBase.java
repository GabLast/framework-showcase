package com.showcase.application.models.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.rest.security.UserRest;
import com.showcase.application.models.security.User;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestBase {
    private UserRest user;
    private UserSetting userSetting;
}
