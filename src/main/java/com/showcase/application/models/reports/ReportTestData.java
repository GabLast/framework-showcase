package com.showcase.application.models.reports;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.utils.Utilities;
import com.vaadin.flow.component.UI;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ReportTestData {

    private String word;
    private String date;
    private String testType;
    private String description;

    public ReportTestData(TestData testData, UserSetting userSetting) {
        this.word = testData.getWord();
        this.date = Utilities.formatDate(testData.getDate(), userSetting.getDateFormat(), userSetting.getTimeZoneString());
        this.testType = UI.getCurrent().getTranslation(TestType.toStringI18nKey(testData.getTestType()));
        this.description = testData.getDescription();
    }
}
