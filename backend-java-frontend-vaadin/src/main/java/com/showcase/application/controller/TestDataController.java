package com.showcase.application.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.rest.ResponseFrame;
import com.showcase.application.models.rest.RestRequestGet;
import com.showcase.application.models.rest.dto.TestDataDto;
import com.showcase.application.models.rest.module.FilterTestData;
import com.showcase.application.models.rest.module.ReturnTestData;
import com.showcase.application.models.rest.module.ReturnTestType;
import com.showcase.application.models.rest.module.TestDataRest;
import com.showcase.application.models.rest.module.TestTypeRest;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.ParameterService;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.services.module.TestTypeService;
import com.showcase.application.services.reports.ReportService;
import com.showcase.application.utils.TranslationProvider;
import com.showcase.application.utils.exceptions.MyException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@RestController()
@CrossOrigin(maxAge = 1800, origins = "*")
@RequestMapping("rest/testdata")
@RequiredArgsConstructor
public class TestDataController {

    private final UserSettingService userSettingService;
    private final TranslationProvider translationProvider;
    private final ParameterService parameterService;
    private final TestDataService testDataService;
    private final TestTypeService testTypeService;
    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    //since this api doesnt catch the exception, it returns the
    //error code in the RestApiAdvise for http code UNAUTHORIZED
    @GetMapping("/findall")
    public ResponseEntity<?> findAllFilter(FilterTestData filterTestData) {
        ResponseFrame responseFrame = new ResponseFrame();
        ReturnTestData returnData = new ReturnTestData();

        responseFrame.setCode(HttpStatus.OK.value());
        responseFrame.setError(false);

//        try {
//            System.out.println("Filter received:" + objectMapper.writeValueAsString(filterTestData));
//        } catch (JsonProcessingException ignored) {
//        }

        UserSetting userSetting = (UserSetting) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (!SecurityUtils.isAccessGranted(Permit.MENU_TEST_DATA)) {
            throw new MyException(HttpStatus.UNAUTHORIZED.value(), translationProvider.getTranslation("error.noaccess", userSetting.getLocale()));
        }

        List<TestData> list = testDataService.findAllFilter(
                true,
                userSetting.getTimeZoneString(),
                filterTestData.getWord(),
                filterTestData.getDescription(),
                null,
                filterTestData.getTestType(),
                filterTestData.getDateStart(),
                filterTestData.getDateEnd(),
                filterTestData.getLimit(),
                filterTestData.getOffset(),
                Sort.by(Sort.Direction.DESC, filterTestData.getSortProperty()));

        for (TestData it : list) {
            TestDataRest tmp = new TestDataRest(it);
            tmp.getTestTypeRest().setName(translationProvider.getTranslation(tmp.getTestTypeRest().getName(), userSetting.getLocale()));
            returnData.getData().add(tmp);
        }
        returnData.setResponseFrame(responseFrame);

        return new ResponseEntity<>(returnData, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestBody() RestRequestGet restRequestGet) {
        ResponseFrame responseFrame = new ResponseFrame();
        ReturnTestData returnData = new ReturnTestData();

        try {
            responseFrame.setCode(HttpStatus.OK.value());
            responseFrame.setError(false);

            UserSetting userSetting = (UserSetting) SecurityContextHolder.getContext().getAuthentication().getDetails();

            //these checks can be removed thanks to using TestDataAdvise
            if (!SecurityUtils.isAccessGranted(Permit.TEST_DATA_CREATE) ||
                    !SecurityUtils.isAccessGranted(Permit.TEST_DATA_VIEW) ||
                    !SecurityUtils.isAccessGranted(Permit.TEST_DATA_EDIT)) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.noaccess", userSetting.getLocale()));
            }

            TestData data = testDataService.getTestDataById(restRequestGet.getId()).orElse(null);
            if (data == null) {
                throw new MyException(HttpStatus.OK.value(), translationProvider.getTranslation("error.notfound", userSetting.getLocale()));
            }

            returnData.getData().add(new TestDataRest(data));
            returnData.setResponseFrame(responseFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            responseFrame.setCode(e.getCode());
            responseFrame.setError(true);
            responseFrame.setMessage(e.getMessage());
            returnData.setResponseFrame(responseFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody() TestDataDto testDataDto) {
        ResponseFrame responseFrame = new ResponseFrame();
        ReturnTestData returnData = new ReturnTestData();

        try {
            responseFrame.setCode(HttpStatus.OK.value());
            responseFrame.setError(false);

            UserSetting userSetting = (UserSetting) SecurityContextHolder.getContext().getAuthentication().getDetails();

            if (!SecurityUtils.isAccessGranted(Permit.TEST_DATA_CREATE)) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.noaccess", userSetting.getLocale()));
            }

            TestData data = null;

            Optional<TestData> getValue = testDataService.getTestDataById(testDataDto.getId());
            if (getValue.isPresent()) {
                data = getValue.get();
                data.setWord(testDataDto.getWord());
                data.setDate(testDataDto.getDate());
                data.setDescription(testDataDto.getDescription());
                data.setTestTypeId(testDataDto.getTestTypeId());
                data.setNumber(testDataDto.getNumber());
            } else {
                data = new TestData(testDataDto);
            }

//            try {
//                System.out.printf("Received Data1: " +  objectMapper.writeValueAsString(testDataDto));
//                System.out.printf("Received Data2: " +  objectMapper.writeValueAsString(data));
//            } catch (JsonProcessingException ignored) {
//
//            }

            data = testDataService.saveTestData(data, userSetting);

            returnData.getData().add(new TestDataRest(data));
            returnData.setResponseFrame(responseFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            responseFrame.setCode(e.getCode());
            responseFrame.setError(true);
            responseFrame.setMessage(e.getMessage());
            returnData.setResponseFrame(responseFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }

    @GetMapping("/testtype/findall")
    public ResponseEntity<?> findAllTestType() {
        //can be called as long as the user is authenticated, so it doesnt need any permit
        ResponseFrame responseFrame = new ResponseFrame();
        ReturnTestType returnData = new ReturnTestType();

        try {
            responseFrame.setCode(HttpStatus.OK.value());
            responseFrame.setError(false);

            List<TestType> list = testTypeService.findAllByEnabled(true);

            for (TestType it : list) {
                returnData.getData().add(new TestTypeRest(it));
            }

            returnData.setResponseFrame(responseFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            responseFrame.setCode(e.getCode());
            responseFrame.setError(true);
            responseFrame.setMessage(e.getMessage());
            returnData.setResponseFrame(responseFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }

    //generate reports as rest
    @ResponseBody
    @GetMapping(value = "/report")
    public ResponseEntity<?> generateReportPDF(@RequestBody() FilterTestData filterTestData) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserSetting userSetting = (UserSetting) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (!SecurityUtils.isAccessGranted(Permit.REPORT_TEST_DATA)) {
            throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.noaccess", userSetting.getLocale()));
        }

        ByteArrayOutputStream byteArrayOutputStream = reportService.generateTestDataReport(
                user,
                userSetting,
                ReportService.PDF,
                filterTestData.getWord(),
                filterTestData.getDescription(),
                null,
                filterTestData.getDateStart(),
                filterTestData.getDateEnd()
        );

        //Headers to set it as pdf
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/pdf");

        return new ResponseEntity<Resource>(
                new ByteArrayResource(byteArrayOutputStream.toByteArray(), translationProvider.getTranslation("report.testdata", userSetting.getLocale())),
                headers,
                HttpStatus.OK
        );
    }
}
