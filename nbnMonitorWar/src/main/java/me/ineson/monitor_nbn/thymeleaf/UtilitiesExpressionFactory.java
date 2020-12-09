package me.ineson.monitor_nbn.thymeleaf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class UtilitiesExpressionFactory implements IExpressionObjectFactory {
    
    static final String UTILITIES_EVALUATION_VARIABLE_NAME = "utilities";
    
    private static final Set<String> ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList(UTILITIES_EVALUATION_VARIABLE_NAME)));

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (UTILITIES_EVALUATION_VARIABLE_NAME.equals(expressionObjectName)) {
            return new UtilitiesExpression();
        }
        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return UTILITIES_EVALUATION_VARIABLE_NAME.equals(expressionObjectName);
    }
    
}