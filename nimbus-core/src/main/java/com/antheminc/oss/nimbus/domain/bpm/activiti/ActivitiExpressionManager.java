/**
 *  Copyright 2016-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.antheminc.oss.nimbus.domain.bpm.activiti;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.el.JsonNodeELResolver;
import org.activiti.engine.impl.el.ReadOnlyMapELResolver;
import org.activiti.engine.impl.el.VariableScopeElResolver;
import org.activiti.spring.ApplicationContextElResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Jayant Chaudhuri
 *
 */
public class ActivitiExpressionManager extends ExpressionManager implements ApplicationContextAware{
	
	protected ApplicationContext applicationContext;
	
	@Override
	public Expression createExpression(String expression) {
		expression = evaluate(expression);
		return super.createExpression(expression);
	}
	
	/**
	 * 
	 * @param expression
	 * @return
	 */
	public String evaluate(String expression){
		if(!expression.startsWith("${")){
			StringBuilder modifiedExpression = new StringBuilder();
			expression = expression.replaceAll("'", "\\\\'");
			modifiedExpression.append("${expressionEvaluator.getValue('").append(expression).append("', processContext.param)").append("}");
			return modifiedExpression.toString();
		}
		return expression;
	}
	
	@Override
	protected ELResolver createElResolver(VariableScope variableScope) {
	    CompositeELResolver compositeElResolver = new CompositeELResolver();
	    compositeElResolver.add(new VariableScopeElResolver(variableScope));
	
	    if (beans != null) {
	      // Only expose limited set of beans in expressions
	      compositeElResolver.add(new ReadOnlyMapELResolver(beans));
	    } else {
	      // Expose full application-context in expressions
	      compositeElResolver.add(new ApplicationContextElResolver(applicationContext));
	    }
	
	    compositeElResolver.add(new ArrayELResolver());
	    compositeElResolver.add(new ListELResolver());
	    compositeElResolver.add(new MapELResolver());
	    compositeElResolver.add(new JsonNodeELResolver());
	    compositeElResolver.add(new BeanELResolver());
	    return compositeElResolver;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
