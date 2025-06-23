package com.showcase.application.views.security;

import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.Profile;
import com.showcase.application.services.security.ProfileService;
import com.showcase.application.utils.Utilities;
import com.showcase.application.utils.exceptions.MyException;
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.WindowBasedCrudLayout;

@Route(value = "vaadin/security/profiles", layout = MainLayout.class)
@RolesAllowed(Permit.MENU_PROFILE)
public class TabProfile extends GenericTab<Profile> implements HasDynamicTitle {

    private final ProfileService profileService;

    private MenuItem miCreate, miEdit, miView, miDelete;

    public TabProfile(ProfileService profileService) {
        super(Profile.class, false);
        this.profileService = profileService;

        prepareComponets();
    }

    @Override
    protected void applySecurity(GridCrud<Profile> crud) {
        crud.setFindAllOperationVisible(true);
        crud.setAddOperationVisible(false);
        crud.setUpdateOperationVisible(false);
        crud.setDeleteOperationVisible(false);
        menuVisualizar.setVisible(false);

        miCreate.setVisible(SecurityUtils.isAccessGranted(Permit.PROFILE_CREATE));
        miEdit.setVisible(SecurityUtils.isAccessGranted(Permit.PROFILE_EDIT));
        miView.setVisible(SecurityUtils.isAccessGranted(Permit.PROFILE_VIEW));
        miDelete.setVisible(SecurityUtils.isAccessGranted(Permit.PROFILE_DELETE));
    }

    @Override
    protected void configFilters(FilterBox filterBox) {
        filterBox.addFilter(String.class, "name", UI.getCurrent().getTranslation("tab.profile.name"), null, null, false);
        filterBox.addFilter(String.class, "description", UI.getCurrent().getTranslation("tab.profile.description"), null, null, false);
        filterBox.addFilter(String.class, "enabled", UI.getCurrent().getTranslation("enabled"), Utilities.listBooleanI18YesNo(),  UI.getCurrent().getTranslation("yes"), false);
    }

    @Override
    protected void configCrudUIMessages(WindowBasedCrudLayout crudLayout) {
        gridCrud.setRowCountCaption(UI.getCurrent().getTranslation("action.rowcount", UI.getCurrent().getTranslation("data")));
    }

    @Override
    protected void configButtons(MenuBar toolBar) {
        Button btnCreate = new Button(UI.getCurrent().getTranslation("create.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.PLUS));
        btnCreate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreate.setSizeFull();

        Button btnView = new Button(UI.getCurrent().getTranslation("view.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.SEARCH));
        btnView.setSizeFull();

        Button btnEdit = new Button(UI.getCurrent().getTranslation("edit.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.PENCIL));
        btnEdit.setSizeFull();

        Button btnDelete = new Button(UI.getCurrent().getTranslation("delete.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnDelete.setSizeFull();

        miCreate = toolBar.addItem(
                btnCreate, e -> UI.getCurrent().navigate(FormProfile.class));

        miEdit = toolBar.addItem(btnEdit, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "0")
            );

            UI.getCurrent().navigate(FormProfile.class, parameters);
        });

        miView = toolBar.addItem(btnView, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "1")
            );

            UI.getCurrent().navigate(FormProfile.class, parameters);
        });

        miDelete = toolBar.addItem(btnDelete, e -> {

            ConfirmWindow confirmWindow =
                    new ConfirmWindow(
                            UI.getCurrent().getTranslation("action.confirm.question", object.toString()),
                            this::delete);
            confirmWindow.open();
        });
    }

    @Override
    protected void configGrid(Grid<Profile> grid) {
        grid.removeAllColumns();
        grid.addColumn(Profile::getName).setKey("word").setHeader(UI.getCurrent().getTranslation("tab.profile.name")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("13rem");
        grid.addColumn(Profile::getDescription).setKey("description").setHeader(UI.getCurrent().getTranslation("tab.profile.description")).setSortable(true).setResizable(true).setFlexGrow(1);

        grid.setClassNameGenerator(data -> {
            if (!data.isEnabled()) {
                if (userSetting.isDarkMode()) {
                    return "row-red-dark";
                } else {
                    return "row-red";
                }
            }
            return null;
        });
    }

    @Override
    protected LazyCrudListener<Profile> configDataSource() {
        return new LazyCrudListener<>() {
            @Override
            public DataProvider<Profile, ?> getDataProvider() {
                TextField name = (TextField) filterBox.getFilter("name");
                TextField description = (TextField) filterBox.getFilter("description");
                Select<String> enabled = (Select<String>) filterBox.getFilter("enabled");

                return DataProvider.fromCallbacks(
                        query -> profileService.findAllFilter(
                                enabled.getValue() != null ? Utilities.isYes(enabled.getValue()) : null,
                                name.getValue(),
                                description.getValue(),
                                query.getLimit(),
                                query.getOffset(),
                                Utilities.generarSortDeBusquedaTabla(
                                        Profile.class,
                                        gridCrud.getGrid().getSortOrder(),
                                        Sort.by(Sort.Direction.DESC, "id"),
                                        null
                                )
                        ).stream(),
                        query -> profileService.countAllFilter(
                                enabled.getValue() != null ? Utilities.isYes(enabled.getValue()) : null,
                                name.getValue(),
                                description.getValue()
                        )
                );
            }

            @Override
            public Profile add(Profile data) {
                return null;
            }

            @Override
            public Profile update(Profile data) {
                return null;
            }

            @Override
            public void delete(Profile data) {
            }
        };
    }


    @Override
    protected void buildFromCruidUI(DefaultCrudFormFactory<Profile> formFactory) {
    }

    @Override
    protected void configOtherComponents(Div divCustomizeBar) {

    }

    @Override
    protected void modifyBtnState() {
        if (object != null) {
            miView.setEnabled(true);
            if(object.isEnabled()) {
                miEdit .setEnabled(true);
                miDelete.setEnabled(true);
            } else {
                miDelete.setEnabled(false);
                miEdit.setEnabled(false);
            }
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

            profileService.delete(object, userSetting);
            gridCrud.refreshGrid();

            new SuccessNotification(UI.getCurrent().getTranslation("action.delete"));
        } catch (MyException e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("title.profile");
    }
}
