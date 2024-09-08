package com.showcase.application.models.security;

import com.showcase.application.models.Base;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
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
public class Permit extends Base {

    public static final String MENU_TEST_DATA = "MENU_TEST_DATA";
    public static final String TEST_DATA_CREATE = "TEST_DATA_CREATE";
    public static final String TEST_DATA_EDIT = "TEST_DATA_EDIT";
    public static final String TEST_DATA_VIEW = "TEST_DATA_VIEW";
    public static final String TEST_DATA_DELETE = "TEST_DATA_DELETE";

    @ManyToOne(fetch = FetchType.LAZY)
    private Permit permitFather;

    private Long code;
    @Column(unique = true)
    private String name;
    private String description;
}
