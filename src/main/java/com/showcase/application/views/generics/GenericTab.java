package com.showcase.application.views.generics;

import com.showcase.application.config.security.MyVaadinSession;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.User;
import com.showcase.application.utils.MyException;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.WindowBasedCrudLayout;

import java.util.Optional;

public abstract class GenericTab<T> extends Div implements AfterNavigationObserver {

    protected final User user;
    protected final UserSetting settings;

    //Selected Object
    protected T object = null;

    //Components
    protected VerticalLayout content = new VerticalLayout();
    protected Button btnView;
    protected final FilterBox filterBox;
    protected final GridCrud<T> gridCrud;

    protected final MenuBar toolBar;
    protected final MenuItem menuFindAll, menuAdd, menuUpdate, menuVisualizar, menuDelete;

    protected final Div divCustomizeBar;

    public GenericTab(Class<T> tClass, boolean footer) {
        //Crud object
        this.gridCrud = new GridCrud<>(tClass);

        //Variables de session
        this.user = (User) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        this.settings = (UserSetting) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.filterBox = new FilterBox(gridCrud::refreshGrid);

        //Defaults
        //Grid
        Grid<T> grid = gridCrud.getGrid();
        grid.setSizeFull();
        grid.setPageSize(50);
        grid.setMultiSort(false);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
        if(footer) {
            grid.appendFooterRow();
        }

        //FormFactory
        gridCrud.setCrudFormFactory(new CustomFormFactory<>(tClass) {
            @Override
            public Component applyChanges(CrudOperation operation, boolean readOnly, FormLayout campos, Component footer, VerticalLayout mainLayout) {
                return mainLayout;
            }
        });
        configFormCrudUI((DefaultCrudFormFactory<T>) gridCrud.getCrudFormFactory());

        //Toolbar Buttons
        gridCrud.setClickRowToUpdate(false);

        gridCrud.getDeleteButton().setText(UI.getCurrent().getTranslation("delete"));
        gridCrud.getDeleteButton().setSizeFull();
        gridCrud.getDeleteButton().addThemeVariants(ButtonVariant.LUMO_ERROR);

        gridCrud.getAddButton().setText(UI.getCurrent().getTranslation("new"));
        gridCrud.getAddButton().setSizeFull();

        gridCrud.getUpdateButton().setText(UI.getCurrent().getTranslation("edit"));
        gridCrud.getUpdateButton().setSizeFull();

        gridCrud.getFindAllButton().setText(UI.getCurrent().getTranslation("refresh"));
        gridCrud.getFindAllButton().setSizeFull();

        btnView = new Button(UI.getCurrent().getTranslation("view"));
        btnView.setSizeFull();
        btnView.setIcon(new Icon(VaadinIcon.SEARCH));
        btnView.setEnabled(false);

        //Crud Layout
        WindowBasedCrudLayout crudLayout = (WindowBasedCrudLayout) gridCrud.getCrudLayout();
        btnView.addClickListener(buttonClickEvent -> {
            if (object != null) {
                crudLayout.showDialog(
                        StringUtils.isBlank("View") ? UI.getCurrent().getTranslation("view") : "View",
//                        UI.getCurrent().getTranslation("view"),
                        gridCrud.getCrudFormFactory().buildNewForm(
                                CrudOperation.READ,
                                object,
                                true,
                                btnCancelar -> crudLayout.hideForm(),
                                buttonClickEvent1 -> crudLayout.hideForm()
                        )
                );
            }
        });
        crudLayout.addToolbarComponent(btnView);

        toolBar = new MenuBar();
        toolBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE, MenuBarVariant.LUMO_SMALL);
        toolBar.setWidthFull();

        menuFindAll = toolBar.addItem(gridCrud.getFindAllButton());
        menuFindAll.setEnabled(gridCrud.getFindAllButton().isEnabled());
        menuFindAll.setVisible(gridCrud.getFindAllButton().isVisible());

        menuAdd = toolBar.addItem(gridCrud.getAddButton());
        menuAdd.setEnabled(gridCrud.getAddButton().isEnabled());
        menuAdd.setVisible(gridCrud.getAddButton().isVisible());

        menuUpdate = toolBar.addItem(gridCrud.getUpdateButton());
        menuUpdate.setEnabled(gridCrud.getUpdateButton().isEnabled());
        menuUpdate.setVisible(gridCrud.getUpdateButton().isVisible());

        menuVisualizar = toolBar.addItem(btnView);
        menuVisualizar.setEnabled(btnView.isEnabled());
        menuVisualizar.setVisible(btnView.isVisible());

        menuDelete = toolBar.addItem(gridCrud.getDeleteButton());
        menuDelete.setEnabled(gridCrud.getDeleteButton().isEnabled());
        menuDelete.setVisible(gridCrud.getDeleteButton().isVisible());

        grid.addSelectionListener(selectionEvent -> {
            Optional<T> firstSelectedItem = selectionEvent.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                object = firstSelectedItem.get();
                modifyBtnState(true);
            } else {
                object = null;
                modifyBtnState(false);
            }

            modifyBtnState();
        });

        //Div for extra buttons
        divCustomizeBar = new Div();
        divCustomizeBar.setVisible(false);
        divCustomizeBar.setWidthFull();

        //Content
        content.setSizeFull();
        content.setSpacing(true);
        content.add(filterBox);
        content.add(toolBar);
        content.add(divCustomizeBar);
        content.add(gridCrud.getGrid());

        add(content);
        setSizeFull();
    }

    protected void prepareComponets() {
        //Filters
        configFilters(filterBox);

        //Cruid UI Library messages
        configCrudUIMessages((WindowBasedCrudLayout) gridCrud.getCrudLayout());

        //Grid columns
        configGrid(gridCrud.getGrid());

        //Cruid UI Library Form
        buildFromCruidUI((DefaultCrudFormFactory<T>) gridCrud.getCrudFormFactory());

        //Grid DataSource
        gridCrud.setCrudListener(configDataSource());

        //Configurando otros botones
        configButtons(toolBar);

        //Configurando otros componentes
        configOtherComponents(divCustomizeBar);

        //Security
        applySecurity(gridCrud);

        //Refresh grid
        gridCrud.refreshGrid();
    }

    public void configFormCrudUI(DefaultCrudFormFactory<T> crudFormFactory) {
        crudFormFactory.setUseBeanValidation(true);
        crudFormFactory.setCancelButtonCaption(UI.getCurrent().getTranslation("cancel"));
        crudFormFactory.setValidationErrorMessage(UI.getCurrent().getTranslation("form.error"));
        crudFormFactory.setButtonCaption(CrudOperation.ADD, UI.getCurrent().getTranslation("save"));
        crudFormFactory.setButtonCaption(CrudOperation.UPDATE, UI.getCurrent().getTranslation("save"));
        crudFormFactory.setButtonCaption(CrudOperation.READ, UI.getCurrent().getTranslation("view"));
        crudFormFactory.setButtonCaption(CrudOperation.DELETE, UI.getCurrent().getTranslation("delete"));
        crudFormFactory.setErrorListener(e -> {
            Integer code = MyException.SERVER_ERROR;
            String message = e.getMessage();
            if (e instanceof MyException) {
                code = ((MyException) e).getCode();
            }

            new ErrorNotification(message, 3);

            throw new MyException(code, message);
        });
    }

    private void modifyBtnState(boolean status) {
        btnView.setEnabled(status);
        menuUpdate.setEnabled(status);
        menuVisualizar.setEnabled(status);
        menuDelete.setEnabled(status);
    }

    protected abstract void applySecurity(GridCrud<T> crud);

    protected abstract void configFilters(FilterBox filterBox);

    protected abstract void configCrudUIMessages(WindowBasedCrudLayout crudLayout);

    protected abstract void configButtons(MenuBar toolBar);

    protected abstract void configGrid(Grid<T> grid);

    protected abstract LazyCrudListener<T> configDataSource();

    protected abstract void buildFromCruidUI(DefaultCrudFormFactory<T> formFactory);

    protected abstract void configOtherComponents(Div divCustomizeBar);

    protected abstract void modifyBtnState();

    protected abstract void delete();

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        modifyBtnState();
    }
}
