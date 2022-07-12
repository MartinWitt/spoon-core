/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.EXECUTABLE_REF;

public class CtExecutableReferenceExpressionImpl extends CtTargetedExpressionImpl implements CtExecutableReferenceExpression {
	@MetamodelPropertyField(role = EXECUTABLE_REF)
	CtExecutableReference executable;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtExecutableReferenceExpression(this);
	}

	@Override
	public CtExecutableReference getExecutable() {
		return executable;
	}

	@Override
	public <C extends CtExecutableReferenceExpression> C setExecutable(CtExecutableReference executable) {
		if (executable != null) {
			executable.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXECUTABLE_REF, executable, this.executable);
		this.executable = executable;
		return ((C) (this));
	}

	@Override
	public CtExecutableReferenceExpression clone() {
		return ((CtExecutableReferenceExpression) (super.clone()));
	}
}
