package com.showcase.application.simplerestclient.models.dto.response.testdata;

import com.showcase.application.simplerestclient.models.dto.JsonBase;
import com.showcase.application.simplerestclient.models.dto.ResponseFrame;
import com.showcase.application.simplerestclient.models.dto.request.testdata.TestData;
import com.showcase.application.simplerestclient.models.dto.response.BaseResponse;

import java.util.List;

public record TestDataFindAllResponse(
        List<TestData> data,
        ResponseFrame responseFrame
) implements BaseResponse, JsonBase {
}
