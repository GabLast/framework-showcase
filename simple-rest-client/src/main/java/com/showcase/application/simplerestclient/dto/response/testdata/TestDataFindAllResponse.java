package com.showcase.application.simplerestclient.dto.response.testdata;

import com.showcase.application.simplerestclient.dto.JsonBase;
import com.showcase.application.simplerestclient.dto.ResponseFrame;
import com.showcase.application.simplerestclient.dto.request.testdata.TestData;
import com.showcase.application.simplerestclient.dto.response.BaseResponse;

import java.util.List;

public record TestDataFindAllResponse(
        List<TestData> data,
        ResponseFrame responseFrame
) implements BaseResponse, JsonBase {
}
