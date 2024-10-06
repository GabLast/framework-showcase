package com.showcase.application.models.security;

import com.showcase.application.models.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@NamedEntityGraph(name = "Token.all", includeAllAttributes = true)
public class Token extends Base {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(unique = true)
    private String token;

    private Date expirationDate;
}
