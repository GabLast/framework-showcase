package com.showcase.application.models.security;

import com.showcase.application.models.Base;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Audited
//@AuditOverride(forClass = Base.class)
public class ProfilePermit extends Base {

    @ManyToOne(fetch =  FetchType.LAZY)
    private Profile profile;
    @ManyToOne(fetch =  FetchType.LAZY)
    private Permit permit;
}
