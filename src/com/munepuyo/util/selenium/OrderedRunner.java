package com.munepuyo.util.selenium;

import java.util.List;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 *
 * @author munepuyo
 *
 */
public class OrderedRunner extends BlockJUnit4ClassRunner {

	public OrderedRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> list = super.computeTestMethods();

		if( list != null ) {
			// sort
			list.stream()
			.map( f -> f.getAnnotation(Order.class) )
			.sorted( (a1,a2) -> a1 == null || a2 == null ? -1 : a1.order() - a2.order() )
			;
		}

		return list;
	}
}
