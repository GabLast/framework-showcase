package com.showcase.application.models.configuration;

import com.showcase.application.models.Base;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parameter extends Base {

    public static final String REMEMBER_ME_TOKEN = "REMEMBER_ME_TOKEN";

    private String code;
    private String nameI18;
    private String value;
}
