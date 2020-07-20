package ru.hawkab.redmineintegration.util;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hawkab.redmineintegration.annotation.Bijection;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.hawkab.redmineintegration.Constants.*;

/**
 * Утилита, предоставляющая возможность интерполяции строк из переданной объектной модели
 *
 * @author olshansky
 * @since 06.06.2020
 */
public class InterpolateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterpolateUtil.class);

    /**
     * Интерполировать строку из переданной объектной модели
     *
     * <p/>
     * <code>
     *      UserEntity user = new UserEntity();<p>
     *      user.displayName = "Иван";<p>
     *      InterpolateUtil.interpolate("Привет, ${user.displayName}.", user);<p>
     * </code>
     * <p>Вернёт: "Привет, Иван."</p>
     *
     * @param input Входная строка, пример: "Привет, ${user.displayName}."
     * @param object объект произвольного класса
     * @param entityName наименование переданного объекта, имя корневой сущности
     * @return Входная строка, в которой переменные в размеченных местах заменены на значения из объекта
     */
    public static String interpolate(String input, Object object, String entityName) {

        Set<Integer> knownObjects = new HashSet<>();
        Map<String, String> mapByObject = getMapByObject(object, entityName, knownObjects);
        knownObjects.clear();
        return replaceAll(input,
                mapByObject,
                "\\$\\{",
                "\\}");
    }

    /**
     * Заменить переменные в тексте на
     *
     * @param input Входная строка, пример: "Привет, ${user.displayName}."
     * @param params Ассоциативный массив, где ключ может быть равен, к примеру: "user.displayName",
     *               а значение равно "Иван"
     * @param leading Регулярное выражение, префикс разметки переменных в тексте, к примеру <b>${</b>var}
     * @param trailing Регулярное выражение, постфикс разметки переменных в тексте, к примеру ${var<b>}</b>
     * @return Входная строка, в которой переменные в размеченных местах заменены на значения из ассоциативного массива
     */
    static String replaceAll(String input,
                                     Map<String, String> params,
                                     String leading,
                                     String trailing) {
        if (Objects.isNull(input)) {
            input = "";
        }
        if (Objects.isNull(params)) {
            params = new HashMap<>();
        }
        String pattern = StringUtils.EMPTY;
        if (Objects.nonNull(leading)) {
            pattern += leading;
        } else {
            return input;
        }
        pattern += INTERPOLATE_REGEX_PATTERN;
        if (Objects.nonNull(trailing)) {
            pattern += trailing;
        } else {
            return input;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        boolean result = m.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                String replacement = params.get(m.group(1));
                if (replacement == null) {
                    replacement = NOT_SPECIFIED;
                }
                m.appendReplacement(sb, replacement);
                result = m.find();
            } while (result);
            m.appendTail(sb);
            return sb.toString();
        }
        return input;
    }

    /**
     * Преобразовать многомерную структуру объекта в одномерную в виде
     * ассоциативного массива (ключ-значение), пример использования:<p>
     * UserEntity user = new User();<p>
     * user.id = 1;<p>
     * user.login = "admin";<p>
     * getMapByObject(user, "user");
     *
     * <p>Результат:</p>
     * Map.Entry[0] key: "user.id", value: "1"<p>
     * Map.Entry[1] key: "user.login", value: "admin"
     *
     *
     * @param obj объект
     * @param prefix наименование корневого элемента
     * @param knownObjects Защита от зацикливания рекурсивного выполнения метода -
     *                            набор хэшей объектов, которые уже фигурируют в результате
     * @return ассоциативный массив, где в качестве ключа будет указано название поля,
     * а в значении массива - значение соответствующего поля переданного объекта
     */
    static Map<String, String> getMapByObject(Object obj, String prefix, Set<Integer> knownObjects) {
        Map<String, String> result = new HashMap<>();
        if (Objects.nonNull(obj)) {
            if (Objects.isNull(knownObjects)) {
                knownObjects = new HashSet<>();
            }
            if (Objects.isNull(prefix)) {
                prefix = obj.getClass().getSimpleName();
                prefix = prefix.substring(0,1).toLowerCase() + prefix.substring(1);
            }
            knownObjects.add(obj.hashCode());
            Set<Integer> finalKnownObjects = knownObjects;
            String finalPrefix = prefix;
            Arrays.stream(obj.getClass().getDeclaredFields()).forEach(field -> {
                field.setAccessible(true);

                if (isBijection(field) ||
                        finalKnownObjects.contains(field.hashCode())) {
                    // 1. Мы вручную пометили определённое поле, а значит, не хотим, чтобы оно обрабатывалось
                    // 2. Алгоритм выявил, что данный объект уже есть в истории stacktrace, а значит, не хотим,
                    // чтобы объект обрабатывался повторно
                    return;
                }

                finalKnownObjects.add(field.hashCode());
                try {
                    Object fieldValue = getFieldValue(field, obj);
                    if (Objects.nonNull(fieldValue) && fieldValue.getClass().isArray()) {
                        Object[] fieldValueArray = (Object[]) fieldValue;
                        for (int i = 0; i < fieldValueArray.length; i++) {
                            result.putAll(getMapByObject(fieldValueArray[i],
                                    finalPrefix + "." + field.getName() + "[" + i + "]",
                                    finalKnownObjects));
                            i++;
                        }
                    } else if (Objects.nonNull(fieldValue) && fieldValue instanceof Collection) {
                        Iterator iterator = ((Collection) fieldValue).iterator();
                        int i = 0;
                        while (iterator.hasNext()) {
                            Object next = iterator.next();
                            result.putAll(getMapByObject(next,
                                    finalPrefix + "." + field.getName() + "[" + i + "]",
                                    finalKnownObjects));
                            i++;
                        }
                    } else if (Objects.nonNull(fieldValue) && isNotTopLevelHierarchy(fieldValue)) {
                        result.putAll(getMapByObject(fieldValue,
                                finalPrefix + "." + field.getName(),
                                finalKnownObjects));
                    } else {
                        result.put(finalPrefix + "." + field.getName(), String.valueOf(fieldValue));
                    }
                } catch (IllegalAccessException ex) {
                    LOGGER.error(GET_MAP_FROM_OBJECT_ERROR_MESSAGE, ex);
                }
            });
        }
        return result;
    }

    /**
     * Проверить, что родитель данного класса не является Object или любым другим
     * стандартным классом из пакета java.lang
     *
     * @param object объект
     * @return флаг, определяющий, что данный объект не является Object или любым другим
     * стандартным классом из пакета java.lang
     */
    static boolean isNotTopLevelHierarchy(Object object) {
        if (Objects.nonNull(object)) {
            Class<?> superclass = object.getClass().getSuperclass();
            if (Objects.nonNull(superclass)) {
                return !superclass.getCanonicalName().startsWith("java.lang");
            }
        }
        return false;
    }


    /**
     * Получить значение определённого поля у переданного объекта
     *
     * @param field поле которое нужно прочитать из объекта
     * @param object переданный объект
     * @return Значение поля
     * @throws IllegalAccessException  Ошибка, возбуждаемая если для поле обеспечивается
     * контроль доступа и оно недоступно для прямого воздействия
     */
    public static Object getFieldValue(Field field, Object object) throws IllegalAccessException {
        if (Objects.nonNull(object)) {
            object = initializeHibernateProxy(object);
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }

    /**
     * Произвести инициализацию немедленно, если был передан HibernateProxy в режиме
     * ленивой инициализации
     *
     * @param entity переданный объект, возможно сущность Hibernate
     * @return Исходная реализация проксированного объекта
     */
    private static Object initializeHibernateProxy(Object entity) {
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

    /**
     * Метод, определяющий, что данное поле имеет взаимную зависимость с другим полем
     *
     * @param field поле сущности
     * @return Флаг, определяющий, что переданное поле имеет циклическую связь
     */
    static boolean isBijection(Field field) {
        if (Objects.isNull(field)) {
            return false;
        }
        Bijection bijection = field.getAnnotation(Bijection.class);
        return Objects.nonNull(bijection);
    }

}
