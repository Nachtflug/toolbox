package ntf.function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class BeanFunctions {

    public static <S, T> Function<S, T> beanMap(Class<T> targetType) {
        return s -> {
            T t = BeanUtils.instantiate(targetType);
            BeanUtils.copyProperties(s, t);
            return t;
        };
    }

    public static <T> Function<String, PropertyDescriptor> propertyDescriptorMapperOf(Class<T> targetType) {
        return fd -> BeanUtils.getPropertyDescriptor(targetType, fd);
    }


    public static List<Field> getAllFields(Class clazz) {
        List<Field> fs= Lists.newArrayList();
        //循环截取所有父类属性
        for (; clazz != Object.class; clazz=clazz.getSuperclass()) {
            try {
                fs.addAll(Lists.newArrayList(clazz.getDeclaredFields()));
            } catch (Exception ignore) {
            }
        }
        return fs;
    }

    public static Field getFieldRecursively(Class clazz, String fieldStr) {
        //循环截取所有父类属性
        for(; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getName().equals(fieldStr))
                        return field;
                }
            } catch (Exception ignore) {}
        }
        return null;
    }

    public static <T> Map<Object, T> identifyByKey(Iterable<T> items, String key) {

        if (items == null)
            return null;
        Iterator<T> iter = items.iterator();
        if (!iter.hasNext())
            return new HashMap<>();
        Class collectClass = iter.next().getClass();
        try {
            PropertyDescriptor keyPropertyDescriptor = Optional.of(collectClass)
                            .map(BeanFunctions::propertyDescriptorMapperOf)
                            .map(f -> f.apply(key))
                            .orElse(null);
            if (keyPropertyDescriptor == null)
                return null;
            Method getter = keyPropertyDescriptor.getReadMethod();
            HashMap<Object, T> ret = Maps.newHashMap();

            for (T item : items) {
                Object o = getter.invoke(item);
                ret.put(o, item);
            }
            return ret;
        } catch (Exception ignore) {

        }
        return null;
    }

    public static <T> Map<Object, T> identifyByKey(T[] items, String key) {

        if (items == null)
            return null;
        return identifyByKey(Arrays.asList(items), key);
    }

}
