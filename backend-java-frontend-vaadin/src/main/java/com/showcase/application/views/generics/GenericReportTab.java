package com.showcase.application.views.generics;

import com.showcase.application.config.security.MyVaadinSession;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;

public abstract class GenericReportTab<T> extends Div {

    protected final User user;
    protected final UserSetting settings;


    //Components
    protected VerticalLayout content = new VerticalLayout();
    protected Button btnView;
    protected final FilterBoxReports filterBox;
    protected final GridCrud<T> gridCrud;

    protected final MenuBar toolBar;
    protected final MenuItem menuFindAll;

    protected final Div divCustomizeBar;

    public GenericReportTab(Class<T> tClass, boolean footer) {
        //Crud object
        this.gridCrud = new GridCrud<>(tClass);

        //Variables de session
        this.user = (User) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        this.settings = (UserSetting) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.filterBox = new FilterBoxReports(gridCrud::refreshGrid, this::setDownloadContent);

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

        //Toolbar Buttons
        gridCrud.setClickRowToUpdate(false);

        gridCrud.getDeleteButton().setText(UI.getCurrent().getTranslation("delete"));
        gridCrud.getDeleteButton().setSizeFull();
        gridCrud.getDeleteButton().addThemeVariants(ButtonVariant.LUMO_ERROR);

        gridCrud.getAddButton().setText(UI.getCurrent().getTranslation("create"));
        gridCrud.getAddButton().setSizeFull();

        gridCrud.getUpdateButton().setText(UI.getCurrent().getTranslation("edit"));
        gridCrud.getUpdateButton().setSizeFull();

        gridCrud.getFindAllButton().setText(UI.getCurrent().getTranslation("refresh"));
        gridCrud.getFindAllButton().setSizeFull();

        btnView = new Button(UI.getCurrent().getTranslation("view"));
        btnView.setSizeFull();
        btnView.setIcon(new Icon(VaadinIcon.SEARCH));
        btnView.setEnabled(false);

        toolBar = new MenuBar();
        toolBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE, MenuBarVariant.LUMO_SMALL);
        toolBar.setWidthFull();

        menuFindAll = toolBar.addItem(gridCrud.getFindAllButton());
        menuFindAll.setEnabled(gridCrud.getFindAllButton().isEnabled());
        menuFindAll.setVisible(gridCrud.getFindAllButton().isVisible());

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
        gridCrud.setRowCountCaption(UI.getCurrent().getTranslation("action.rowcount", UI.getCurrent().getTranslation("data")));

        //Filters
        configFilters(filterBox);

        //Grid columns
        configGrid(gridCrud.getGrid());

        //Grid DataSource
        gridCrud.setCrudListener(configDataSource());

        //Configurando otros componentes
        configOtherComponents(divCustomizeBar);

        //setting file
        setDownloadContent();

        //Refresh grid
        gridCrud.refreshGrid();
    }

    protected abstract void configFilters(FilterBoxReports filterBox);

    protected abstract void configGrid(Grid<T> grid);

    protected abstract LazyCrudListener<T> configDataSource();

    protected abstract void configOtherComponents(Div divCustomizeBar);

    protected abstract void setDownloadContent();

}
