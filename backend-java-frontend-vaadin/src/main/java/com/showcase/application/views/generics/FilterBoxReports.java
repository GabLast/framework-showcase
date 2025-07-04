package com.showcase.application.views.generics;

import com.showcase.application.config.security.MyVaadinSession;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.utils.Utilities;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.DownloadHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterBoxReports extends Accordion {

    private FormLayout form;
    public Anchor btnDownloadCSV;
    public Anchor btnDownloadExcel;
    public Anchor btnDownloadPDF;

    private final Runnable refreshCall;
    private final Runnable downloadCall;
    private final Map<String, Component> filterMap;
    private final DatePicker.DatePickerI18n datePickerFormat;
    protected final UserSetting settings;

    public FilterBoxReports(Runnable refreshCall, Runnable downloadCall) {
        this.refreshCall = refreshCall;
        this.downloadCall = downloadCall;
        this.filterMap = new HashMap<>();
        this.settings = (UserSetting) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.datePickerFormat = new DatePicker.DatePickerI18n();
        configDateFormats(this.datePickerFormat);

        setId("FilterBoxReports");

        add(UI.getCurrent().getTranslation("filter.title"), buildView()).addThemeVariants(DetailsVariant.FILLED, DetailsVariant.REVERSE, DetailsVariant.SMALL);

        setWidthFull();
    }

    private void configDateFormats(DatePicker.DatePickerI18n datePickerI18n) {
        datePickerI18n.setDateFormat(settings.getDateFormat());
        datePickerI18n.setToday(UI.getCurrent().getTranslation("today"));
        datePickerI18n.setMonthNames(List.of(
                UI.getCurrent().getTranslation("january"),
                UI.getCurrent().getTranslation("february"),
                UI.getCurrent().getTranslation("march"),
                UI.getCurrent().getTranslation("april"),
                UI.getCurrent().getTranslation("may"),
                UI.getCurrent().getTranslation("june"),
                UI.getCurrent().getTranslation("july"),
                UI.getCurrent().getTranslation("august"),
                UI.getCurrent().getTranslation("september"),
                UI.getCurrent().getTranslation("october"),
                UI.getCurrent().getTranslation("november"),
                UI.getCurrent().getTranslation("december")
        ));
        datePickerI18n.setCancel(UI.getCurrent().getTranslation("cancel"));
    }

    private Component buildView() {
        btnDownloadCSV = new Anchor();
        btnDownloadCSV.setWidthFull();
        btnDownloadCSV.setVisible(false);

        Button btnAux = new Button(UI.getCurrent().getTranslation("download.csv"));
        btnAux.setIcon(VaadinIcon.FILE_PROCESS.create());
        btnAux.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        btnAux.setWidthFull();

        btnDownloadCSV.add(btnAux);

//************

        btnDownloadExcel = new Anchor();
        btnDownloadExcel.setWidthFull();
        btnDownloadExcel.setVisible(false);

        Button btnAuxExcel = new Button(UI.getCurrent().getTranslation("download.excel"));
        btnAuxExcel.setIcon(VaadinIcon.FILE_PROCESS.create());
        btnAuxExcel.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        btnAuxExcel.setWidthFull();

        btnDownloadExcel.add(btnAuxExcel);

//************

        btnDownloadPDF = new Anchor();
        btnDownloadPDF.setWidthFull();
        btnDownloadPDF.setVisible(false);

        Button btnAuxPdf = new Button(UI.getCurrent().getTranslation("download.pdf"));
        btnAuxPdf.setIcon(VaadinIcon.FILE_PROCESS.create());
        btnAuxPdf.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        btnAuxPdf.setWidthFull();

        btnDownloadPDF.add(btnAuxPdf);

        //Layout de los filtros
        form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("300px", 2),
                new FormLayout.ResponsiveStep("600px", 3),
                new FormLayout.ResponsiveStep("900px", 4),
                new FormLayout.ResponsiveStep("1200px", 5));

        Div div = new Div();
        div.setHeight("10px");
        form.add(div, 5);
        form.add(btnDownloadCSV, 1);
        form.add(btnDownloadExcel, 1);
        form.add(btnDownloadPDF, 1);

        return form;
    }

    public Component getFilter(String id) {
        return filterMap.get(id);
    }

    public <T> void addFilter(Class<T> dataClass, String id, String caption, List<T> listObjects, T defaultValue, boolean multiSelect, boolean isRequired) {
        if (listObjects != null && !multiSelect) {
            Select<T> select = new Select<T>();
            select.setLabel(caption);
            select.setId(id);
            select.setItems(listObjects);
            select.setValue(defaultValue);

            if (!isRequired) {
                select.setEmptySelectionAllowed(true);
                select.setEmptySelectionCaption(UI.getCurrent().getTranslation("all"));
            }

            select.getElement().setAttribute("theme", "small");
            select.addValueChangeListener(comboBoxTComponentValueChangeEvent -> refreshData());
            select.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            addComponentToForm(id, select);
        } else if (listObjects != null) {
            MultiSelectComboBox<T> multiSelectComboBox = new MultiSelectComboBox<T>();
            multiSelectComboBox.setLabel(caption);
            multiSelectComboBox.setId(id);
            multiSelectComboBox.setItems(listObjects);

            if (defaultValue != null) {
                multiSelectComboBox.setValue(defaultValue);
            }

            multiSelectComboBox.setAllowCustomValue(false);
            multiSelectComboBox.setPlaceholder(UI.getCurrent().getTranslation("all"));
            multiSelectComboBox.getElement().setAttribute("theme", "small");

            if (!isRequired) {
                multiSelectComboBox.setRequired(false);
                multiSelectComboBox.setRequiredIndicatorVisible(false);
            }

            multiSelectComboBox.setClearButtonVisible(true);
            multiSelectComboBox.addValueChangeListener(comboBoxTComponentValueChangeEvent -> refreshData());
            multiSelectComboBox.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            addComponentToForm(id, multiSelectComboBox);
        } else if (dataClass == String.class) {
            TextField textField = new TextField(caption);
            textField.setId(id);
            textField.setRequired(isRequired);
            textField.setRequiredIndicatorVisible(isRequired);
            textField.setClearButtonVisible(true);
            textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            textField.addValueChangeListener(textFieldStringComponentValueChangeEvent -> refreshData());
            textField.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            addComponentToForm(id, textField);
        } else if (dataClass == Integer.class || dataClass == Long.class) {
            IntegerField numberField = new IntegerField(caption);
            numberField.setId(id);
            numberField.setRequired(isRequired);
            numberField.setRequiredIndicatorVisible(isRequired);
            numberField.setClearButtonVisible(true);
            numberField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            numberField.setPlaceholder("0");
            numberField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            numberField.addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> refreshData());
            numberField.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            addComponentToForm(id, numberField);
        } else if (dataClass == BigDecimal.class) {
            BigDecimalField bigDecimalField = new BigDecimalField(caption);
            bigDecimalField.setId(id);
            bigDecimalField.setRequired(isRequired);
            bigDecimalField.setRequiredIndicatorVisible(isRequired);
            bigDecimalField.setClearButtonVisible(true);
            bigDecimalField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            bigDecimalField.setPlaceholder("0");
            bigDecimalField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            bigDecimalField.addValueChangeListener(bigDecimalFieldBigDecimalComponentValueChangeEvent -> refreshData());
            bigDecimalField.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            addComponentToForm(id, bigDecimalField);
        } else if (dataClass == Date.class || dataClass == LocalDate.class) {
            DatePicker dpStart = new DatePicker(caption);
            dpStart.setWidthFull();
            dpStart.setId(id + "start");
            dpStart.setRequired(isRequired);
            dpStart.setRequiredIndicatorVisible(isRequired);
            dpStart.setClearButtonVisible(true);
            dpStart.setValue(defaultValue != null ? (LocalDate) defaultValue : null);
            dpStart.getElement().setAttribute("theme", "small");
            dpStart.setLocale(UI.getCurrent().getLocale());
            dpStart.addValueChangeListener(datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpStart.setErrorMessage(UI.getCurrent().getTranslation("form.error"));

            DatePicker dpEnd = new DatePicker(UI.getCurrent().getTranslation("to"));
            dpEnd.setWidthFull();
            dpEnd.setId(id + "end");
            dpEnd.setRequired(isRequired);
            dpEnd.setRequiredIndicatorVisible(isRequired);
            dpEnd.setClearButtonVisible(true);
            dpEnd.setValue(null);
            dpEnd.getElement().setAttribute("theme", "small");
            dpEnd.setLocale(UI.getCurrent().getLocale());
            dpEnd.addValueChangeListener(datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpEnd.setErrorMessage(UI.getCurrent().getTranslation("form.error"));

            FormLayout formDates = new FormLayout();
            formDates.setSizeFull();
            formDates.setResponsiveSteps(new FormLayout.ResponsiveStep("1px", 2));
            formDates.add(dpStart);
            formDates.add(dpEnd);

            addComponentToForm(id, formDates);

            filterMap.put(id + "start", dpStart);
            filterMap.put(id + "end", dpEnd);
        } else if (dataClass == LocalDateTime.class) {
            DateTimePicker dpStart = new DateTimePicker(caption);
            dpStart.setWidthFull();
            dpStart.setId(id + "start");
            dpStart.setValue(defaultValue != null ? (LocalDateTime) defaultValue : null);
            dpStart.setRequiredIndicatorVisible(isRequired);
            dpStart.getElement().setAttribute("theme", "small");
            dpStart.setLocale(UI.getCurrent().getLocale());
            dpStart.addValueChangeListener(datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpStart.setDatePickerI18n(datePickerFormat);
            dpStart.setErrorMessage(UI.getCurrent().getTranslation("form.error"));

            DateTimePicker dpEnd = new DateTimePicker(UI.getCurrent().getTranslation("to"));
            dpEnd.setWidthFull();
            dpEnd.setId(id + "end");
            dpEnd.setValue(null);
            dpEnd.setRequiredIndicatorVisible(isRequired);
            dpEnd.getElement().setAttribute("theme", "small");
            dpEnd.setLocale(UI.getCurrent().getLocale());
            dpEnd.addValueChangeListener(datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpEnd.setDatePickerI18n(datePickerFormat);
            dpEnd.setErrorMessage(UI.getCurrent().getTranslation("form.error"));

            FormLayout formDates = new FormLayout();
            formDates.setSizeFull();
            formDates.setResponsiveSteps(new FormLayout.ResponsiveStep("1px", 1));
            formDates.add(dpStart);
            formDates.add(dpEnd);

            addComponentToForm(id, formDates);

            filterMap.put(id + "start", dpStart);
            filterMap.put(id + "end", dpEnd);
        } else if (dataClass == NativeLabel.class) {
            NativeLabel label = new NativeLabel(caption);
            label.setId(id);
            addComponentToForm(id, label);
        }
    }

    private void addComponentToForm(String id, Component component) {
        form.addComponentAsFirst(component);
        filterMap.put(id, component);
    }

    public void setDownloadFileCsv(ByteArrayOutputStream byteArrayOutputStream, String fileName) {

        btnDownloadCSV.setVisible(true);
//        btnDownloadCSV.getElement().setAttribute("download", fileName + System.currentTimeMillis());
        DownloadHandler handler = event -> {
            event.setFileName(Utilities.generateFileName(fileName, "csv"));
            event.setContentType("text/csv");
            try (OutputStream out = event.getOutputStream()) {
                // Write your data to the output stream
                out.write(byteArrayOutputStream.toByteArray());
            }
        };

        btnDownloadCSV.setHref(handler);
    }

    public void setDownloadFileExcel(ByteArrayOutputStream byteArrayOutputStream, String fileName) {

        btnDownloadExcel.setVisible(true);
//        btnDownloadExcel.getElement().setAttribute("download", fileName + System.currentTimeMillis());
//        btnDownloadExcel.setHref(Utilities.getStreamResource(byteArrayOutputStream, fileName, ".xlsx"));
        DownloadHandler handler = event -> {
            event.setFileName(Utilities.generateFileName(fileName, "xlsx"));
            event.setContentType("application/vnd.ms-excel");
            try (OutputStream out = event.getOutputStream()) {
                // Write your data to the output stream
                out.write(byteArrayOutputStream.toByteArray());
            }
        };
        btnDownloadExcel.setHref(handler);
    }

    public void setDownloadFilePdf(ByteArrayOutputStream byteArrayOutputStream, String fileName) {
        btnDownloadPDF.setVisible(true);
//        btnDownloadPDF.getElement().setAttribute("download", fileName + System.currentTimeMillis());
//        btnDownloadPDF.setHref(Utilities.getStreamResource(byteArrayOutputStream, fileName, ".pdf"));
        DownloadHandler handler = event -> {
            event.setFileName(Utilities.generateFileName(fileName, "pdf"));
            event.setContentType("application/pdf");
            try (OutputStream out = event.getOutputStream()) {
                // Write your data to the output stream
                out.write(byteArrayOutputStream.toByteArray());
            }
        };

        btnDownloadPDF.setHref(handler);
    }

    private void refreshData() {
        if (verifyFields()) {
            return;
        }
        refreshCall.run();
        downloadCall.run();
    }

    public boolean verifyFields() {
        List<Component> componentList = form.getChildren().toList();
        boolean isError = false;

        for (Component component : componentList) {
            if (component instanceof HorizontalLayout || component instanceof VerticalLayout || component instanceof FormLayout) {
                for (Component child : component.getChildren().toList()) {
                    if (verifyComponent(child)) {
                        isError = true;
                    }
                }
            } else {
                if (verifyComponent((component))) {
                    isError = true;
                }
            }
        }

        return isError;
    }

    private boolean verifyComponent(Component component) {
        boolean isError = false;

        if (component instanceof TextField) {
            if (((TextField) component).isRequired() && StringUtils.isBlank(((TextField) component).getValue())) {
                isError = true;
                ((TextField) component).setInvalid(true);
            } else {
                ((TextField) component).setInvalid(false);
            }
        } else if (component instanceof IntegerField) {
            if (((IntegerField) component).isRequiredIndicatorVisible() && ((IntegerField) component).getValue() == null) {
                isError = true;
                ((IntegerField) component).setInvalid(true);
            } else {
                ((IntegerField) component).setInvalid(false);
            }
        } else if (component instanceof BigDecimalField) {
            if (((BigDecimalField) component).isRequiredIndicatorVisible() && ((BigDecimalField) component).getValue() == null) {
                isError = true;
                ((BigDecimalField) component).setInvalid(true);
            } else {
                ((BigDecimalField) component).setInvalid(false);
            }
        } else if (component instanceof Select) {
            if (((Select<?>) component).isRequiredIndicatorVisible() && ((Select<?>) component).getValue() == null) {
                isError = true;
                ((Select<?>) component).setInvalid(true);
            } else {
                ((Select<?>) component).setInvalid(false);
            }
        } else if (component instanceof ComboBox) {
            if (((ComboBox<?>) component).isRequired() && ((ComboBox<?>) component).getValue() == null) {
                isError = true;
                ((ComboBox<?>) component).setInvalid(true);
            } else {
                ((ComboBox<?>) component).setInvalid(false);
            }
        } else if (component instanceof DatePicker) {
            if (((DatePicker) component).isRequired() && ((DatePicker) component).getValue() == null) {
                isError = true;
                ((DatePicker) component).setInvalid(true);
            } else {
                ((DatePicker) component).setInvalid(false);
            }
        } else if (component instanceof DateTimePicker) {
            if (((DateTimePicker) component).isRequiredIndicatorVisible() && ((DateTimePicker) component).getValue() == null) {
                isError = true;
                ((DateTimePicker) component).setInvalid(true);
            } else {
                ((DateTimePicker) component).setInvalid(false);
            }
        }

        return isError;
    }

}
