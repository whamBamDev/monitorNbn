package me.ineson.monitorNbn.thymeleaf;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class UtilitiesDialect extends AbstractDialect implements IExpressionObjectDialect {

    private final IExpressionObjectFactory UTILITIES_EXPRESSION_OBJECTS_FACTORY = new UtilitiesExpressionFactory();

    public UtilitiesDialect() {
        super("utilities");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return UTILITIES_EXPRESSION_OBJECTS_FACTORY;
    }

}
