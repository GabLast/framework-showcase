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
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;

import java.io.File;

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
        add(new Anchor("https://www.linkedin.com/in/gabriel-marte-899417276/", "LinkedIn"));

        //new File() sets the current relative path to the project root on the current version
        //due to this, we have to add the resources folder path to the file path
        String filename = "gabriel-cv.pdf";
        String currentRelativePath = "src/main/resources";
        String pathToFile = currentRelativePath + "/other/" + filename;
//        System.out.println("PATH: " + pathToFile);
//        System.out.println("file path: " + new File(pathToFile).getAbsolutePath());
        Anchor cv = new Anchor(DownloadHandler.forFile(new File(pathToFile)), "Gabriel's CV");
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
