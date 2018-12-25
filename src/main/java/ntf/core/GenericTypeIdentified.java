package ntf.core;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Optional;

/**
 * interface版本的TypeToken
 * @param <T>
 */
public interface GenericTypeIdentified<T> {


    default Type getType() {
        return getType(getClass());
    }

    default Type getType(Class<?> clazz) {

        Type[] interfaces = clazz.getGenericInterfaces();
        Optional<Type> opInterfaces = Arrays.stream(interfaces)
                .filter(i -> i instanceof ParameterizedType &&
                        ((ParameterizedType) i).getRawType().equals(GenericTypeIdentified.class))
                .findAny();
        Class<?> superClazz = clazz.getSuperclass();
        if (!opInterfaces.isPresent()) {
            if (superClazz == Object.class)
                return null;
            Type tmpT = getType(superClazz);
            if (tmpT == null || tmpT instanceof TypeVariable) { //泛型继承被擦除
                if (clazz.getGenericSuperclass() instanceof ParameterizedType)
                    // 直接拿取最近父类泛型，这里有坑，因为没有判断继承关系中，如果有多个泛型参数，所要识别的泛型参数的位置
                    // 这里直接用了下标0的具体类型
                    return ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
                return null;
            }
            return tmpT;
        }
        return ((ParameterizedType) opInterfaces.get()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    default Class<T> getGenericTypeClass() {
        Type genericType = getType();
        if (genericType == null)
            return null;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            return (Class<T>) pt.getRawType();
        } else {
            return (Class<T>) genericType;
        }

    }
}
