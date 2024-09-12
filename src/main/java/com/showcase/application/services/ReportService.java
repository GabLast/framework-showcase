package com.showcase.application.services;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Page;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.reports.ReportTestData;
import com.showcase.application.models.security.User;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.ReportUtilities;
import com.showcase.application.utils.TranslationProvider;
import com.showcase.application.utils.Utilities;
import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    public static final Integer EXCEL = 0;
    public static final Integer PDF = 1;

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

            DynamicReportBuilder reportBuilder = new FastReportBuilder()
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("word", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.word", userSetting.getLocale()))
                            .setStyle(ReportUtilities.baseStyle)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("date", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.date", userSetting.getLocale()))
                            .setStyle(ReportUtilities.baseStyle)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("testType", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.testtype", userSetting.getLocale()))
                            .setStyle(ReportUtilities.baseStyle)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("description", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.description", userSetting.getLocale()))
                            .setStyle(ReportUtilities.baseStyle)
                            .build())
                    .setTitle(translationProvider.getTranslation("report.testdata.title", userSetting.getLocale()))
                    .setTitleStyle(ReportUtilities.titleStyle)
                    .addAutoText(ReportUtilities.generateAutoText(user != null ? user.getName() : "-", AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, null))
                    .addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("creationdate", userSetting.getLocale()) + ": " + Utilities.formatDate(currentDate, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()), AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, null))
                    .addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("report", userSetting.getLocale(), translationProvider.getTranslation("title.testdata", userSetting.getLocale())), AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, ReportUtilities.titleStyle))
                    .addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("daterange", userSetting.getLocale(),
                                    Utilities.formatDate(start, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()),
                                    Utilities.formatDate(end, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString())
                            ),
                            AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, null))
                    .setPageSizeAndOrientation(Page.Page_Letter_Landscape())
                    .setPrintColumnNames(true)
                    .setUseFullPageWidth(true)
                    .setPrintBackgroundOnOddRows(false)
                    .setMargins(10, 10, 10, 10)
                    .setReportLocale(userSetting.getLocale())
                    .setWhenNoData(translationProvider.getTranslation("empty", userSetting.getLocale()), new Style())
                    .setMargins(10, 10, 10, 10);


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

            DynamicReportBuilder reportBuilder = new FastReportBuilder()
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("word", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.word", userSetting.getLocale()))
                            .setWidth(ReportUtilities.NORMAL_WIDTH)
                            .setStyle(ReportUtilities.columnLeft)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("date", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.date", userSetting.getLocale()))
                            .setWidth(ReportUtilities.DATE_WIDTH)
                            .setStyle(ReportUtilities.columnCenter)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("testType", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.testtype", userSetting.getLocale()))
                            .setWidth(50)
                            .setStyle(ReportUtilities.columnCenter)
                            .build())
                    .addColumn(ColumnBuilder.getNew()
                            .setColumnProperty("description", String.class)
                            .setTitle(translationProvider.getTranslation("tab.testdata.description", userSetting.getLocale()))
                            .setWidth(350)
                            .setStyle(ReportUtilities.columnLeft)
                            .build())
                    .setTitle(translationProvider.getTranslation("report.testdata.title", userSetting.getLocale()))
                    .setTitleStyle(ReportUtilities.titleStyle)
                    .addAutoText(ReportUtilities.generateAutoText(user != null ? UI.getCurrent().getTranslation("createdby") + ": " + user.getName() : "-", AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, null))
                    .addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("creationdate", userSetting.getLocale()) + ": " + Utilities.formatDate(currentDate, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()), AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, null))
                    .addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("report", userSetting.getLocale(), translationProvider.getTranslation("title.testdata", userSetting.getLocale())), AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, ReportUtilities.titleStyle));

            if (start != null && end != null) {
                reportBuilder.addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("daterange", userSetting.getLocale(),
                                Utilities.formatDate(start, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString()),
                                Utilities.formatDate(end, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString())
                        ),
                        AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, null));
            } else if (start == null && end != null) {
                reportBuilder.addAutoText(ReportUtilities.generateAutoText(translationProvider.getTranslation("daterange.to", userSetting.getLocale(),
                                Utilities.formatDate(end, userSetting.getDateTimeFormat(), userSetting.getTimeZoneString())
                        ),
                        AutoText.AUTOTEXT_CUSTOM_MESSAGE, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 200, 0, null));
            }

            reportBuilder.setPageSizeAndOrientation(Page.Page_Letter_Landscape())
                    .setPrintColumnNames(true)
                    .setUseFullPageWidth(true)
                    .setPrintBackgroundOnOddRows(false)
                    .setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
                    .setMargins(10, 10, 10, 10)
                    .setReportLocale(userSetting.getLocale())
                    .setWhenNoData(translationProvider.getTranslation("empty", userSetting.getLocale()), new Style())
                    .setMargins(10, 10, 10, 10);


            DynamicReport report = reportBuilder.build();
            JasperPrint print = DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), data);

            //
            List<ByteArrayOutputStream> list = new ArrayList<>();

            //CSV
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
}
