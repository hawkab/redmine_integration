package ru.hawkab.redmineintegration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Метка, необходимая для того, чтобы дать возможность игнорировать
 * ссылку на поле с взаимной зависимостью сущностей
 *
 * @author olshansky
 * @since 05.06.2020
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bijection {

}
