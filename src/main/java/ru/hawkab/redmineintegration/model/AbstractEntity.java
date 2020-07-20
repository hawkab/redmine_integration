package ru.hawkab.redmineintegration.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Абстрактный класс, обеспечивающий реализацию некоторых методов по-умолчанию
 *
 * @author olshansky
 * @since 04.06.2020
 */
public abstract class AbstractEntity {

    /**
     * Конвертировать объект в строку
     *
     * @return Строковое представление всех полей и их значений с учётом наследования классов
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
