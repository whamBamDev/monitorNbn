package me.ineson.monitorNbn.thymeleaf;

//import me.ineson.monitorNbn.thymeleaf.UtilitiesExpression
import spock.lang.*

class UtilitiesExpressionFactorySpecification extends Specification {

	def dialect = new UtilitiesDialect();
	def factory = dialect.getExpressionObjectFactory();

    @Unroll
    def "isCacheable tests"(String expressionObjectName, boolean expectedResult) {
        when:
		    def isCacheableResult = factory.isCacheable(expressionObjectName)
	
        then:
			isCacheableResult == expectedResult

        where:
			expressionObjectName                                          | expectedResult
			null                                                          | false
			""                                                            | false
			"23432432"                                                    | false
			UtilitiesExpressionFactory.UTILITIES_EVALUATION_VARIABLE_NAME | true
			
	}

	@Unroll
	def "buildObject negative tests shhould always return null"(String expressionObjectName, def expectedResult) {
		when:
		    def returnedObject = factory.buildObject(null, expressionObjectName)
		
		then:
			returnedObject == expectedResult
	
		where:
			expressionObjectName                                          | expectedResult
			null                                                          | null
			""                                                            | null
			"23432432"                                                    | null
				
	}
	
	@Unroll
	def "buildObject success test returnd the correct class"(String expressionObjectName, Class<? extends Object>  expectedResult) {
		when:
		    def returnedObject = factory.buildObject(null, expressionObjectName)
		
		then:
			expectedResult.isCase(returnedObject)
	
		where:
			expressionObjectName                                          | expectedResult
			UtilitiesExpressionFactory.UTILITIES_EVALUATION_VARIABLE_NAME | UtilitiesExpression
				
	}

	
	@Unroll
	def "buildObject success test returnd the correct class"() {
		when:
			Set<String> nameList = factory.getAllExpressionObjectNames();
		
		then:
		    nameList != null
			nameList.size() == 1
			nameList.getAt(0) == UtilitiesExpressionFactory.UTILITIES_EVALUATION_VARIABLE_NAME
	}
}
