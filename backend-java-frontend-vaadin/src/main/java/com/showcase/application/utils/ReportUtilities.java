package com.showcase.application.utils;

import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.ExpressionHelper;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalTextAlign;
import ar.com.fdvs.dj.domain.constants.VerticalTextAlign;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;

import java.awt.*;

public class ReportUtilities {
    public static final Style baseStyle = new StyleBuilder(true)
            .setFont(new Font(10, Font._FONT_ARIAL, false, false, false))
            .setBackgroundColor(Color.WHITE)
            .setTextColor(Color.BLACK)
            .setHorizontalTextAlign(HorizontalTextAlign.LEFT)
            .build();

    public static final Style titleStyle = new StyleBuilder(true)
            .setFont(new Font(13, Font._FONT_ARIAL, true, false, false))
            .setBackgroundColor(Color.WHITE)
            .setTextColor(Color.BLACK)
            .setHorizontalTextAlign(HorizontalTextAlign.CENTER)
            .setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT)
            .setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT)
            .setPaddingBottom(7).setPaddingTop(5).build();

    public static final Style prettyPageHeadersStyle = new StyleBuilder(true)
            .setFont(new Font(10, Font._FONT_ARIAL, true, false, false))
            .setTransparent(false)
            .setBackgroundColor(new Color(160, 255, 166)) //https://rgbcolorpicker.com/
            .setTextColor(Color.BLACK)
            .setBorder(Border.THIN())
            .setPaddingLeft(1)
            .setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT)
            .setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT)
            .setHorizontalTextAlign(HorizontalTextAlign.CENTER)
            .setVerticalTextAlign(VerticalTextAlign.MIDDLE).build();

    public static final Style prettyColumnHeaderStyle = new StyleBuilder(true)
            .setFont(new Font(10, Font._FONT_ARIAL, true, false, false))
            .setTransparent(false)
            .setBackgroundColor(new Color(160, 255, 166)) //https://rgbcolorpicker.com/
            .setTextColor(Color.BLACK)
            .setBorder(Border.THIN())
            .setPaddingLeft(1)
            .setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT)
            .setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT)
            .setHorizontalTextAlign(HorizontalTextAlign.CENTER)
            .setVerticalTextAlign(VerticalTextAlign.MIDDLE).build();

    public static final Style detailsStyle = new StyleBuilder(true)
            .setFont(new Font(10, Font._FONT_ARIAL, true, false, false))
            .setTransparent(false)
            .setTextColor(Color.BLACK)
            .setBorder(Border.THIN())
            .setPaddingLeft(2)
            .setPaddingRight(1)
            .setPaddingTop(1)
            .setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT)
            .setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT)
            .setVerticalTextAlign(VerticalTextAlign.MIDDLE).build();

    public static final Style columnLeft = new StyleBuilder(true)
            .setHorizontalTextAlign(HorizontalTextAlign.LEFT).build();
    public static final Style columnCenter = new StyleBuilder(true)
            .setHorizontalTextAlign(HorizontalTextAlign.CENTER).build();
    public static final Style columnRight = new StyleBuilder(true)
            .setHorizontalTextAlign(HorizontalTextAlign.RIGHT).build();

    public static final Integer NORMAL_WIDTH = 85;
    public static final Integer DATE_WIDTH = 60;
    public static final Integer DATE_TIME_WIDTH = 75;


    public static AutoText generateAutoText(String msg, byte type, byte position, byte alignment, int width, int width2, Style style) {
        AutoText autoText = new AutoText(type, position, HorizontalBandAlignment.buildAligment(alignment));
        autoText.setMessageKey(msg);
        autoText.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
        autoText.setWidth(width);
        autoText.setWidth2(width2);

        if (style != null) {
            autoText.setStyle(style);
        } else {
            autoText.setStyle(baseStyle);
        }

        return autoText;
    }

}
