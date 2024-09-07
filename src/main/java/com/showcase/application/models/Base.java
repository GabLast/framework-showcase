package com.showcase.application.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
public abstract class Base implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id = 0L;

    @CreatedBy
    protected String createdBy = "system";

    @LastModifiedBy
    protected String modifiedBy = "system";

    @CreatedDate
    protected Date dateCreated = new Date();

    @LastModifiedDate
    protected Date lastUpdated = new Date();

    @Version
    protected Long version;

    protected boolean enabled = true;

}
