/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;

/**
 * This code element defines an access to super.
 *
 * Example:
 * <pre>
 *     class Foo { int foo() { return 42;}};
 *     class Bar extends Foo {
 *     int foo() {
 *       return super.foo(); // &lt;-- access to super
 *     }
 *     };
 * </pre>
 *
 * The target is used when one writes `SuperClass.super.foo()`.
 *
 * @param <T>
 * 		Type of super
 */
public interface CtSuperAccess extends CtVariableRead, CtTargetedExpression {@Override
	CtSuperAccess clone();

	@Override
	@DerivedProperty
	CtTypeReference getType();
}
