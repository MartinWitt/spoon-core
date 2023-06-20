/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.ELSE;
import static spoon.reflect.path.CtRole.THEN;


/**
 * This code element defines conditional expressions using the ? (ternary expressions).
 *
 * Example:
 * <pre>
 *     System.out.println(
 *        1==0 ? "foo" : "bar" // &lt;-- ternary conditional
 *     );
 * </pre>
 */
public interface CtConditional extends CtExpression {

	/**
	 * Gets the "false" expression.
	 */
	@PropertyGetter(role = ELSE)
	CtExpression getElseExpression();

	/**
	 * Gets the "true" expression.
	 */
	@PropertyGetter(role = THEN)
	CtExpression getThenExpression();

	/**
	 * Gets the condition expression.
	 */
	@PropertyGetter(role = CONDITION)
	CtExpression getCondition();

	/**
	 * Sets the "false" expression.
	 */
	@PropertySetter(role = ELSE)
	<C extends CtConditional> C setElseExpression(CtExpression elseExpression);

	/**
	 * Sets the "true" expression.
	 */
	@PropertySetter(role = THEN)
	<C extends CtConditional> C setThenExpression(CtExpression thenExpression);

	/**
	 * Sets the condition expression.
	 */
	@PropertySetter(role = CONDITION)
	<C extends CtConditional> C setCondition(CtExpression condition);

	@Override
	CtConditional clone();
}
