package org.top;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认 key 构建器
 */
public class DefaultLockKeyBuilder implements LockKeyBuilder{

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private BeanResolver resolver;

    public DefaultLockKeyBuilder(BeanFactory factory){
        resolver = new BeanFactoryResolver(factory);
    }


    @Override
    public String buildKey(MethodInvocation methodInvocation, String[] definitionKeys) {
        Method method = methodInvocation.getMethod();
        if(definitionKeys.length >= 1 || ! "".equalsIgnoreCase(definitionKeys[0])){
            return getSpelDefinitionKey(definitionKeys, method, methodInvocation.getArguments());
        }
        return "";
    }


    private String getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues){
        StandardEvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, NAME_DISCOVERER);
        context.setBeanResolver(resolver);
        List<String> keys = new ArrayList<>(definitionKeys.length);
        for(String definitionKey : definitionKeys){
            if(definitionKey != null && !definitionKey.isEmpty()){
                String key = PARSER.parseExpression(definitionKey).getValue(context, String.class);
                keys.add(key);
            }
        }
        return StringUtils.collectionToDelimitedString(keys, ".", "", "");
    }

}
