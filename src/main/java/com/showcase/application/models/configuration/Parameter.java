package com.showcase.application.models.configuration;

import com.showcase.application.models.Base;
import jakarta.persistence.Column;
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
    public static final String JWT_KEY = "JWT_KEY";

    @Column(nullable = false)
    private String code;
    private String nameI18;
    @Column(nullable = false)
    private String value;
}
