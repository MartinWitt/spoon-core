/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.ELSE;
import static spoon.reflect.path.CtRole.THEN;

public class CtConditionalImpl extends CtExpressionImpl implements CtConditional {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = ELSE)
	CtExpression elseExpression;

	@MetamodelPropertyField(role = CONDITION)
	CtExpression condition;

	@MetamodelPropertyField(role = THEN)
	CtExpression thenExpression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtConditional(this);
	}

	@Override
	public CtExpression getElseExpression() {
		return elseExpression;
	}

	@Override
	public CtExpression getCondition() {
		return condition;
	}

	@Override
	public CtExpression getThenExpression() {
		return thenExpression;
	}

	@Override
	public <C extends CtConditional> C setElseExpression(CtExpression elseExpression) {
		if (elseExpression != null) {
			elseExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, ELSE, elseExpression, this.elseExpression);
		this.elseExpression = elseExpression;
		return ((C) (this));
	}

	@Override
	public <C extends CtConditional> C setCondition(CtExpression condition) {
		if (condition != null) {
			condition.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CONDITION, condition, this.condition);
		this.condition = condition;
		return ((C) (this));
	}

	@Override
	public <C extends CtConditional> C setThenExpression(CtExpression thenExpression) {
		if (thenExpression != null) {
			thenExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, THEN, thenExpression, this.thenExpression);
		this.thenExpression = thenExpression;
		return ((C) (this));
	}

	@Override
	public CtConditional clone() {
		return ((CtConditional) (super.clone()));
	}
}
