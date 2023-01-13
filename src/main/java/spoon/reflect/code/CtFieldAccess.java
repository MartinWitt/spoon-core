/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.annotations.PropertyGetter;

import static spoon.reflect.path.CtRole.VARIABLE;

/**
 * This code element defines an access to a field variable (read and write)
 *
 * @param <T>
 * 		Type of this field
 */
public interface CtFieldAccess extends CtVariableAccess, CtTargetedExpression {@Override
	@spoon.reflect.annotations.PropertyGetter(role = VARIABLE)
	CtFieldReference getVariable();

	@Override
	CtFieldAccess clone();
}