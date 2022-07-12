/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.CASE_KIND;
import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a case within a switch-case.
 *
 * Example: <pre>
 * int x = 0;
 * switch(x) {
 *     case 1: // &lt;-- case statement
 *       System.out.println("foo");
 * }</pre>
 *
 * @param <S>
 * 		This type must be assignable from the switch type
 * @see spoon.reflect.code.CtSwitch
 */
public interface CtCase extends CtStatement, CtStatementList {
	/**
	 * Gets the case expression.
	 * Use {@link #getCaseExpressions()} since Java 12
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression getCaseExpression();

	/**
	 * Sets the case expression. If set with null, the CtCase will represent a default label.
	 * Use {@link #setCaseExpressions(List)} since Java 12
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtCase> T setCaseExpression(CtExpression caseExpression);

	/**
	 * Gets the case expressions.
	 * (Multiple case expressions are available as a preview feature since Java 12)
	 */
	@PropertyGetter(role = EXPRESSION)
	List<CtExpression> getCaseExpressions();

	/**
	 * Sets the case expressions. If set with null or an empty list, the CtCase will represent a default label.
	 * (Multiple case expressions are available as a preview feature since Java 12)
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtCase> T setCaseExpressions(List<CtExpression> caseExpressions);

	/**
	 * Adds case expression.
	 * (Multiple case expressions are available as a preview feature since Java 12)
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtCase> T addCaseExpression(CtExpression caseExpression);

	/**
	 * Gets the kind of this case - colon (:) or arrow (-&gt;)
	 * (Arrow syntax is available as a preview feature since Java 12)
	 */
	@PropertyGetter(role = CASE_KIND)
	CaseKind getCaseKind();

	/**
	 * Sets the kind of this case - colon (:) or arrow (-&gt;)
	 * (Arrow syntax is available as a preview feature since Java 12)
	 */
	@PropertySetter(role = CASE_KIND)
	<T extends CtCase> T setCaseKind(CaseKind kind);

	@Override
	CtCase clone();
}
