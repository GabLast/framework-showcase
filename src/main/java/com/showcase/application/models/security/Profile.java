package com.showcase.application.models.security;

import com.showcase.application.models.Base;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@Audited
//@AuditOverride(forClass = Base.class)
public class Profile extends Base {

    private String name;
    private String description;
}
