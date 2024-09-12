package com.showcase.application.views.module;

import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.security.Permit;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.services.module.TestTypeService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.Utilities;
import com.showcase.application.views.MainLayout;
import com.showcase.application.views.generics.FilterBox;
import com.showcase.application.views.generics.GenericTab;
import com.showcase.application.views.generics.dialog.ConfirmWindow;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.showcase.application.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.WindowBasedCrudLayout;

import java.util.Date;

@Route(value = "testdata", layout = MainLayout.class)
@RolesAllowed(Permit.MENU_TEST_DATA)
public class TabTestData extends GenericTab<TestData> {

    private final TestDataService testDataService;
    private final TestTypeService testTypeService;

    private MenuItem miCreate, miEdit, miView, miDelete;

    public TabTestData(TestDataService testDataService, TestTypeService testTypeService) {
        super(TestData.class, false);
        this.testDataService = testDataService;
        this.testTypeService = testTypeService;

        prepareComponets();
    }

    @Override
    protected void applySecurity(GridCrud<TestData> crud) {
        crud.setFindAllOperationVisible(true);
        crud.setAddOperationVisible(false);
        crud.setUpdateOperationVisible(false);
        crud.setDeleteOperationVisible(false);
        menuVisualizar.setVisible(false);

        miCreate.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_CREATE));
        miEdit.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_EDIT));
        miView.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_VIEW));
        miDelete.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_DELETE));
    }

    @Override
    protected void configFilters(FilterBox filterBox) {
        filterBox.addFilter(String.class, "word", UI.getCurrent().getTranslation("tab.testdata.word"), null, null, false);
        filterBox.addFilter(TestType.class, "testType", UI.getCurrent().getTranslation("tab.testdata.testtype"), testTypeService.findAllByEnabled(true), null, false);
        filterBox.addFilter(String.class, "description", UI.getCurrent().getTranslation("tab.testdata.description"), null, null, false);
        filterBox.addFilter(Date.class, "date", UI.getCurrent().getTranslation("tab.testdata.date"), null, null, false);
    }

    @Override
    protected void configCrudUIMessages(WindowBasedCrudLayout crudLayout) {
        crudLayout.setWindowCaption(CrudOperation.ADD, UI.getCurrent().getTranslation("new"));
        crudLayout.setWindowCaption(CrudOperation.UPDATE, UI.getCurrent().getTranslation("edit"));
        crudLayout.setWindowCaption(CrudOperation.DELETE, UI.getCurrent().getTranslation("delete"));

        gridCrud.setDeletedMessage(UI.getCurrent().getTranslation("action.delete"));
        gridCrud.setSavedMessage(UI.getCurrent().getTranslation("action.save"));
        gridCrud.setRowCountCaption(UI.getCurrent().getTranslation("action.rowcount", UI.getCurrent().getTranslation("data")));
    }

    @Override
    protected void configButtons(MenuBar toolBar) {
        Button btnNuevo = new Button(UI.getCurrent().getTranslation("new.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.PLUS));
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNuevo.setSizeFull();

        Button btnVisualizar = new Button(UI.getCurrent().getTranslation("view.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.SEARCH));
        btnVisualizar.setSizeFull();

        Button btnEditar = new Button(UI.getCurrent().getTranslation("edit.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.PENCIL));
        btnEditar.setSizeFull();

        Button btnBorrar = new Button(UI.getCurrent().getTranslation("delete.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.TRASH));
        btnBorrar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnBorrar.setSizeFull();

        miCreate = toolBar.addItem(
                btnNuevo, e -> UI.getCurrent().navigate(FormTestData.class));

        miEdit = toolBar.addItem(btnEditar, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "0")
            );

            UI.getCurrent().navigate(FormTestData.class, parameters);
        });

        miView = toolBar.addItem(btnVisualizar, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "1")
            );

            UI.getCurrent().navigate(FormTestData.class, parameters);
        });

        miDelete = toolBar.addItem(btnBorrar, e -> {

            ConfirmWindow ventanaConfirmacion =
                    new ConfirmWindow(
                            UI.getCurrent().getTranslation("action.delete.object", object.toString()),
                            this::delete);
            ventanaConfirmacion.open();
        });
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
                return testDataService.saveAndFlush(data);
            }

            @Override
            public TestData update(TestData data) {
                return testDataService.saveAndFlush(data);
            }

            @Override
            public void delete(TestData data) {
                testDataService.saveAndFlush(data);
            }
        };
    }


    @Override
    protected void buildFromCruidUI(DefaultCrudFormFactory<TestData> formFactory) {
//        formFactory.setVisibleProperties("word", "testType", "description");
//        formFactory.setFieldCaptions(
//                UI.getCurrent().getTranslation("tab.testdata.word"),
//                UI.getCurrent().getTranslation("tab.testdata.testtype"),
//                UI.getCurrent().getTranslation("tab.testdata.description")
//        );
//
//        formFactory.setFieldProvider("word", a -> {
//            TextField field = new TextField(UI.getCurrent().getTranslation("tab.testdata.word"));
//            field.setPlaceholder(UI.getCurrent().getTranslation("tab.testdata.word") + "...");
//            field.setRequired(true);
//            field.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
//            field.setRequiredIndicatorVisible(true);
//            return field;
//        });
//
//        formFactory.setFieldProvider("testType", a -> {
//            Select<TestType> select = new Select<>();
//            select.setLabel(UI.getCurrent().getTranslation("tab.testdata.testtype"));
//            select.setPlaceholder(UI.getCurrent().getTranslation("tab.testdata.testtype") + "...");
//            select.setRequiredIndicatorVisible(true);
//            select.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
//            select.setItems(testTypeService.findAllByEnabled(true));
//            select.setItemLabelGenerator(TestType::toStringI18nKey);
//            return select;
//        });
//
//        formFactory.setFieldProvider("description", a -> {
//            TextArea field = new TextArea(UI.getCurrent().getTranslation("tab.testdata.description"));
//            field.setPlaceholder(UI.getCurrent().getTranslation("tab.testdata.description") + "...");
//            field.setRequired(true);
//            field.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
//            field.setRequiredIndicatorVisible(true);
//            return field;
//        });
        //decimalfield needs setConverter
//        BigDecimalField decimalField = new BigDecimalField("number");
//        decimalField.setRequired(false);
//        decimalField.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
//        decimalField.setRequiredIndicatorVisible(false);
//        formFactory.setFieldProvider("number", a -> decimalField);
//        formFactory.setConverter("number", new Converter<BigDecimal, BigDecimal>() {
//            @Override
//            public Result<BigDecimal> convertToModel(BigDecimal value, ValueContext valueContext) {
//                return Result.ok(Objects.requireNonNullElse(value, BigDecimal.ZERO));
//            }
//
//            @Override
//            public BigDecimal convertToPresentation(BigDecimal value, ValueContext valueContext) {
//                return Objects.requireNonNullElse(value, BigDecimal.ZERO);
//            }
//        });
    }

    @Override
    protected void configOtherComponents(Div divCustomizeBar) {

    }

    @Override
    protected void modifyBtnState() {
        if (object != null) {
            miEdit.setEnabled(true);
            miView.setEnabled(true);
            miDelete.setEnabled(true);
        } else {
            miEdit.setEnabled(false);
            miView.setEnabled(false);
            miDelete.setEnabled(false);
        }
    }

    @Override
    protected void delete() {
        try {
            if (object == null) {
                return;
            }

            testDataService.disable(object);
            gridCrud.refreshGrid();

            new SuccessNotification(UI.getCurrent().getTranslation("action.delete.object", object.toString()));
        } catch (MyException e) {
            new ErrorNotification(e.getMessage());
        }
    }
}
