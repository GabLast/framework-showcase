package com.showcase.application.views.generics.components;

import com.vaadin.flow.component.html.Span;

public class SpanTitle extends Span {

    public SpanTitle() {
        getStyle().set("font-size", "20px").set("font-weight", "600").set("line-height", "var(--lumo-line-height-m)");
    }

    public SpanTitle(String caption) {
        this();
        setText(caption);
    }
}
