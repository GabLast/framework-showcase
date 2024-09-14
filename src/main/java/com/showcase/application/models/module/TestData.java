package com.showcase.application.models.module;


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
//@Audited
//@AuditOverride(forClass = Base.class)
public class TestData extends Base {

    @Column(nullable = false)
    private String word;
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    private TestType testType;

    @Column(columnDefinition = "longtext")
    private String description;

    public String toString() {
        return word;
    }
}
