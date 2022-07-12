/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtReturnImpl extends CtStatementImpl implements CtReturn {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression returnedExpression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtReturn(this);
	}

	@Override
	public CtExpression getReturnedExpression() {
		return returnedExpression;
	}

	@Override
	public <T extends CtReturn> T setReturnedExpression(CtExpression expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.returnedExpression);
		this.returnedExpression = expression;
		return ((T) (this));
	}

	@Override
	public CtReturn clone() {
		return ((CtReturn) (super.clone()));
	}

	@Override
	public java.lang.Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType targetType) {
		return clone();
	}
}
