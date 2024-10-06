package com.showcase.application.views.broadcaster.module;

import com.showcase.application.models.module.TestData;
import com.showcase.application.views.broadcaster.BroadcasterUI;
import lombok.Getter;

public class BroadcasterTestData extends BroadcasterUI<TestData> {

    @Getter
    private static final BroadcasterTestData instance;

    static {
        instance = new BroadcasterTestData();
    }

}
