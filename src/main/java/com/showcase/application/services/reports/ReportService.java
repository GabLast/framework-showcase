package com.showcase.application.services.reports;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.StringExpression;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.reports.ReportTestData;
import com.showcase.application.models.security.User;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.apache.commons.lang3.StringUtils;
import org.jxls.builder.JxlsStreaming;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    public static final Integer EXCEL = 0;
    public static final Integer CSV = 0;
    public static final Integer PDF = 1;
    private final String JASPER_SUFFIX = ".jasper";
    private final String JASPER_PATH = "/templates/jasper/";


    private final TranslationProvider translationProvider;
    private final TestDataService testDataService;

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateTestDataReport(User user, UserSetting userSetting, Integer reportType,
                                                        String word, String description, TestType testType, LocalDate dateStart, LocalDate dateEnd
    ) {
        try {
            Date currentDate = new Date();

            Date start = dateStart != null ? Date.from(dateStart.atStartOfDay().atZone(userSetting.getTimezone().toZoneId()).toInstant()) : null;
            Date end = dateStart != null ? Date.from(dateEnd.atTime(LocalTime.MAX).atZone(userSetting.getTimezone().toZoneId()).toInstant()) : null;
            String range = "";
            if (start != null) {
                range += translationProvider.getTranslation("daterange.from", userSetting.getLocale(),
                        Utilities.formatDate(start, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()));
            }

            if (end != null) {
                range += " ";
                range += translationProvider.getTranslation("daterange.to", userSetting.getLocale(),
                        Utilities.formatDate(end, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //data for report
            List<ReportTestData> data = new ArrayList<>();
            //tmp data to be transformed into report class
            List<TestData> tmp = testDataService.findAllFilter(
                    true, userSetting.getTimeZoneString(),
                    word, description, testType,
                    dateStart, dateEnd,
                    testDataService.countAllFilter(true, userSetting.getTimeZoneString(), word, description, testType, dateStart, dateEnd),
                    0,
                    null
            );
            tmp.forEach(it -> data.add(new ReportTestData(it, userSetting)));

            AbstractColumn wordCol = ColumnBuilder.getNew()
                    .setColumnProperty("word", String.class)
                    .setTitle(translationProvider.getTranslation("tab.testdata.word", userSetting.getLocale()))
                    .setWidth(ReportUtilities.NORMAL_WIDTH)
                    .setHeaderStyle(ReportUtilities.prettyColumnHeaderStyle)
                    .build();

            AbstractColumn dateCol = ColumnBuilder.getNew()
                    .setColumnProperty("date", String.class)
                    .setTitle(translationProvider.getTranslation("tab.testdata.date", userSetting.getLocale()))
                    .setWidth(ReportUtilities.DATE_WIDTH)
                    .setHeaderStyle(ReportUtilities.prettyColumnHeaderStyle)
                    .build();

            DynamicReportBuilder reportBuilder = new FastReportBuilder()
                    .setDefaultStyles(ReportUtilities.titleStyle, null, ReportUtilities.prettyColumnHeaderStyle,  ReportUtilities.detailsStyle)
                    .addColumn(wordCol)
                    .addColumn(dateCol)
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("testType", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.testtype", userSetting.getLocale()))
                            .setWidth(50)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("description", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.description", userSetting.getLocale()))
                            .setWidth(350)
                            .build())
                    .setTitle(translationProvider.getTranslation("report", userSetting.getLocale(), translationProvider.getTranslation("title.testdata", userSetting.getLocale())))
                    .addAutoText(ReportUtilities.generateAutoText(user != null ? translationProvider.getTranslation("createdby", userSetting.getLocale()) + ": " + user.getName() : "-", AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 200, 0, ReportUtilities.prettyPageHeadersStyle))
                    .addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("creationdate", userSetting.getLocale()) + ": " + Utilities.formatDate(currentDate, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()), AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 200, 0,  ReportUtilities.prettyPageHeadersStyle));

            if (!StringUtils.isBlank(range)) {
                reportBuilder.addAutoText(ReportUtilities.generateAutoText(range, AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, ReportUtilities.prettyColumnHeaderStyle));
            }

            reportBuilder.setPageSizeAndOrientation(Page.Page_Letter_Landscape())
                    .setPrintColumnNames(true)
                    .setUseFullPageWidth(true)
                    .setPrintBackgroundOnOddRows(true)
                    .setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
                    .setReportLocale(userSetting.getLocale())
                    .setMargins(10, 10, 10, 10)
                    .setWhenNoData(translationProvider.getTranslation("empty", userSetting.getLocale()), new Style())
                    .setGrandTotalLegend(" ")
                    .addGlobalFooterVariable(wordCol, new StringExpression() {
                        @Override
                        public Object evaluate(Map map, Map map1, Map map2) {
                            return translationProvider.getTranslation("total.rows", userSetting.getLocale()) + ":";
                        }
                    })
                    .addGlobalFooterVariable(dateCol, new StringExpression() {
                        @Override
                        public Object evaluate(Map map, Map map1, Map map2) {
                            return data.size();
                        }
                    })
            ;

            DynamicReport report = reportBuilder.build();
            JasperPrint print = DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), data);

            if (reportType.equals(PDF)) {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.exportReport();

                outputStream.flush();
                outputStream.close();
            } else if (reportType.equals(EXCEL)) {
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.exportReport();

                outputStream.flush();
                outputStream.close();
            }

            return outputStream;
        } catch (MyException e) {
            log.error(e.getMessage());
            throw e;
        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<ByteArrayOutputStream> generateTestDataReport(User user, UserSetting userSetting,
                                                              String word, String description, TestType testType, LocalDate dateStart, LocalDate dateEnd
    ) {
        try {
            Date currentDate = new Date();

            Date start = dateStart != null ? Date.from(dateStart.atStartOfDay().atZone(userSetting.getTimezone().toZoneId()).toInstant()) : null;
            Date end = dateStart != null ? Date.from(dateEnd.atTime(LocalTime.MAX).atZone(userSetting.getTimezone().toZoneId()).toInstant()) : null;

            String range = "";
            if (start != null) {
                range += translationProvider.getTranslation("daterange.from", userSetting.getLocale(),
                        Utilities.formatDate(start, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()));
            }

            if (end != null) {
                range += " ";
                range += translationProvider.getTranslation("daterange.to", userSetting.getLocale(),
                        Utilities.formatDate(end, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()));
            }

            //data for report
            List<ReportTestData> data = new ArrayList<>();
            //tmp data to be transformed into report class
            List<TestData> tmp = testDataService.findAllFilter(
                    true, userSetting.getTimeZoneString(),
                    word, description, testType,
                    dateStart, dateEnd,
                    testDataService.countAllFilter(true, userSetting.getTimeZoneString(), word, description, testType, dateStart, dateEnd),
                    0,
                    Sort.by(Sort.Direction.ASC, "id")
            );
            tmp.forEach(it -> data.add(new ReportTestData(it, userSetting)));

            AbstractColumn wordCol = ColumnBuilder.getNew()
                    .setColumnProperty("word", String.class)
                    .setTitle(translationProvider.getTranslation("tab.testdata.word", userSetting.getLocale()))
                    .setWidth(ReportUtilities.NORMAL_WIDTH)
                    .setHeaderStyle(ReportUtilities.prettyColumnHeaderStyle)
                    .build();

            AbstractColumn dateCol = ColumnBuilder.getNew()
                    .setColumnProperty("date", String.class)
                    .setTitle(translationProvider.getTranslation("tab.testdata.date", userSetting.getLocale()))
                    .setWidth(ReportUtilities.DATE_WIDTH)
                    .setHeaderStyle(ReportUtilities.prettyColumnHeaderStyle)
                    .build();

            DynamicReportBuilder reportBuilder = new FastReportBuilder()
                    .setDefaultStyles(ReportUtilities.titleStyle, null, ReportUtilities.prettyColumnHeaderStyle,  ReportUtilities.detailsStyle)
                    .addColumn(wordCol)
                    .addColumn(dateCol)
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("testType", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.testtype", userSetting.getLocale()))
                            .setWidth(50)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("description", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.description", userSetting.getLocale()))
                            .setWidth(350)
                            .build())
                    .setTitle(translationProvider.getTranslation("report", userSetting.getLocale(), translationProvider.getTranslation("title.testdata", userSetting.getLocale())))
                    .addAutoText(ReportUtilities.generateAutoText(user != null ? translationProvider.getTranslation("createdby", userSetting.getLocale()) + ": " + user.getName() : "-", AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 200, 0, ReportUtilities.prettyPageHeadersStyle))
                    .addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("creationdate", userSetting.getLocale()) + ": " + Utilities.formatDate(currentDate, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()), AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 200, 0,  ReportUtilities.prettyPageHeadersStyle));

            if (!StringUtils.isBlank(range)) {
                reportBuilder.addAutoText(ReportUtilities.generateAutoText(range, AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, ReportUtilities.prettyColumnHeaderStyle));
            }

            reportBuilder.setPageSizeAndOrientation(Page.Page_Letter_Landscape())
                    .setPrintColumnNames(true)
                    .setUseFullPageWidth(true)
                    .setPrintBackgroundOnOddRows(true)
                    .setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
                    .setReportLocale(userSetting.getLocale())
                    .setMargins(10, 10, 10, 10)
                    .setWhenNoData(translationProvider.getTranslation("empty", userSetting.getLocale()), new Style())
                    .setGrandTotalLegend(" ")
                    .addGlobalFooterVariable(wordCol, new StringExpression() {
                        @Override
                        public Object evaluate(Map map, Map map1, Map map2) {
                            return translationProvider.getTranslation("total.rows", userSetting.getLocale()) + ":";
                        }
                    })
                    .addGlobalFooterVariable(dateCol, new StringExpression() {
                        @Override
                        public Object evaluate(Map map, Map map1, Map map2) {
                            return data.size();
                        }
                    })
            ;


            DynamicReport report = reportBuilder.build();
            JasperPrint print = DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), data);

            //
            List<ByteArrayOutputStream> list = new ArrayList<>();

            //EXCEL
            ByteArrayOutputStream csv = new ByteArrayOutputStream();
            JRCsvExporter csvExporter = new JRCsvExporter();
            csvExporter.setExporterOutput(new SimpleWriterExporterOutput(csv));
            csvExporter.setExporterInput(new SimpleExporterInput(print));
            csvExporter.exportReport();
            csv.flush();
            csv.close();
            list.add(csv);

            //PDF
            ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdf));
            pdfExporter.setExporterInput(new SimpleExporterInput(print));
            pdfExporter.exportReport();
            pdf.flush();
            pdf.close();
            list.add(pdf);

            return list;
        } catch (MyException e) {
            log.error(e.getMessage());
            throw e;
        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<ByteArrayOutputStream> generateReportFromTemplate(User user, UserSetting userSetting,
                                                                  String word, String description, TestType testType, LocalDate dateStart, LocalDate dateEnd
    ) {
        try {
            Date currentDate = new Date();

            Date start = dateStart != null ? Date.from(dateStart.atStartOfDay().atZone(userSetting.getTimezone().toZoneId()).toInstant()) : null;
            Date end = dateStart != null ? Date.from(dateEnd.atTime(LocalTime.MAX).atZone(userSetting.getTimezone().toZoneId()).toInstant()) : null;
            String range = "";
            if (start != null) {
                range += translationProvider.getTranslation("daterange.from", userSetting.getLocale(),
                        Utilities.formatDate(start, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()));
            }

            if (end != null) {
                range += " ";
                range += translationProvider.getTranslation("daterange.to", userSetting.getLocale(),
                        Utilities.formatDate(end, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()));
            }


            //data for report
            List<ReportTestData> data = new ArrayList<>();
            //tmp data to be transformed into report class
            List<TestData> tmp = testDataService.findAllFilter(
                    true, userSetting.getTimeZoneString(),
                    word, description, testType,
                    dateStart, dateEnd,
                    testDataService.countAllFilter(true, userSetting.getTimeZoneString(), word, description, testType, dateStart, dateEnd),
                    0,
                    Sort.by(Sort.Direction.ASC, "id")
            );
            tmp.forEach(it -> data.add(new ReportTestData(it, userSetting)));

            Map<String, Object> params = new HashMap<>();
            params.put("page", translationProvider.getTranslation("page", userSetting.getLocale()));
            params.put("of", translationProvider.getTranslation("of", userSetting.getLocale()));
            params.put("range", range);
            params.put("logo", GlobalConstants.LOGO);
            params.put("creationDate", translationProvider.getTranslation("creationdate", userSetting.getLocale()) + ": " + Utilities.formatDate(currentDate, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()));
            params.put("user", user != null ? translationProvider.getTranslation("createdby", userSetting.getLocale()) + ": " + user.getName() : translationProvider.getTranslation("createdby", userSetting.getLocale()) + ": -");
            params.put("title", translationProvider.getTranslation("report", userSetting.getLocale(), translationProvider.getTranslation("title.testdata", userSetting.getLocale())));
            params.put("total", translationProvider.getTranslation("total.rows", userSetting.getLocale()));
            params.put("wordCol", translationProvider.getTranslation("tab.testdata.word", userSetting.getLocale()));
            params.put("typeCol", translationProvider.getTranslation("tab.testdata.testtype", userSetting.getLocale()));
            params.put("dateCol", translationProvider.getTranslation("tab.testdata.word", userSetting.getLocale()));
            params.put("descriptionCol", translationProvider.getTranslation("tab.testdata.word", userSetting.getLocale()));
            params.put("data", data);

            List<ByteArrayOutputStream> list = new ArrayList<>();

            //EXCEL
//            https://jxls.sourceforge.net/migration-to-v3-0.html
//            https://jxls.sourceforge.net/each.html
            byte[] tmpBytes = JxlsPoiTemplateFillerBuilder.newInstance()
                    .withTemplate(getClass().getResourceAsStream(ExcelReports.REPORT_TEMPLATE))
                    .withStreaming(JxlsStreaming.STREAMING_ON)
                    .buildAndFill(params);
            ByteArrayOutputStream excel = new ByteArrayOutputStream(tmpBytes.length);
            excel.write(tmpBytes, 0, tmpBytes.length);
            excel.flush();
            excel.close();
            list.add(excel);

            //PDF
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
            String reportName = "reportTemplate";
            JasperPrint jasperPrintPDF = JasperFillManager.fillReport(getClass().getResourceAsStream(JASPER_PATH + reportName + JASPER_SUFFIX), params, dataSource);

            ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrintPDF));
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdf));
            pdfExporter.exportReport();
            pdf.flush();
            pdf.close();
            list.add(pdf);

            return list;
        } catch (MyException e) {
            log.error(e.getMessage());
            throw e;
        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
