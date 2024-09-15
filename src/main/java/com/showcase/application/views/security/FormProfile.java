package com.showcase.application.views.security;

import com.showcase.application.config.security.SecurityUtils;
import com.showcase.application.models.security.Permit;
import com.showcase.application.models.security.Profile;
import com.showcase.application.models.security.ProfilePermit;
import com.showcase.application.services.security.PermitService;
import com.showcase.application.services.security.ProfilePermitService;
import com.showcase.application.services.security.ProfileService;
import com.showcase.application.utils.MyException;
import com.showcase.application.views.MainLayout;
import com.showcase.application.views.generics.BaseForm;
import com.showcase.application.views.generics.notifications.ErrorNotification;
import com.showcase.application.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "security/form-profile/:id?/:view?", layout = MainLayout.class)
@RolesAllowed(value = {Permit.PROFILE_CREATE, Permit.PROFILE_EDIT, Permit.PROFILE_VIEW})
public class FormProfile extends BaseForm<Profile> {

    private final ProfileService profileService;
    private final PermitService permitService;
    private final ProfilePermitService profilePermitService;

    private TextField tfName;
    private TextArea tfDescription;
    private List<Permit> details;

    private TreeGrid<Permit> gridDetails;

    public FormProfile(ProfileService profileService, PermitService permitService, ProfilePermitService profilePermitService) {
        super(BaseForm.TYPE_TABS_AS_DETAILS);
        this.profileService = profileService;
        this.permitService = permitService;
        this.profilePermitService = profilePermitService;

        objectToSave = new Profile();
        details = new ArrayList<>();
    }

    @Override
    protected void setComponentValues() {
        gridDetails.setItems(
                permitService.findAllByEnabledAndPermitFatherIsNull(true),
                it -> permitService.findAllByEnabledAndPermitFather(true, it));
    }

    @Override
    protected void buildComponents() {
        tfName = new TextField(UI.getCurrent().getTranslation("form.profile.name"));
        tfName.setRequired(true);
        tfName.setRequiredIndicatorVisible(true);
        tfName.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfName.setPlaceholder(UI.getCurrent().getTranslation("form.profile.name") + "...");
        tfName.setSizeFull();

        tfDescription = new TextArea(UI.getCurrent().getTranslation("form.profile.description"));
        tfDescription.setRequired(true);
        tfDescription.setRequiredIndicatorVisible(true);
        tfDescription.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
        tfDescription.setPlaceholder(UI.getCurrent().getTranslation("form.profile.description") + "...");
        tfDescription.setSizeFull();

        Component details = buildDetails();

        formLayout.add(tfName, 2);
        formLayout.add(tfDescription, 2);
        formLayout.add(details, 4);

        Tab tabDetails = new Tab(UI.getCurrent().getTranslation("form.profile.details"));
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

        verticalLayout.addAndExpand(buildGrid());

        return verticalLayout;
    }

    private Component buildGrid() {
        gridDetails = new TreeGrid<>();
        gridDetails.setWidthFull();
        gridDetails.setSelectionMode(Grid.SelectionMode.NONE);
        gridDetails.setColumnReorderingAllowed(false);
        gridDetails.addComponentHierarchyColumn(it -> {
            Checkbox check = new Checkbox();
            check.setReadOnly(view);
            check.setValue(details.stream().anyMatch(child -> child.getCode().equals(it.getCode())));
            check.addValueChangeListener(event -> {

                List<Permit> children = permitService.findAllByEnabledAndPermitFather(true, it);

                if (!event.getValue()) {
                    gridDetails.collapse(it);
                    details.removeIf(child -> children.stream().anyMatch(child2 -> child.getCode().equals(child2.getCode())));
                    details.removeIf(child -> it.getCode().equals(child.getCode()));
                } else {
                    gridDetails.expand(it);
                    details.add(it);
                    details.removeIf(child -> children.stream().anyMatch(child2 -> child.getCode().equals(child2.getCode())));
                    details.addAll(children);
                }

//                System.out.println("Current Permits:");
//                details.forEach(value -> System.out.println(value.getCode()));
//                System.out.println("\n\n");

                gridDetails.getDataProvider().refreshAll();
            });

            return check;
        }).setWidth("150px").setFlexGrow(0).setResizable(true);
        gridDetails.addColumn(data -> {
                    String[] keys = data.getNameI18().split(",");
                    if (keys.length == 2) {
                        return UI.getCurrent().getTranslation(keys[1], UI.getCurrent().getTranslation(keys[0]));
                    } else {
                        return UI.getCurrent().getTranslation(data.getNameI18());
                    }
                })
                .setHeader(UI.getCurrent().getTranslation("form.profile.name")).setWidth("200px").setFlexGrow(1).setResizable(true).setSortable(false);
        gridDetails.addColumn(data -> {
                    String[] keys = data.getDescriptionI18().split(",");
                    if (keys.length == 2) {
                        return UI.getCurrent().getTranslation(keys[1], UI.getCurrent().getTranslation(keys[0]));
                    } else {
                        return UI.getCurrent().getTranslation(data.getDescriptionI18());
                    }
                })
                .setHeader(UI.getCurrent().getTranslation("form.profile.description")).setWidth("200px").setFlexGrow(1).setResizable(true).setSortable(false);

        return gridDetails;
    }

    @Override
    protected void enableVisualizationOnly() {
        tfName.setReadOnly(true);
        tfDescription.setReadOnly(true);
    }

    @Override
    protected void fillFields() {
        if (objectToSave == null || objectToSave.getId() == 0L) {
            return;
        }

        tfName.setValue(objectToSave.getName());
        tfDescription.setValue(objectToSave.getDescription());
        details = profilePermitService.findAllByEnabledAndProfile(true, objectToSave).stream().map(ProfilePermit::getPermit).collect(Collectors.toList());
        gridDetails.getDataProvider().refreshAll();

        if (view) {
            enableVisualizationOnly();
        }
    }

    @Override
    protected boolean verifyFields() {
        boolean isError = false;

        if ((tfName.isRequired() || tfName.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfName.getValue()) || tfName.isInvalid())) {
            isError = true;
            tfName.setInvalid(true);
        } else {
            tfName.setInvalid(false);
        }

        if ((tfDescription.isRequired() || tfDescription.isRequiredIndicatorVisible()) && (StringUtils.isBlank(tfDescription.getValue()) || tfDescription.isInvalid())) {
            isError = true;
            tfDescription.setInvalid(true);
        } else {
            tfDescription.setInvalid(false);
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

            objectToSave.setName(tfName.getValue().trim());
            objectToSave.setDescription(tfDescription.getValue().trim());

            objectToSave = profileService.create(objectToSave, details, userSetting);

            new SuccessNotification(UI.getCurrent().getTranslation("action.save.object", objectToSave.toString()));

            UI.getCurrent().getPage().getHistory().back();
        } catch (MyException e) {

            if (objectToSave != null && objectToSave.getId() != 0L) {
                objectToSave = (profileService.get(objectToSave.getId())).orElse(null);
            }

            if (objectToSave == null) {
                objectToSave = new Profile();
            }

            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        String title;

        if (objectToSave == null || objectToSave.getId() == 0L) {
            title = UI.getCurrent().getTranslation("create") + " " + UI.getCurrent().getTranslation("form.profile.title");
        } else if (objectToSave.getId() != 0L && !view) {
            title = UI.getCurrent().getTranslation("edit") + " " + UI.getCurrent().getTranslation("form.profile.title");
        } else if (objectToSave.getId() != 0L && view) {
            title = UI.getCurrent().getTranslation("view") + " " + UI.getCurrent().getTranslation("form.profile.title");
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

        Optional<Profile> header = profileService.get(headerId);
        if (header.isEmpty()) {
            event.getUI().getPage().getHistory().back();
            return;
        }

        //check params
        Optional<String> viewParam = routeParameters.get("view");

        objectToSave = header.get();
        view = viewParam.isPresent() && !StringUtils.isBlank(viewParam.get()) && viewParam.get().equalsIgnoreCase("1");

        if (view) {
            if (!SecurityUtils.isAccessGranted(Permit.PROFILE_VIEW)) {
                event.getUI().getPage().getHistory().back();
            }
        } else if (!SecurityUtils.isAccessGranted(Permit.PROFILE_EDIT)) {
            event.getUI().getPage().getHistory().back();
        }
    }
}
