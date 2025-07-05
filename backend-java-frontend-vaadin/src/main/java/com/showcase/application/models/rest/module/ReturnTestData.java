package com.showcase.application.models.rest.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.rest.ResponseFrame;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ReturnTestData {
    private List<TestDataRest> data = new ArrayList<>();
    private ResponseFrame responseFrame = new ResponseFrame();
}
