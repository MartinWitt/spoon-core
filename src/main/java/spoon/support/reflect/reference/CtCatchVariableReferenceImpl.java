/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.visitor.CtVisitor;

public class CtCatchVariableReferenceImpl extends CtVariableReferenceImpl implements CtCatchVariableReference {
	private static final long serialVersionUID = 1L;

	public CtCatchVariableReferenceImpl() {
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCatchVariableReference(this);
	}

	@Override
	public CtCatchVariable getDeclaration() {
		CtElement element = this;
		String name = getSimpleName();
		CtCatchVariable var;
		do {
			CtCatch catchBlock = element.getParent(spoon.reflect.code.CtCatch.class);
			if (catchBlock == null) {
				return null;
			}
			var = catchBlock.getParameter();
			element = catchBlock;
		} while (!name.equals(var.getSimpleName()) );
		return var;
	}

	@Override
	public CtCatchVariableReference clone() {
		return ((CtCatchVariableReference) (super.clone()));
	}
}