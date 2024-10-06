package com.showcase.application.views.security;

import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.User;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.UserService;
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

@Route(value = "vaadin/security/users", layout = MainLayout.class)
@RolesAllowed(Permit.MENU_USER)
public class TabUser extends GenericTab<User> implements HasDynamicTitle {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    private MenuItem miCreate, miEdit, miView, miDelete, miCreateToken;

    public TabUser(UserService userService, AuthenticationService authenticationService) {
        super(User.class, false);
        this.userService = userService;
        this.authenticationService = authenticationService;

        prepareComponets();
    }

    @Override
    protected void applySecurity(GridCrud<User> crud) {
        crud.setFindAllOperationVisible(true);
        crud.setAddOperationVisible(false);
        crud.setUpdateOperationVisible(false);
        crud.setDeleteOperationVisible(false);
        menuVisualizar.setVisible(false);

        miCreate.setVisible(SecurityUtils.isAccessGranted(Permit.USER_CREATE));
        miEdit.setVisible(SecurityUtils.isAccessGranted(Permit.USER_EDIT));
        miView.setVisible(SecurityUtils.isAccessGranted(Permit.USER_VIEW));
        miDelete.setVisible(SecurityUtils.isAccessGranted(Permit.USER_DELETE));
        miCreateToken.setVisible(SecurityUtils.isAccessGranted(Permit.USER_TOKEN));
    }

    @Override
    protected void configFilters(FilterBox filterBox) {
        filterBox.addFilter(String.class, "name", UI.getCurrent().getTranslation("tab.user.name"), null, null, false);
        filterBox.addFilter(String.class, "mail", UI.getCurrent().getTranslation("tab.user.mail"), null, null, false);
        filterBox.addFilter(String.class, "admin", UI.getCurrent().getTranslation("tab.user.admin"), Utilities.listBooleanI18YesNo(), null, false);
        filterBox.addFilter(String.class, "enabled", UI.getCurrent().getTranslation("enabled"), Utilities.listBooleanI18YesNo(), UI.getCurrent().getTranslation("yes"), false);
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

        Button btnCreateToken = new Button(UI.getCurrent().getTranslation("create.object", UI.getCurrent().getTranslation("token")), new Icon(VaadinIcon.PLUS));
        btnCreateToken.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreateToken.setSizeFull();

        miCreate = toolBar.addItem(
                btnCreate, e -> UI.getCurrent().navigate(FormUser.class));

        miEdit = toolBar.addItem(btnEdit, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "0")
            );

            UI.getCurrent().navigate(FormUser.class, parameters);
        });

        miView = toolBar.addItem(btnView, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "1")
            );

            UI.getCurrent().navigate(FormUser.class, parameters);
        });

        miDelete = toolBar.addItem(btnDelete, e -> {

            ConfirmWindow confirmWindow =
                    new ConfirmWindow(
                            UI.getCurrent().getTranslation("action.confirm.question", object.toString()),
                            this::delete);
            confirmWindow.open();
        });

        miCreateToken = toolBar.addItem(btnCreateToken, e -> {
            ConfirmWindow confirmWindow =
                    new ConfirmWindow(
                            UI.getCurrent().getTranslation("action.confirm.question", object.toString()),
                            this::generateToken);
            confirmWindow.open();
        });
    }

    @Override
    protected void configGrid(Grid<User> grid) {
        grid.removeAllColumns();

        grid.addColumn(User::getName).setKey("name").setHeader(UI.getCurrent().getTranslation("tab.user.name")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("13rem");
        grid.addColumn(User::getMail).setKey("mail").setHeader(UI.getCurrent().getTranslation("tab.user.mail")).setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(data -> Utilities.formatBooleanYes(data.isAdmin())).setKey("admin").setHeader(UI.getCurrent().getTranslation("tab.user.admin")).setSortable(true).setResizable(true).setFlexGrow(1);

        grid.setClassNameGenerator(data -> {
            if (data.isAdmin()) {
                if (userSetting.isDarkMode()) {
                    return "row-green-dark";
                } else {
                    return "row-green";
                }
            }
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
    protected LazyCrudListener<User> configDataSource() {
        return new LazyCrudListener<>() {
            @Override
            public DataProvider<User, ?> getDataProvider() {
                TextField name = (TextField) filterBox.getFilter("name");
                TextField mail = (TextField) filterBox.getFilter("mail");
                Select<String> enabled = (Select<String>) filterBox.getFilter("enabled");
                Select<String> admin = (Select<String>) filterBox.getFilter("admin");

                return DataProvider.fromCallbacks(
                        query -> userService.findAllFilter(
                                enabled.getValue() != null ? Utilities.isYes(enabled.getValue()) : null,
                                name.getValue(),
                                mail.getValue(),
                                admin.getValue() != null ? Utilities.isYes(admin.getValue()) : null,
                                query.getLimit(),
                                query.getOffset(),
                                Utilities.generarSortDeBusquedaTabla(
                                        User.class,
                                        gridCrud.getGrid().getSortOrder(),
                                        Sort.by(Sort.Direction.DESC, "id"),
                                        null
                                )
                        ).stream(),
                        query -> userService.countAllFilter(
                                enabled.getValue() != null ? Utilities.isYes(enabled.getValue()) : null,
                                name.getValue(),
                                mail.getValue(),
                                admin.getValue() != null ? Utilities.isYes(admin.getValue()) : null
                        )
                );
            }

            @Override
            public User add(User data) {
                return null;
            }

            @Override
            public User update(User data) {
                return null;
            }

            @Override
            public void delete(User data) {
            }
        };
    }


    @Override
    protected void buildFromCruidUI(DefaultCrudFormFactory<User> formFactory) {
    }

    @Override
    protected void configOtherComponents(Div divCustomizeBar) {

    }

    @Override
    protected void modifyBtnState() {
        if (object != null) {
            miView.setEnabled(true);
            if (object.isEnabled()) {
                miEdit.setEnabled(true);
                miDelete.setEnabled(true);
                miCreateToken.setEnabled(true);
            } else {
                miDelete.setEnabled(false);
                miEdit.setEnabled(false);
                miCreateToken.setEnabled(false);
            }
        } else {
            miEdit.setEnabled(false);
            miView.setEnabled(false);
            miDelete.setEnabled(false);
            miCreateToken.setEnabled(false);
        }
    }

    protected void delete() {
        try {
            if (object == null) {
                return;
            }

            userService.delete(object, userSetting);
            gridCrud.refreshGrid();

            new SuccessNotification(UI.getCurrent().getTranslation("action.delete"));
        } catch (MyException e) {
            new ErrorNotification(e.getMessage());
        }
    }

    private void generateToken() {
        try {
            if (object == null) {
                return;
            }

            Object aux = authenticationService.generateToken(object);

            new SuccessNotification(UI.getCurrent().getTranslation("action.save"));
        } catch (MyException e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("title.user");
    }
}
