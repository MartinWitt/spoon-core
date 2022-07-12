/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtTargetedExpression;

import static spoon.reflect.path.CtRole.TARGET;

public abstract class CtTargetedExpressionImpl extends CtExpressionImpl implements CtTargetedExpression {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = TARGET)
	CtExpression target;

	@Override
	public CtExpression getTarget() {
		return target;
	}

	@Override
	public <C extends CtTargetedExpression> C setTarget(CtExpression target) {
		if (target != null) {
			target.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TARGET, target, this.target);
		this.target = target;
		return ((C) (this));
	}

	@Override
	public CtTargetedExpression clone() {
		return ((CtTargetedExpression) (super.clone()));
	}
}
