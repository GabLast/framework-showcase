package com.showcase.application.config.security;

import com.showcase.application.views.general.HomeView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;
import com.vaadin.flow.router.internal.DefaultErrorHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Tag(Tag.DIV)
@DefaultErrorHandler
@Slf4j
public class RouteHandler extends RouteNotFoundError/* implements AuthenticationEntryPoint*/ {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
//        log.debug("Not existing view requested with name: /" + event.getLocation().getPath());
//        log.debug("Redirect user to /start");
        event.forwardTo(HomeView.class);
        return HttpServletResponse.SC_NOT_FOUND;
    }

//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
//    }
}
