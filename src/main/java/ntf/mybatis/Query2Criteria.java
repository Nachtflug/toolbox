package ntf.mybatis;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import ntf.annotation.Criteria;
import ntf.core.GenericTypeIdentified;
import ntf.function.BeanFunctions;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.converter.Converter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 将Query对象转化为Criteria查询对象
 * Criteria应遵循mybatis自动生成规则,一般由example.createCriteria生成
 * 将 Query中@{@link ntf.annotation.Criteria Criteria}注解的属性转化成criteria中对应的条件
 * 如果属性没有标明该注解，则默认为相等关系
 */

@Log4j2
public class Query2Criteria<Q, C> implements Converter<Q, C>, GenericTypeIdentified<Q> {

    private Class<Q> queryEntityClass;

    private Class<C> criteriaEntityClass;

    private Map<Object, Field> queryFieldsByName;
    private Map<Object, Method> criteriaMethodsByName;

    private static List<Class> criteriaAnnos = Lists.newArrayList(Criteria.class.getDeclaredClasses());

    public Query2Criteria(Class<Q> qc, Class<C> cc) {
        queryEntityClass = qc;
        criteriaEntityClass = cc;
        init();
    }


    public Query2Criteria(Class<C> cc) {
        queryEntityClass = getGenericTypeClass();
        criteriaEntityClass = cc;
        init();
    }

    private void init() {
        queryFieldsByName = BeanFunctions.identifyByKey(queryEntityClass.getDeclaredFields(), "name");
        criteriaMethodsByName = BeanFunctions.identifyByKey(criteriaEntityClass.getDeclaredMethods(), "name");
    }
    public void convert(Q q, C criteria) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(queryEntityClass);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                String propertyName = pd.getName();
                if ("class".equals(propertyName))  //跳过类自带的属性
                    continue;
                Method getter = pd.getReadMethod();
                Object obj = getter.invoke(q);
                if (obj == null) // TODO isNull 和  isNotNull 逻辑
                    continue;

                Field f = queryFieldsByName.get(propertyName);
                if (f == null) //declaredFields 不包括继承的属性
                    continue;
                Annotation[] annos = f.getAnnotations();
                AnnotatedElement ae = AnnotatedElementUtils.forAnnotations(annos);
                Criteria criteriaAnno = AnnotatedElementUtils.findMergedAnnotation(ae, Criteria.class);
                Class<?> annoClass = null;
                if (criteriaAnno == null) {
                    annoClass = Criteria.EqualTo.class; // 默认为相等
                } else {
                    for (Annotation anno : annos) {
                        if (criteriaAnnos.contains(anno.annotationType())) {
                            annoClass = anno.annotationType();
                            break;
                        }
                    }
                }
                if (annoClass == Criteria.Except.class) // 排除
                    continue;
                if (annoClass == null) //未找到（理论上经过上面代码不会进入该分支条件，这里是为了预防spring升级产生的可能的变动）
                    continue;

                // 方法名
                String ctMethodName = "and";
                // 如果用户在注解里定义了value就用提供的value，否则默认用字段名
                if (criteriaAnno != null && StringUtils.isNotBlank(criteriaAnno.value())) {
                    ctMethodName += StringUtils.capitalize(criteriaAnno.value());
                } else {
                    ctMethodName += StringUtils.capitalize(propertyName);
                }
                // 补上方法后缀
                if (!ctMethodName.endsWith(annoClass.getSimpleName()))
                    ctMethodName += annoClass.getSimpleName();

                if (annoClass.equals(Criteria.Like.class))
                    obj = "%" + obj + "%";
                Method method = criteriaMethodsByName.get(ctMethodName);
                if (method != null)
                    method.invoke(criteria, obj);
                else if (log.isDebugEnabled())
                    log.warn("未找到query 2 criteria的方法：" + ctMethodName);
            }

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException ignore) {

        }
    }

    @Override
    public C convert(Q q) {
        try {
            C cri = criteriaEntityClass.newInstance();
            convert(q, cri);
            return cri;
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e);
        }
        return null;
    }
}
