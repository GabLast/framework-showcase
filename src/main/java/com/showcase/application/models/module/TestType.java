package com.showcase.application.models.module;

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
//@Audited
//@AuditOverride(forClass = Base.class)
public class TestType extends Base {
    @Getter
    @AllArgsConstructor
    public enum TestTypeCode {
        TYPE_1(1, "Type 1", ""),
        TYPE_2(2, "Type 2", ""),
        TYPE_3(3, "Type 3", "");

        private final Integer code;
        private final String name;
        private final String description;
    }

    private Integer code;
    private String name;
    private String description;

    public String toString() {
        return name;
    }

    public static String toStringI18nKey(TestType testType) {
        if (testType == null) {
            return "empty";
        }

        String typeName = testType.getName();
        if (testType.getCode().equals(TestType.TestTypeCode.TYPE_1.getCode())) {
            typeName = "type1";
        } else if (testType.getCode().equals(TestType.TestTypeCode.TYPE_2.getCode())) {
            typeName = "type2";
        } else if (testType.getCode().equals(TestType.TestTypeCode.TYPE_3.getCode())) {
            typeName = "type3";
        }
        return typeName;
    }
}
