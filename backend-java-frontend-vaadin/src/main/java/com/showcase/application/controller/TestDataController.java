package com.showcase.application.controller;


import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.rest.RequestFrame;
import com.showcase.application.models.rest.RestRequestGet;
import com.showcase.application.models.rest.dto.TestDataDto;
import com.showcase.application.models.rest.module.*;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.ParameterService;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.services.module.TestTypeService;
import com.showcase.application.services.reports.ReportService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.TranslationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

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

    @GetMapping("/findall")
    public ResponseEntity<?> findAllFilter(FilterTestData filterTestData) {
        RequestFrame requestFrame = new RequestFrame();
        ReturnTestData returnData = new ReturnTestData();

        try {

            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(false);

//            System.out.println("Filter:" + filterTestData.toString());

            UserSetting userSetting = (UserSetting) SecurityContextHolder.getContext().getAuthentication().getDetails();

            if (!SecurityUtils.isAccessGranted(Permit.MENU_TEST_DATA)) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.noaccess", userSetting.getLocale()));
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
                returnData.getList().add(tmp);
            }
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(true);
            requestFrame.setMessage(e.getMessage());
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestBody() RestRequestGet restRequestGet) {
        RequestFrame requestFrame = new RequestFrame();
        ReturnTestData returnData = new ReturnTestData();

        try {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(false);

            UserSetting userSetting = (UserSetting) SecurityContextHolder.getContext().getAuthentication().getDetails();

            if (!SecurityUtils.isAccessGranted(Permit.TEST_DATA_CREATE) ||
                    !SecurityUtils.isAccessGranted(Permit.TEST_DATA_VIEW) ||
                    !SecurityUtils.isAccessGranted(Permit.TEST_DATA_EDIT)) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.noaccess", userSetting.getLocale()));
            }

            TestData data = testDataService.getTestDataById(restRequestGet.getId()).orElse(null);
            if (data == null) {
                throw new MyException(HttpStatus.OK.value(), translationProvider.getTranslation("error.notfound", userSetting.getLocale()));
            }

            returnData.getList().add(new TestDataRest(data));
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(true);
            requestFrame.setMessage(e.getMessage());
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody() TestDataDto testDataDto) {
        RequestFrame requestFrame = new RequestFrame();
        ReturnTestData returnData = new ReturnTestData();

        try {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(false);

            UserSetting userSetting = (UserSetting) SecurityContextHolder.getContext().getAuthentication().getDetails();

            if (!SecurityUtils.isAccessGranted(Permit.TEST_DATA_CREATE)) {
                throw new MyException(MyException.CLIENT_ERROR, translationProvider.getTranslation("error.noaccess", userSetting.getLocale()));
            }

            TestData data = testDataService.saveTestData(new TestData(testDataDto), userSetting);

            returnData.getList().add(new TestDataRest(data));
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(true);
            requestFrame.setMessage(e.getMessage());
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }

    @GetMapping("/testtype/findall")
    public ResponseEntity<?> findAllTestType() {
        //can be called as long as the user is authenticated, so it doesnt need any permit
        RequestFrame requestFrame = new RequestFrame();
        ReturnTestType returnData = new ReturnTestType();

        try {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(false);

            List<TestType> list = testTypeService.findAllByEnabled(true);

            for (TestType it : list) {
                returnData.getList().add(new TestTypeRest(it));
            }

            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(true);
            requestFrame.setMessage(e.getMessage());
            returnData.setRequestFrame(requestFrame);

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
