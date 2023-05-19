/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * This element defines an annotation type.
 */
public interface CtAnnotationType extends CtType {

	@DerivedProperty
	Set<CtAnnotationMethod> getAnnotationMethods();

	/**
	 * {@inheritDoc }
	 */
	@Override
	<M, C extends CtType> C addMethod(CtMethod method);

	/**
	 * {@inheritDoc }
	 */
	@Override
	<C extends CtType> C setMethods(Set<CtMethod> methods);

	@Override
	CtAnnotationType clone();

	@Override
	@UnsettableProperty
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	@Override
	@UnsettableProperty
	<C extends CtType> C setSuperInterfaces(Set<CtTypeReference> interfaces);

	@Override
	@UnsettableProperty
	<C extends CtType> C setSuperclass(CtTypeReference superClass);

}
