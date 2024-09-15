package com.showcase.application.controller;


import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.rest.RequestFrame;
import com.showcase.application.models.rest.RestBase;
import com.showcase.application.models.rest.RestRequestGet;
import com.showcase.application.models.rest.module.*;
import com.showcase.application.services.configuration.ParameterService;
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

    private final TranslationProvider translationProvider;
    private final ParameterService parameterService;
    private final TestDataService testDataService;
    private final TestTypeService testTypeService;
    private final ReportService reportService;

    //TODO JWT and/or integration with spring security
//    Claims claims = Jwts.parser()
//            .verifyWith(Keys.hmacShaKeyFor("".getBytes()))
//            .build()
//            .parseSignedClaims("receivedJWT")
//            .getPayload();
//
//    FilterTestData fromJason = new Gson().fromJson(claims.get("data").toString(), FilterTestData.class);
//
//    String jwt = Jwts.builder()
//            .issuer("Framework-Showcase-App")
//            .subject("data")
//            .claim("data", filterTestData)
//            .signWith(Utilities.generateJWTKey(parameterService.findFirstByEnabledAndCode(true, Parameter.JWT_KEY).getValue()))
//            .compact();

    @GetMapping("/findall")
    public ResponseEntity<?> findAllFilter(@RequestBody() FilterTestData filterTestData) {
        RequestFrame requestFrame = new RequestFrame();
        ReturnTestData returnData = new ReturnTestData();

        try {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(false);

            List<TestData> list = testDataService.findAllFilter(
                    true,
                    filterTestData.getUserSetting().getTimeZoneString(),
                    filterTestData.getWord(),
                    filterTestData.getDescription(),
                    filterTestData.getTestType(filterTestData.getTestTypeRest()),
                    filterTestData.getDateStart(),
                    filterTestData.getDateEnd(),
                    filterTestData.getRestPagination().getOffset(),
                    filterTestData.getRestPagination().getLimit(),
                    Sort.by(Sort.Direction.DESC, filterTestData.getRestPagination().getSortProperty()));

            for (TestData it : list) {
                returnData.getList().add(new TestDataRest(it));
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

            TestData data = testDataService.getTestDataById(restRequestGet.getId()).orElse(null);
            if (data == null) {
                throw new MyException(HttpStatus.OK.value(), translationProvider.getTranslation("error.null", restRequestGet.getUserSetting().getLocale()));
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
    public ResponseEntity<?> save(@RequestBody() TestDataRest testDataRest) {
        RequestFrame requestFrame = new RequestFrame();
        ReturnTestData returnData = new ReturnTestData();

        try {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(false);

            TestData data = testDataService.saveTestData(new TestData(testDataRest), testDataRest.getUserSetting());

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
    public ResponseEntity<?> findAllTestType(@RequestBody() RestBase restBase) {
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

        ByteArrayOutputStream byteArrayOutputStream = reportService.generateTestDataReport(
                filterTestData.getUser(),
                filterTestData.getUserSetting(),
                ReportService.PDF,
                filterTestData.getWord(),
                filterTestData.getDescription(),
                filterTestData.getTestType(filterTestData.getTestTypeRest()),
                filterTestData.getDateStart(),
                filterTestData.getDateEnd()
        );

        //Headers to set it as pdf
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/pdf");

        return new ResponseEntity<Resource>(
                new ByteArrayResource(byteArrayOutputStream.toByteArray(), translationProvider.getTranslation("report.testdata", filterTestData.getUserSetting().getLocale())),
                headers,
                HttpStatus.OK
        );
    }
}
