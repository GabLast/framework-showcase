package com.showcase.application.models.rest.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.rest.RequestFrame;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnTestType {
    private List<TestTypeRest> list = new ArrayList<>();
    private RequestFrame requestFrame = new RequestFrame();
}
