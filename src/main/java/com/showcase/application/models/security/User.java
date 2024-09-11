package com.showcase.application.models.security;

import com.showcase.application.models.Base;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_class")
//@Audited
//@AuditOverride(forClass = Base.class)
public class User extends Base {

    @Column(unique = true)
    private String username;
    private String password;

    private String name;
    @Column(unique = true)
    private String mail;

    private boolean admin = false;

    public String toString() {
        return username;
    }
}
