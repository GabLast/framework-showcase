package com.showcase.application.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.Id;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

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
        if (date == null) {
            return "";
        }

        if (StringUtils.isBlank(format)) {
            format = "dd/MM/yyyy hh:mm a";
        }

        SimpleDateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat(format);
        } catch (IllegalArgumentException e) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        }

        if (StringUtils.isNotBlank(timeZone)) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        } else {
            dateFormat.setTimeZone(TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE));
        }

        return dateFormat.format(date);
    }

    public static String formatDecimal(BigDecimal value) {
        DecimalFormat df = new DecimalFormat();
        df.setDecimalSeparatorAlwaysShown(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        return value != null ? df.format(value) : "0.00";
    }

    public static String formatDecimal(BigDecimal value, int cantidadDecimales) {
        DecimalFormat df = new DecimalFormat();
        df.setDecimalSeparatorAlwaysShown(cantidadDecimales > 0);
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMaximumFractionDigits(cantidadDecimales);
        df.setMinimumFractionDigits(cantidadDecimales);

        return value != null ? df.format(value) : "0.00";
    }

    public static String generateFileName(String name, String extension) {
        return name + System.currentTimeMillis() + "." + extension;
    }

//    public static StreamResource getStreamResource(ByteArrayOutputStream baos, String nombre, String extension) {
//        if (baos != null) {
//            return new StreamResource(nombre + System.currentTimeMillis() + extension, () -> new ByteArrayInputStream(baos.toByteArray()));
//        } else {
//            return null;
//        }
//    }
//
//    public static StreamResource getStreamResource(Document adjunto, String extension) {
//        if (adjunto != null) {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            try {
//                byteArrayOutputStream.write(adjunto.getFile());
//                byteArrayOutputStream.flush();
//                byteArrayOutputStream.close();
//
//                return new StreamResource(adjunto.getName() + System.currentTimeMillis() + extension, () -> new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            return null;
//        }
//    }

    public static <T> String getIdName(Class<T> entityType) {
        if (entityType == null) {
            return null;
        }

        Field idField = getIdField(entityType);
        return idField != null ? idField.getName() : null;
    }

    public static <T> Sort generarSortDeBusquedaTabla(Class<T> entityType, List<GridSortOrder<T>> sortOrder, Sort defaultSort, Map<String, String> mapKeysOrderObjects) {
        Sort sort = defaultSort;
        if (sortOrder != null && !sortOrder.isEmpty()) {
            sort = Sort.by(new ArrayList<>(List.of(
                    sortOrder.stream().map(it -> new Sort.Order(
                            it.getDirection() == SortDirection.DESCENDING ? Sort.Direction.DESC : Sort.Direction.ASC,
                            mapKeysOrderObjects != null && mapKeysOrderObjects.containsKey(it.getSorted().getKey()) ? mapKeysOrderObjects.get(it.getSorted().getKey()) : it.getSorted().getKey()
                    )).toArray(Sort.Order[]::new)
            )));
        } else if (defaultSort == null) {
            String idName = getIdName(entityType);
            if (idName != null && !idName.isEmpty()) {
                sort = Sort.by(Sort.Direction.ASC, idName);
            }
        }
        return sort;
    }

    public static List<String> listBooleanI18YesNo() {
        return List.of(UI.getCurrent().getTranslation("yes"), UI.getCurrent().getTranslation("no"));
    }

    public static boolean isYes(String status) {
        if (StringUtils.isBlank(status)) {
            return false;
        }

        return UI.getCurrent().getTranslation("yes").equalsIgnoreCase(status);
    }

    public static List<String> listBooleanI18ActiveDisabled() {
        return List.of(UI.getCurrent().getTranslation("active"), UI.getCurrent().getTranslation("disabled"));
    }

    public static boolean isActive(String status) {
        if (StringUtils.isBlank(status)) {
            return false;
        }

        return UI.getCurrent().getTranslation("active").equalsIgnoreCase(status);
    }

    public static String formatBooleanYes(boolean data) {
        String value = "";
        if (data) {
            value = UI.getCurrent().getTranslation("yes");
        } else {
            value = UI.getCurrent().getTranslation("no");
        }
        return value;
    }

    public static String formatBooleanActive(boolean data) {
        String value = "";
        if (data) {
            value = UI.getCurrent().getTranslation("active");
        } else {
            value = UI.getCurrent().getTranslation("disabled");
        }
        return value;
    }

    public static List<String> listMonthsI18() {
        return List.of(
                UI.getCurrent().getTranslation("january"),
                UI.getCurrent().getTranslation("february"),
                UI.getCurrent().getTranslation("march"),
                UI.getCurrent().getTranslation("april"),
                UI.getCurrent().getTranslation("may"),
                UI.getCurrent().getTranslation("june"),
                UI.getCurrent().getTranslation("july"),
                UI.getCurrent().getTranslation("august"),
                UI.getCurrent().getTranslation("september"),
                UI.getCurrent().getTranslation("october"),
                UI.getCurrent().getTranslation("november"),
                UI.getCurrent().getTranslation("december")
        );
    }

    public static String capitalizeEveryLetterOfString(String input) {
        input = input.toLowerCase();
        return Arrays.stream(input.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static void runScript(String path, Connection connection) throws Exception {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setSendFullScript(false);
        scriptRunner.setStopOnError(true);
        scriptRunner.runScript(new java.io.FileReader(path));
    }

    public static SecretKey generateJWTKey(String key) {
        //docs at https://github.com/jwtk/jjwt
//        return Keys.hmacShaKeyFor(key.getBytes());
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(key.getBytes()));
    }

//    public static IvParameterSpec generateIv() {
//        byte[] iv = new byte[16];
//        new SecureRandom().nextBytes(iv);
//        return new IvParameterSpec(iv);
//    }
//
//    private static String getEncryptionAlgorithm() {
//        return "AES/CBC/PKCS5Padding";
//    }
//
//    public static SecretKey generateSecretKey()  {
//        try {
//            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//            keyGenerator.init(128);
//            return keyGenerator.generateKey();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String encrypt(String input, SecretKey key) {
//        try {
//            Cipher cipher = Cipher.getInstance(getEncryptionAlgorithm());
//            cipher.init(Cipher.ENCRYPT_MODE, key, generateIv());
//            byte[] cipherText = cipher.doFinal(input.getBytes());
//            return Base64.getEncoder().encodeToString(cipherText);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return "";
//        }
//    }
//
//    public static String decrypt(String cipherText, SecretKey key) {
//        try {
//
//            Cipher cipher = Cipher.getInstance(getEncryptionAlgorithm());
//            cipher.init(Cipher.ENCRYPT_MODE, key, generateIv());
//            byte[] plainText = cipher.doFinal(Base64.getDecoder()
//                    .decode(cipherText));
//            return new String(plainText);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return "";
//        }
//    }
}
