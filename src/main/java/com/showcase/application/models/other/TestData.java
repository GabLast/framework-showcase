package com.showcase.application.models.other;


import com.showcase.application.models.Base;
import jakarta.persistence.*;
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
public class TestData extends Base {
    private String word;
    private byte[] image;

    @ManyToOne(fetch = FetchType.EAGER)
    private TestType testType;

    @Lob
    @Column(columnDefinition = "tinytext")
    private String description;

    public String toString() {
        return word;
    }
}
