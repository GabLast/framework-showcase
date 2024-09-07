package com.showcase.application.utils;

import com.showcase.application.models.configurations.Document;
import com.vaadin.flow.server.StreamResource;
import jakarta.persistence.Id;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utilities {

    public static Logger getLogger(Class<?> aClass) {
        return LoggerFactory.getLogger(aClass);
    }

    public static <T> Field getIdField(Class<T> entityType) {
        Class<? super T> superclass = entityType.getSuperclass();
        if (superclass == null) {
            return null;
        }

        for (Field field : superclass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null;
    }

    public static <T> T setFieldValue(T entidad, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = entidad.getClass().getSuperclass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignored) {
            try {
                field = entidad.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw e;
            }
        }
        field.setAccessible(true);
        field.set(entidad, value);
        return entidad;
    }

    public static <T> Object getFieldValue(T object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = object.getClass().getSuperclass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignored) {
            try {
                field = object.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw e;
            }
        }
        field.setAccessible(true);
        return field.get(object);
    }

    public static String formatDate(Date date, String format, String timeZone) {
        //Si la fecha es nula devuelvo vacio.
        if (date == null) {
            return "";
        }


        if (StringUtils.isBlank(format)) {
            format = "dd/MM/yyyy hh:mm a";
        }

        //Formateador de fecha.
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        if (StringUtils.isNotBlank(timeZone)) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }

        return dateFormat.format(date);
    }

    public static StreamResource getStreamResource(ByteArrayOutputStream baos, String nombre, String extension) {
        if (baos != null) {
            return new StreamResource(nombre + System.currentTimeMillis() + extension, () -> new ByteArrayInputStream(baos.toByteArray()));
        } else {
            return null;
        }
    }

    public static StreamResource getStreamResource(Document adjunto, String extension) {
        if (adjunto != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byteArrayOutputStream.write(adjunto.getFile());
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();

                return new StreamResource(adjunto.getName() + System.currentTimeMillis() + extension, () -> new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
}
