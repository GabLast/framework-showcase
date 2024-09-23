package com.showcase.application.models.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.rest.dto.UserSettingDto;
import com.showcase.application.models.rest.security.UserRest;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestBase {
    private UserRest user;
    private UserSettingDto userSetting;
}
