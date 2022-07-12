/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.LovecraftException;
import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.QualifiedNameBasedSortedSet;
import spoon.support.visitor.SignaturePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.EXPRESSION;
import static spoon.reflect.path.CtRole.NAME;
import static spoon.reflect.path.CtRole.PARAMETER;
import static spoon.reflect.path.CtRole.THROWN;

public class CtLambdaImpl extends CtExpressionImpl implements CtLambda {
	@MetamodelPropertyField(role = NAME)
	String simpleName = "";
	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression expression;
	@MetamodelPropertyField(role = BODY)
	CtBlock body;
	@MetamodelPropertyField(role = PARAMETER)
	List<CtParameter> parameters = emptyList();
	@MetamodelPropertyField(role = THROWN)
	Set<CtTypeReference> thrownTypes = emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLambda(this);
	}

	@Override
	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public <C extends CtNamedElement> C setSimpleName(String simpleName) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, NAME, simpleName, this.simpleName);
		this.simpleName = simpleName;
		return ((C) (this));
	}

	@Override
	@SuppressWarnings("unchecked")
	public CtBlock getBody() {
		return ((CtBlock) (body));
	}

	@Override
	public <C extends CtBodyHolder> C setBody(CtStatement statement) {
		if (statement != null) {
			CtBlock body = getFactory().Code().getOrCreateCtBlock(statement);
			getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, BODY, body, this.body);
			if (expression != null && body != null) {
				throw new SpoonException("A lambda can't have two bodys.");
			}
			if (body != null) {
				body.setParent(this);
			}
			this.body = body;
		} else {
			getFactory().getEnvironment().getModelChangeListener().onObjectDelete(this, BODY, this.body);
			this.body = null;
		}
		return ((C) (this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> CtMethod getOverriddenMethod() {
		// The type of this lambda expression. For example: `Consumer<Integer>`
		CtTypeReference lambdaTypeRef = getType();
		if (lambdaTypeRef == null) {
			// it can be null in noclasspath mode, so we do not know which method is called, by lambda
			return null;
		}
		if (!(lambdaTypeRef instanceof CtIntersectionTypeReference)) {
			return getOverriddenMethodForNormalType(lambdaTypeRef);
		}
		CtMethod lambdaExecutableMethod = getOverriddenMethodForIntersectionType(lambdaTypeRef);
		if (lambdaExecutableMethod == null) {
			throw new SpoonException("The lambda can be based on interface, which has one method. But " + lambdaTypeRef.getQualifiedName() + " has no one");
		}
		return ((CtMethod) (lambdaExecutableMethod));
	}

	private CtMethod getOverriddenMethodForIntersectionType(CtTypeReference lambdaTypeRef)
			throws SpoonException {
		CtMethod lambdaExecutableMethod = null;
		CtElement parent = lambdaTypeRef.getParent();
		CtTypeReference parentTypeReference = null;
		if (parent != null && parent instanceof CtLocalVariable) {
			parentTypeReference = ((CtLocalVariable) parent).getType();
		} else if (parent != null && parent instanceof CtAssignment) {
			parentTypeReference = ((CtAssignment) parent).getAssigned().getType();
		}
		for (CtTypeReference ctTypeReference : ((CtIntersectionTypeReference) (lambdaTypeRef)).getBounds()) {
			CtMethod tmp = getOverriddenMethodForNormalType(ctTypeReference);
			if (tmp != null && (lambdaExecutableMethod == null || ctTypeReference.equals(parentTypeReference))) {
				lambdaExecutableMethod = tmp;
			}
		}
		return lambdaExecutableMethod;
	}

	private <R> CtMethod getOverriddenMethodForNormalType(CtTypeReference lambdaTypeRef) throws SpoonException {
		CtType lambdaType = lambdaTypeRef.getTypeDeclaration();
		if (lambdaType.isInterface() == false) {
			throw new SpoonException("The lambda can be based on interface only. But type " + lambdaTypeRef.getQualifiedName() + " is not an interface");
		}
		Set<CtMethod> lambdaTypeMethods = lambdaType.getAllMethods();
		CtMethod lambdaExecutableMethod = null;
		if (lambdaTypeMethods.size() == 1) {
			//even the default method can be used, if it is the only one
			lambdaExecutableMethod = lambdaTypeMethods.iterator().next();
		} else {
			for (CtMethod method : lambdaTypeMethods) {
				if (getFactory().Method().OBJECT_METHODS.stream().anyMatch(method::isOverriding)) {
					continue;
				}
				if (method.isDefaultMethod() || method.hasModifier(ModifierKind.PRIVATE) || method.hasModifier(ModifierKind.STATIC)) {
					continue;
				}
				if (lambdaExecutableMethod != null) {
					throw new SpoonException("The lambda can be based on interface, which has only one method. But " + lambdaTypeRef.getQualifiedName() + " has at least two: " + lambdaExecutableMethod.getSignature() + " and " + method.getSignature());
				}
				lambdaExecutableMethod = method;
			}
		}
		return ((CtMethod) ( lambdaExecutableMethod));
	}

	@Override
	public List<CtParameter> getParameters() {
		return unmodifiableList(parameters);
	}

	@Override
	public <C extends CtExecutable> C setParameters(List<CtParameter> params) {
		if (params == null || params.isEmpty()) {
			this.parameters = CtElementImpl.emptyList();
			return ((C) (this));
		}
		if (this.parameters == CtElementImpl.<CtParameter>emptyList()) {
			this.parameters = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, PARAMETER, this.parameters, new ArrayList<>(this.parameters));
		this.parameters.clear();
		for (CtParameter p : params) {
			addParameter(p);
		}
		return ((C) (this));
	}

	@Override
	public <C extends CtExecutable> C addParameter(CtParameter parameter) {
		addParameterAt(parameters.size(), parameter);
		return ((C) (this));
	}

	@Override
	public <C extends CtExecutable> C addParameterAt(int position, CtParameter parameter) {
		if (parameter == null) {
			return ((C) (this));
		}
		if (parameters == CtElementImpl.<CtParameter>emptyList()) {
			parameters = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		parameter.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, PARAMETER, this.parameters, parameter);
		parameters.add(position, parameter);
		return ((C) (this));
	}

	@Override
	public boolean removeParameter(CtParameter parameter) {
		if (parameters == CtElementImpl.<CtParameter>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, PARAMETER, parameters, parameters.indexOf(parameter), parameter);
		return parameters.remove(parameter);
	}

	@Override
	public Set<CtTypeReference> getThrownTypes() {
		return thrownTypes;
	}

	@Override
	@UnsettableProperty
	public <C extends CtExecutable> C setThrownTypes(Set<CtTypeReference> thrownTypes) {
		return ((C) (this));
	}

	@Override
	public <C extends CtExecutable> C addThrownType(CtTypeReference throwType) {
		if (throwType == null) {
			return ((C) (this));
		}
		if (thrownTypes == CtElementImpl.<CtTypeReference>emptySet()) {
			thrownTypes = new QualifiedNameBasedSortedSet<>();
		}
		throwType.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(this, THROWN, this.thrownTypes, throwType);
		thrownTypes.add(throwType);
		return ((C) (this));
	}

	@Override
	public boolean removeThrownType(CtTypeReference throwType) {
		if (thrownTypes == CtElementImpl.<CtTypeReference>emptySet()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(this, THROWN, thrownTypes, throwType);
		return thrownTypes.remove(throwType);
	}

	@Override
	public String getSignature() {
		final SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature();
	}

	@Override
	public CtExecutableReference getReference() {
		return getFactory().Executable().createReference(this);
	}

	@Override
	public CtExpression getExpression() {
		return expression;
	}

	@Override
	public <C extends CtLambda> C setExpression(CtExpression expression) {
		if (body != null && expression != null) {
			throw new LovecraftException("A lambda can't have two bodies.");
		} else {
			if (expression != null) {
				expression.setParent(this);
			}
			getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
			this.expression = expression;
		}
		return ((C) (this));
	}

	@Override
	public CtLambda clone() {
		return ((CtLambda) (super.clone()));
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference type) {
		if (type != null) {
			type.setImplicit(true);
		}
		return super.setType(type);
	}
}
