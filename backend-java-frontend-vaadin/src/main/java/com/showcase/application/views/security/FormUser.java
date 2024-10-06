package com.showcase.application.views.security;

import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.Profile;
import com.showcase.application.models.security.ProfileUser;
import com.showcase.application.models.security.User;
import com.showcase.application.services.security.ProfileService;
import com.showcase.application.services.security.ProfileUserService;
import com.showcase.application.services.security.UserService;
import com.showcase.application.utils.MyException;
import com.showcase.application.views.MainLayout;
import com.showcase.application.views.generics.BaseForm;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.showcase.application.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(value = "vaadin/security/form-user/:id?/:view?", layout = MainLayout.class)
@RolesAllowed(value = {Permit.USER_CREATE, Permit.USER_EDIT, Permit.USER_VIEW})
public class FormUser extends BaseForm<User> {

    private final UserService userService;
    private final ProfileService profileService;
    private final ProfileUserService profileUserService;

    private TextField tfUsername, tfName;
    private PasswordField tfPassword;
    private EmailField tfMail;
    private Checkbox checkAdmin;

    private List<ProfileUser> listDelete;
    private List<ProfileUser> details;
    private Grid<ProfileUser> gridDetails;

    private Button btnAdd;
    private ComboBox<Profile> cbProfile;

    public FormUser(UserService userService, ProfileService profileService, ProfileUserService profileUserService) {
        super(BaseForm.TYPE_TABS_AS_DETAILS);
        this.userService = userService;
        this.profileService = profileService;
        this.profileUserService = profileUserService;

        objectToSave = new User();
        details = new ArrayList<>();
        listDelete = new ArrayList<>();
    }

    @Override
    protected void setComponentValues() {
        cbProfile.setItems(profileService.findAllByEnabled(true));
    }

    @Override
    protected void buildComponents() {
        tfUsername = new TextField(UI.getCurrent().getTranslation("form.user.username"));
        tfUsername.setRequired(true);
        tfUsername.setRequiredIndicatorVisible(true);
        tfUsername.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfUsername.setPlaceholder(UI.getCurrent().getTranslation("form.user.username") + "...");
        tfUsername.setSizeFull();
        tfUsername.addValueChangeListener(event -> {
            if (tfUsername.isInvalid()) {
                return;
            }

            User tmp = userService.findByUsername(tfUsername.getValue().trim());
            if (tmp != null && !objectToSave.getId().equals(tmp.getId())) {
                tfUsername.setErrorMessage(UI.getCurrent().getTranslation("error.usernametaken"));
                tfUsername.setInvalid(true);
            } else {
                tfUsername.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
                tfUsername.setInvalid(false);
            }
        });

        tfPassword = new PasswordField(UI.getCurrent().getTranslation("form.user.password"));
        tfPassword.setRequired(true);
        tfPassword.setRequiredIndicatorVisible(true);
        tfPassword.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfPassword.setPlaceholder(UI.getCurrent().getTranslation("form.user.password") + "...");
        tfPassword.setSizeFull();

        tfName = new TextField(UI.getCurrent().getTranslation("name"));
        tfName.setRequired(true);
        tfName.setRequiredIndicatorVisible(true);
        tfName.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfName.setPlaceholder(UI.getCurrent().getTranslation("name") + "...");
        tfName.setSizeFull();

        tfMail = new EmailField(UI.getCurrent().getTranslation("form.user.mail"));
        tfMail.setRequired(true);
        tfMail.setRequiredIndicatorVisible(true);
        tfMail.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfMail.setPlaceholder(UI.getCurrent().getTranslation("form.user.mail") + "...");
        tfMail.setSizeFull();
        tfMail.addValueChangeListener(event -> {
            if (tfMail.isInvalid()) {
                return;
            }

            User tmp = userService.findByMail(tfMail.getValue().trim());
            if (tmp != null && !objectToSave.getId().equals(tmp.getId())) {
                tfMail.setErrorMessage(UI.getCurrent().getTranslation("error.mailtaken"));
                tfMail.setInvalid(true);
            } else {
                tfMail.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
                tfMail.setInvalid(false);
            }
        });

        checkAdmin = new Checkbox(UI.getCurrent().getTranslation("form.user.admin"));

        Component details = buildDetails();

        formLayout.add(tfUsername, 1);
        formLayout.add(tfPassword, 1);
        formLayout.add(tfName, 1);
        formLayout.add(tfMail, 1);
        formLayout.add(checkAdmin);
        formLayout.add(details, 4);

        Tab tabDetails = new Tab(UI.getCurrent().getTranslation("form.user.details"));
        tabs.addTabAsFirst(tabDetails);
        tabs.addSelectedChangeListener(event -> {

            if (event.getSelectedTab() == null) {
                return;
            }

            if (event.getSelectedTab().equals(tabDetails)) {
                contentDiv.add(details);
            }
        });
    }

    private Component buildDetails() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);

        verticalLayout.add(buildDetailsForms());
        verticalLayout.addAndExpand(buildGrid());

        return verticalLayout;
    }

    private boolean verifyDetails() {
        boolean isError = false;

        if ((cbProfile.isRequired() || cbProfile.isRequiredIndicatorVisible()) && cbProfile.getValue() == null) {
            isError = true;
            cbProfile.setInvalid(true);
        } else {
            cbProfile.setInvalid(false);
        }

        return isError;
    }

    private Component buildDetailsForms() {

        cbProfile = new ComboBox<>(UI.getCurrent().getTranslation("form.user.profile"));
        cbProfile.setRequired(true);
        cbProfile.setRequiredIndicatorVisible(true);
        cbProfile.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        cbProfile.setPlaceholder(UI.getCurrent().getTranslation("form.user.profile") + "...");
        cbProfile.setSizeFull();

        btnAdd = new Button(UI.getCurrent().getTranslation("add"), new Icon(VaadinIcon.PLUS));
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAdd.addClickListener(event -> {
            if (verifyDetails()) {
                return;
            }

            ProfileUser profileUser = new ProfileUser();
            profileUser.setProfile(cbProfile.getValue());

            details.add(profileUser);
            gridDetails.setItems(details);

            cbProfile.clear();
        });

        FormLayout form = new FormLayout();
        form.setSizeUndefined();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3),
                new FormLayout.ResponsiveStep("1200px", 4));
        form.add(cbProfile, 1);
        form.add(btnAdd, 1);

        return form;
    }

    private Component buildGrid() {
        gridDetails = new Grid<>();
        gridDetails.setWidthFull();
        gridDetails.setColumnReorderingAllowed(true);
        gridDetails.setSelectionMode(Grid.SelectionMode.NONE);
        gridDetails.setColumnReorderingAllowed(false);
        gridDetails.addColumn(ProfileUser::getProfile).setHeader(UI.getCurrent().getTranslation("form.user.profile"))
                .setWidth("12rem").setFlexGrow(0).setResizable(true).setSortable(false);
        gridDetails.addColumn(data -> data.getProfile().getDescription()).setHeader(UI.getCurrent().getTranslation("form.user.description"))
                .setWidth("12rem").setFlexGrow(1).setResizable(true).setSortable(false);
        gridDetails.addComponentColumn(data -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH));
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            button.setSizeFull();
            button.setEnabled(!view);
            button.addClickListener(event -> {
                if (data.getId() != 0L) {
                    listDelete.add(data);
                }
                details.remove(data);
                gridDetails.setItems(details);
            });

            return button;
        }).setKey("delete").setHeader("").setFlexGrow(0).setWidth("120px");

        return gridDetails;
    }

    @Override
    protected void enableVisualizationOnly() {
        tfUsername.setReadOnly(true);
        tfPassword.setReadOnly(true);
        tfMail.setReadOnly(true);
        tfName.setReadOnly(true);
        checkAdmin.setReadOnly(true);
        btnAdd.setEnabled(false);
    }

    @Override
    protected void fillFields() {
        if (objectToSave == null || objectToSave.getId() == 0L) {
            return;
        }

        tfUsername.setValue(objectToSave.getUsername());
        tfPassword.setValue(objectToSave.getPassword());
        tfName.setValue(objectToSave.getName());
        tfMail.setValue(objectToSave.getMail());
        checkAdmin.setValue(objectToSave.isAdmin());

        details = profileUserService.findAllByEnabledAndUser(true, objectToSave);
        gridDetails.setItems(details);

        if (view) {
            enableVisualizationOnly();
        }
    }

    @Override
    protected boolean verifyFields() {
        boolean isError = false;

        if ((tfUsername.isRequired() || tfUsername.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfUsername.getValue()) || tfUsername.isInvalid())) {
            isError = true;
            tfUsername.setInvalid(true);
        } else {
            tfUsername.setInvalid(false);
        }

        if ((tfPassword.isRequired() || tfPassword.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfPassword.getValue()) || tfPassword.isInvalid())) {
            isError = true;
            tfPassword.setInvalid(true);
        } else {
            tfPassword.setInvalid(false);
        }

        if ((tfName.isRequired() || tfName.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfName.getValue()) || tfName.isInvalid())) {
            isError = true;
            tfName.setInvalid(true);
        } else {
            tfName.setInvalid(false);
        }

        if ((tfMail.isRequired() || tfMail.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfMail.getValue()) || tfMail.isInvalid())) {
            isError = true;
            tfMail.setInvalid(true);
        } else {
            tfMail.setInvalid(false);
        }

        if (details == null || details.isEmpty()) {
            isError = true;
            new ErrorNotification(UI.getCurrent().getTranslation("error.empty.details"));
            gridDetails.focus();
        }

        return isError;
    }

    @Override
    protected void save() {
        try {

            if (verifyFields()) {
                return;
            }

            objectToSave.setUsername(tfUsername.getValue().trim());
            objectToSave.setPassword(tfPassword.getValue());
            objectToSave.setName(tfName.getValue().trim());
            objectToSave.setMail(tfMail.getValue().trim());
            objectToSave.setAdmin(checkAdmin.getValue());

            objectToSave = userService.saveUser(objectToSave, details, listDelete, userSetting);

            new SuccessNotification(UI.getCurrent().getTranslation("action.save.object", objectToSave.toString()));

            UI.getCurrent().getPage().getHistory().back();
        } catch (MyException e) {

            if (objectToSave != null && objectToSave.getId() != 0L) {
                objectToSave = (userService.get(objectToSave.getId())).orElse(null);
            }

            if (objectToSave == null) {
                objectToSave = new User();
            }

            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        String title;

        if (objectToSave == null || objectToSave.getId() == 0L) {
            title = UI.getCurrent().getTranslation("create") + " " + UI.getCurrent().getTranslation("form.user.title");
        } else if (objectToSave.getId() != 0L && !view) {
            title = UI.getCurrent().getTranslation("edit") + " " + UI.getCurrent().getTranslation("form.user.title");
        } else if (objectToSave.getId() != 0L && view) {
            title = UI.getCurrent().getTranslation("view") + " " + UI.getCurrent().getTranslation("form.user.title");
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

        Optional<User> header = userService.get(headerId);
        if (header.isEmpty()) {
            event.getUI().getPage().getHistory().back();
            return;
        }

        //check params
        Optional<String> viewParam = routeParameters.get("view");

        objectToSave = header.get();
        view = viewParam.isPresent() && !StringUtils.isBlank(viewParam.get()) && viewParam.get().equalsIgnoreCase("1");

        if (view) {
            if (!SecurityUtils.isAccessGranted(Permit.USER_VIEW)) {
                event.getUI().getPage().getHistory().back();
            }
        } else if (!SecurityUtils.isAccessGranted(Permit.USER_EDIT)) {
            event.getUI().getPage().getHistory().back();
        }
    }
}
