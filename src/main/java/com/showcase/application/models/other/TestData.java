package com.showcase.application.models.other;


import com.showcase.application.models.Base;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TestData extends Base {
    private String word;
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    private TestType testType;
}
