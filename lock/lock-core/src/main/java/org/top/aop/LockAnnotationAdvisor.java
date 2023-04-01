package org.top.aop;

import lombok.NonNull;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.top.annotation.Lock4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LockAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private Advice advice;

    private Pointcut pointcut = new AnnotationMethodPoint(Lock4j.class);


    public LockAnnotationAdvisor(@NonNull LockInterceptor lockInterceptor, int order){
        this.advice = lockInterceptor;
        setOrder(order);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if(this.advice instanceof BeanFactoryAware){
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }


    public static class AnnotationMethodPoint implements Pointcut{

        private final Class<? extends Annotation> annotationType;

        public AnnotationMethodPoint(Class<? extends Annotation> annotationType){
            Assert.notNull(annotationType, "Annotation type must not be null");
            this.annotationType = annotationType;
        }

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new LockAnnotationAdvisor.AnnotationMethodMatcher(this.annotationType);
        }
    }


    private static class AnnotationMethodMatcher extends StaticMethodMatcher {

        private final Class<? extends Annotation> annotationType;

        public AnnotationMethodMatcher(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            if(matchesMethod(method)){
                return true;
            }
            if(Proxy.isProxyClass(targetClass)){
                return false;
            }
            Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            return (specificMethod != method && matchesMethod(specificMethod));
        }


        private boolean matchesMethod(Method method) {
            return AnnotatedElementUtils.hasAnnotation(method, this.annotationType);
        }
    }


}
