package com.showcase.application.models.security;

import com.showcase.application.models.Base;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@Entity
public class Permit extends Base {

    private Long code;
    @Column(unique = true)
    private String name;
    private String description;
}
