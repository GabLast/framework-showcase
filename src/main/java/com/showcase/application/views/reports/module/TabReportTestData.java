package com.showcase.application.views.reports.module;

import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.security.Permit;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.services.module.TestTypeService;
import com.showcase.application.services.reports.ReportService;
import com.showcase.application.utils.Utilities;
import com.showcase.application.views.MainLayout;
import com.showcase.application.views.generics.FilterBoxReports;
import com.showcase.application.views.generics.GenericReportTab;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.vaadin.crudui.crud.LazyCrudListener;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

@Route(value = "vaadin/reports/testdata", layout = MainLayout.class)
@RolesAllowed(value = Permit.REPORT_TEST_DATA)
public class TabReportTestData extends GenericReportTab<TestData> implements HasDynamicTitle {

    private final ReportService reportService;
    private final TestDataService testDataService;
    private final TestTypeService testTypeService;

    public TabReportTestData(ReportService reportService, TestDataService testDataService, TestTypeService testTypeService) {
        super(TestData.class, false);
        this.reportService = reportService;
        this.testDataService = testDataService;
        this.testTypeService = testTypeService;

        prepareComponets();
    }

    @Override
    protected void configFilters(FilterBoxReports filterBox) {
        filterBox.addFilter(String.class, "word", UI.getCurrent().getTranslation("tab.testdata.word"), null, null, false, false);
        filterBox.addFilter(TestType.class, "testType", UI.getCurrent().getTranslation("tab.testdata.testtype"), testTypeService.findAllByEnabled(true), null, false, false);
        filterBox.addFilter(String.class, "description", UI.getCurrent().getTranslation("tab.testdata.description"), null, null, false, false);
        filterBox.addFilter(Date.class, "date", UI.getCurrent().getTranslation("tab.testdata.date"), null, null, false, false);
    }

    @Override
    protected void configGrid(Grid<TestData> grid) {
        grid.removeAllColumns();
        grid.addColumn(TestData::getWord).setKey("word").setHeader(UI.getCurrent().getTranslation("tab.testdata.word")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("12rem");
        grid.addColumn(data -> UI.getCurrent().getTranslation(TestType.toStringI18nKey(data.getTestType()))).setKey("testType").setHeader(UI.getCurrent().getTranslation("tab.testdata.testtype")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("7.5rem");
        grid.addColumn(data -> Utilities.formatDate(data.getDate(), settings.getDateTimeFormat(), settings.getTimeZoneString())).setKey("date").setHeader(UI.getCurrent().getTranslation("tab.testdata.date")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("10.625rem");
        grid.addColumn(TestData::getDescription).setKey("description").setHeader(UI.getCurrent().getTranslation("tab.testdata.description")).setSortable(true).setResizable(true).setFlexGrow(1);
    }

    @Override
    protected LazyCrudListener<TestData> configDataSource() {
        return new LazyCrudListener<>() {
            @Override
            public DataProvider<TestData, ?> getDataProvider() {
                TextField word = (TextField) filterBox.getFilter("word");
                TextField description = (TextField) filterBox.getFilter("description");
                Select<TestType> type = (Select<TestType>) filterBox.getFilter("testType");
                DatePicker start = (DatePicker) filterBox.getFilter("datestart");
                DatePicker end = (DatePicker) filterBox.getFilter("dateend");

                return DataProvider.fromCallbacks(
                        query -> testDataService.findAllFilter(
                                true,
                                settings.getTimeZoneString(),
                                word.getValue(),
                                description.getValue(),
                                type.getValue(),
                                start.getValue(), end.getValue(),
                                query.getLimit(),
                                query.getOffset(),
                                Utilities.generarSortDeBusquedaTabla(
                                        TestData.class,
                                        gridCrud.getGrid().getSortOrder(),
                                        Sort.by(Sort.Direction.DESC, "id"),
                                        null
                                )
                        ).stream(),
                        query -> testDataService.countAllFilter(
                                true,
                                settings.getTimeZoneString(),
                                word.getValue(),
                                description.getValue(),
                                type.getValue(),
                                start.getValue(), end.getValue()
                        )
                );
            }

            @Override
            public TestData add(TestData data) {
                return null;
            }

            @Override
            public TestData update(TestData data) {
                return null;
            }

            @Override
            public void delete(TestData data) {

            }
        };
    }

    @Override
    protected void configOtherComponents(Div divCustomizeBar) {

    }

    @Override
    protected void setDownloadContent() {
        if (filterBox.verifyFields()) {
            new ErrorNotification(UI.getCurrent().getTranslation("error"));
            return;
        }

        TextField word = (TextField) filterBox.getFilter("word");
        TextField description = (TextField) filterBox.getFilter("description");
        Select<TestType> type = (Select<TestType>) filterBox.getFilter("testType");
        DatePicker start = (DatePicker) filterBox.getFilter("datestart");
        DatePicker end = (DatePicker) filterBox.getFilter("dateend");

//        ByteArrayOutputStream csvOutput =
//                reportService.generateTestDataReport(
//                        user, settings, reportService.EXCEL,
//                        word.getValue(), description.getValue(), type.getValue(), start.getValue(), end.getValue()
//                );
//
        List<ByteArrayOutputStream> list = reportService.generateTestDataReport(
                user, settings,
                word.getValue(), description.getValue(), type.getValue(), start.getValue(), end.getValue()
        );
//
//        List<ByteArrayOutputStream> list = reportService.generateReportFromTemplate(
//                user, settings,
//                word.getValue(), description.getValue(), type.getValue(), start.getValue(), end.getValue()
//        );

        filterBox.setDownloadFileCsv(list.get(ReportService.EXCEL), UI.getCurrent().getTranslation("title.testdata"));
//        filterBox.setDownloadFileExcel(list.get(ReportService.EXCEL), UI.getCurrent().getTranslation("title.testdata"));
        filterBox.setDownloadFilePdf(list.get(ReportService.PDF), UI.getCurrent().getTranslation("title.testdata"));
    }

    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("title.testdata") + UI.getCurrent().getTranslation("title.reports");
    }
}
