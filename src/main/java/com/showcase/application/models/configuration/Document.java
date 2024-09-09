package com.showcase.application.models.configuration;

import com.showcase.application.models.security.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@Audited
//@AuditOverride(forClass = Base.class)
public class Document {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] file;
    private String contentType;
    private String name;
}
