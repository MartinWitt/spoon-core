/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.reference.CtFieldReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

/**
 * This element defines a field declaration.
 */
public interface CtField extends CtVariable, CtTypeMember, CtRHSReceiver, CtShadowable {

	/**
	 * The separator for a string representation of a field.
	 */
	String FIELD_SEPARATOR = "#";

	/* (non-Javadoc)

	@see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	@Override
	@DerivedProperty
	CtFieldReference getReference();

	/**
	 * Useful proxy to {@link #getDefaultExpression()}.
	 */
	@Override
	@DerivedProperty
	CtExpression getAssignment();

	@Override
	@UnsettableProperty
	<U extends CtRHSReceiver> U setAssignment(CtExpression assignment);

	@Override
	CtField clone();
}
