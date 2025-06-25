package com.showcase.application.simplerestclient.models.dto.response.testdata;

import com.showcase.application.simplerestclient.models.dto.JsonBase;
import com.showcase.application.simplerestclient.models.dto.RequestFrame;
import com.showcase.application.simplerestclient.models.dto.request.testdata.TestData;
import com.showcase.application.simplerestclient.models.dto.response.BaseResponse;

import java.util.List;

public record TestDataResponse(
        List<TestData> data,
        RequestFrame requestFrame
) implements BaseResponse, JsonBase {
}
