/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtExpression;

import static spoon.reflect.path.CtRole.EXPRESSION;

public abstract class CtArrayAccessImpl extends CtTargetedExpressionImpl implements CtArrayAccess {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = EXPRESSION)
	private CtExpression expression;

	@Override
	public CtExpression getIndexExpression() {
		return expression;
	}

	@Override
	public <C extends CtArrayAccess> C setIndexExpression(CtExpression expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
		this.expression = expression;
		return ((C) (this));
	}

	@Override
	public CtArrayAccess clone() {
		return ((CtArrayAccess) (super.clone()));
	}
}
