package org.top.annotation;

import org.springframework.context.annotation.Import;
import org.top.spring.CustomScannerRegistrar;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Import(CustomScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage();
}
