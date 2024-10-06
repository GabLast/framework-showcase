package com.showcase.application.views.general;

import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;

@Route(value = "vaadin/about", layout = MainLayout.class)
@PermitAll
public class AboutView extends VerticalLayout implements HasDynamicTitle {

    public AboutView() {
        setSpacing(false);

        Image img = new Image(GlobalConstants.LOGO, "placeholder");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("Framework Showcase");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph(UI.getCurrent().getTranslation("developedby") + ":"));
        add(new Paragraph("Gabriel José Marte Lantigua\t"));
        add(new Paragraph(UI.getCurrent().getTranslation("links") + ":"));
        add(new Anchor("https://github.com/GabLast", UI.getCurrent().getTranslation("github")));
        add(new Anchor("https://www.linkedin.com/in/gabriel-marte-899417276/", "Linkedin"));

        Anchor cv = new Anchor();
        cv.setText("Gabriel's CV");
        cv.getElement().setAttribute("download", "Gabriel-Marte-Curriculum");
        StreamResource streamResource = new StreamResource(
                "Gabriel-Marte-Curriculum.pdf",
                () -> getClass().getResourceAsStream("/other/gabriel-cv.pdf"));
        cv.setHref(streamResource);
        add(cv);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("title.about");
    }
}
