package ru.hawkab.redmineintegration.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Класс, предоставляющий утилитарные методы
 *
 * @author olshansky
 * @since 04.06.2020
 */
public class AppUtils {

    /**
     * Преобразовать stacktrace в строку
     *
     * @param throwable исключение
     * @return stacktrace в строковом представлении
     */
    public static String convertStackTraceToString(Throwable throwable) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

}
