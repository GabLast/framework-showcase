package com.showcase.application.models.other;

import com.showcase.application.models.Base;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class TestType extends Base {
    @Getter
    @AllArgsConstructor
    public enum TestTypeCode {
        TYPE_1(1, "Received Agreement", ""),
        TYPE_2(2, "Partial Payment", ""),
        TYPE_3(3, "Full Payment", "");

        private final Integer code;
        private final String name;
        private final String description;
    }

    private Integer code;
    private String name;
    private String description;
}
