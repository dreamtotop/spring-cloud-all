package org.top.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {

    /**
     * service version
     * @return
     */
    String version() default "";

    /**
     * service group
     * @return
     */
    String group() default "";
}
