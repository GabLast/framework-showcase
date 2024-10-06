package com.showcase.application.views.module;

import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.security.Permit;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.services.module.TestTypeService;
import com.showcase.application.utils.MyException;
import com.showcase.application.views.MainLayout;
import com.showcase.application.views.generics.BaseForm;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.showcase.application.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Optional;

@Route(value = "vaadin/module/form-testdata/:id?/:view?", layout = MainLayout.class)
@RolesAllowed(value = {Permit.TEST_DATA_CREATE, Permit.TEST_DATA_EDIT, Permit.TEST_DATA_VIEW})
public class FormTestData extends BaseForm<TestData> {

    private final TestDataService testDataService;
    private final TestTypeService testTypeService;

    private TextField tfWord;
    private TextArea tfDescription;
    private ComboBox<TestType> cbTestType;
    private DateTimePicker dpDate;

    public FormTestData(TestDataService testDataService, TestTypeService testTypeService) {
        super(BaseForm.TYPE_TABS_ON_TOP);
        this.testDataService = testDataService;
        this.testTypeService = testTypeService;
        objectToSave = new TestData();
    }

    @Override
    protected void setComponentValues() {
        cbTestType.setItems(testTypeService.findAllByEnabled(true));
        cbTestType.setItemLabelGenerator(it -> UI.getCurrent().getTranslation(TestType.toStringI18nKey(it)));
    }

    @Override
    protected void buildComponents() {
        tfWord = new TextField(UI.getCurrent().getTranslation("form.testdata.word"));
        tfWord.setRequired(true);
        tfWord.setRequiredIndicatorVisible(true);
        tfWord.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfWord.setPlaceholder(UI.getCurrent().getTranslation("form.testdata.word") + "...");
        tfWord.setSizeFull();

        cbTestType = new ComboBox<>();
        cbTestType.setLabel(UI.getCurrent().getTranslation("form.test.testtype"));
        cbTestType.setRequiredIndicatorVisible(true);
        cbTestType.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        cbTestType.setSizeFull();

        dpDate = new DateTimePicker(UI.getCurrent().getTranslation("form.testdata.date"));
        dpDate.setRequiredIndicatorVisible(true);
        dpDate.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        dpDate.setLocale(userSetting.getLocale());
        dpDate.setSizeFull();

        tfDescription = new TextArea(UI.getCurrent().getTranslation("form.testdata.description"));
        tfDescription.setRequired(true);
        tfDescription.setRequiredIndicatorVisible(true);
        tfDescription.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfDescription.setPlaceholder(UI.getCurrent().getTranslation("form.testdata.description") + "...");
        tfDescription.setSizeFull();

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3));

        formLayout.add(tfWord);
        formLayout.add(cbTestType);
        formLayout.add(dpDate);
        formLayout.add(tfDescription, 3);
    }

    @Override
    protected void enableVisualizationOnly() {
        tfWord.setReadOnly(true);
        cbTestType.setReadOnly(true);
        dpDate.setReadOnly(true);
        tfDescription.setReadOnly(true);
    }

    @Override
    protected void fillFields() {
        if (objectToSave == null || objectToSave.getId() == 0L) {
            return;
        }

        tfWord.setValue(objectToSave.getWord());
        cbTestType.setValue(objectToSave.getTestType());
        tfDescription.setValue(objectToSave.getDescription());
        dpDate.setValue(objectToSave.getDate().toInstant().atZone(userSetting.getTimezone().toZoneId()).toLocalDateTime());
//        dpDate.setValue(objectToSave.getDate().toInstant().atZone(userSetting.getTimezone().toZoneId()).toLocalDate());

        if (view) {
            enableVisualizationOnly();
        }
    }

    @Override
    protected boolean verifyFields() {
        boolean isError = false;

        if ((tfWord.isRequired() || tfWord.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfWord.getValue()) || tfWord.isInvalid())) {
            isError = true;
            tfWord.setInvalid(true);
        } else {
            tfWord.setInvalid(false);
        }

        if (cbTestType.isRequiredIndicatorVisible() && (cbTestType.getValue() == null || cbTestType.isInvalid())) {
            isError = true;
            cbTestType.setInvalid(true);
        } else {
            cbTestType.setInvalid(false);
        }

        if (dpDate.isRequiredIndicatorVisible() && (dpDate.getValue() == null || dpDate.isInvalid())) {
            isError = true;
            dpDate.setInvalid(true);
        } else {
            dpDate.setInvalid(false);
        }

        if ((tfDescription.isRequired() || tfDescription.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfDescription.getValue()) || tfDescription.isInvalid())) {
            isError = true;
            tfDescription.setInvalid(true);
        } else {
            tfDescription.setInvalid(false);
        }

        return isError;
    }

    @Override
    protected void save() {
        try {

            if (verifyFields()) {
                return;
            }

            objectToSave.setWord(tfWord.getValue().trim());
            objectToSave.setDescription(tfDescription.getValue().trim());
            objectToSave.setTestType(cbTestType.getValue());
            objectToSave.setDate(Date.from(dpDate.getValue().atZone(userSetting.getTimezone().toZoneId()).toInstant()));
//            Date.from(localDate.atStartOfDay(userSetting.getTimezone().toZoneId())).toInstant());

            objectToSave = testDataService.saveAndFlush(objectToSave);

            new SuccessNotification(UI.getCurrent().getTranslation("action.save.object", objectToSave.toString()));

            UI.getCurrent().getPage().getHistory().back();
        } catch (MyException e) {

            if (objectToSave != null && objectToSave.getId() != 0L) {
                objectToSave = (testDataService.getTestDataById(objectToSave.getId())).orElse(null);
            }

            if (objectToSave == null) {
                objectToSave = new TestData();
            }

            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        String title;

        if (objectToSave == null || objectToSave.getId() == 0L) {
            title = UI.getCurrent().getTranslation("create") + " " + UI.getCurrent().getTranslation("form.testdata.title");
        } else if (objectToSave.getId() != 0L && !view) {
            title = UI.getCurrent().getTranslation("edit") + " " + UI.getCurrent().getTranslation("form.testdata.title");
        } else if (objectToSave.getId() != 0L && view) {
            title = UI.getCurrent().getTranslation("view") + " " + UI.getCurrent().getTranslation("form.testdata.title");
        } else {
            title = UI.getCurrent().getTranslation("empty");
        }

        return title;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters routeParameters = event.getRouteParameters();

        Optional<String> idParam = routeParameters.get("id");
        if (idParam.isEmpty()) {
            return;
        }

        long headerId;
        try {
            headerId = Long.parseLong(idParam.get());
        } catch (NumberFormatException e) {
            headerId = 0L;
        }

        if (headerId == 0L) {
            return;
        }

        Optional<TestData> header = testDataService.getTestDataById(headerId);
        if (header.isEmpty()) {
            event.getUI().getPage().getHistory().back();
            return;
        }

        //check params
        Optional<String> viewParam = routeParameters.get("view");

        objectToSave = header.get();
        view = viewParam.isPresent() && !StringUtils.isBlank(viewParam.get()) && viewParam.get().equalsIgnoreCase("1");

        if (view) {
            if (!SecurityUtils.isAccessGranted(Permit.TEST_DATA_VIEW)) {
                event.getUI().getPage().getHistory().back();
            }
        } else if (!SecurityUtils.isAccessGranted(Permit.TEST_DATA_EDIT)) {
            event.getUI().getPage().getHistory().back();
        }
    }
}
