/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import static spoon.reflect.path.CtRole.DEFAULT_EXPRESSION;

import java.util.List;
import java.util.Set;

/**
 * The implementation for {@link spoon.reflect.declaration.CtAnnotationMethod}.
 */
public class CtAnnotationMethodImpl extends CtMethodImpl implements CtAnnotationMethod {
	@MetamodelPropertyField(role = DEFAULT_EXPRESSION)
	CtExpression defaultExpression;

	@Override
	public void accept(CtVisitor v) {
		v.visitCtAnnotationMethod(this);
	}

	@Override
	public CtExpression getDefaultExpression() {
		return defaultExpression;
	}

	@Override
	public <C extends CtAnnotationMethod> C setDefaultExpression(CtExpression assignedExpression) {
		if (assignedExpression != null) {
			assignedExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, DEFAULT_EXPRESSION, assignedExpression, this.defaultExpression);
		this.defaultExpression = assignedExpression;
		return ((C) (this));
	}

	@Override
	@DerivedProperty
	public CtBlock getBody() {
		return null;
	}

	@Override
	@UnsettableProperty
	public <T extends CtBodyHolder> T setBody(CtStatement statement) {
		return ((T) (this));
	}

	@Override
	@DerivedProperty
	public Set<CtTypeReference> getThrownTypes() {
		return emptySet();
	}

	@Override
	@UnsettableProperty
	public <U extends CtExecutable> U setThrownTypes(Set<CtTypeReference> thrownTypes) {
		return ((U) (this));
	}

	@Override
	@DerivedProperty
	public List<CtTypeParameter> getFormalCtTypeParameters() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <C extends CtFormalTypeDeclarer> C setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters) {
		return ((C) (this));
	}

	@Override
	@DerivedProperty
	public List<CtParameter> getParameters() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <U extends CtExecutable> U setParameters(List<CtParameter> parameters) {
		return ((U) (this));
	}

	@Override
	public CtAnnotationMethod clone() {
		return ((CtAnnotationMethod) (super.clone()));
	}
}


