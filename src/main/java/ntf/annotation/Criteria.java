package ntf.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 标注变量在sql中的类型, value = 数据库中的字段，驼峰命名，如果没有就取标注变量的名称
 * 例：
 *  @Criteria.Like("custName")
 *  private String custNameLike;
 * 在解析成mybatis的Example时可以用到
 * 如果取变量名，且变量名后缀等于注解名，则会去除变量名的注解名后缀作为数据库字段
 * 例：
 *  @Criteria.Like
 *  private String custNameLike;
 *  实际取值会去掉 "Like"，即 custNameLike -> custName；
 */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Criteria {


    String value() default "";

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface Like {
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    /**
     * 必须标注List
     */
    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface In {
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";

    }

    /**
     * 必须标注List
     */
    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface NotIn {
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface EqualTo{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface GreatThan{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface LessThan{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface GreaterThanOrEqualTo{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface LessThanOrEqualTo{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface IsNull{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface IsNotNull{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

    /**
     * 不做example的转换
     */
    @Criteria
    @Retention(RetentionPolicy.RUNTIME)
    @interface Except{
        @AliasFor(annotation = Criteria.class, attribute = "value")
        String value() default "";
    }

}
