/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.SpoonException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtTextBlock;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * This sub-factory contains utility methods to create code elements. To avoid
 * over-using reflection, consider using {@link spoon.template.Template}.
 */
public class CodeFactory extends SubFactory {

	/**
	 * Creates a {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 */
	public CodeFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a binary operator.
	 *
	 * @param <T>
	 * 		the type of the expression
	 * @param left
	 * 		the left operand
	 * @param right
	 * 		the right operand
	 * @param kind
	 * 		the operator kind
	 * @return a binary operator expression
	 */
	public <T> CtBinaryOperator createBinaryOperator(CtExpression left, CtExpression right, BinaryOperatorKind kind) {
		return factory.Core().<T>createBinaryOperator().setLeftHandOperand(left).setKind(kind).setRightHandOperand(right);
	}

	/**
	 * Creates a accessed type.
	 *
	 * <p>This method sets a <i>clone</i> of the given {@code accessedType} object to the
	 * {@linkplain CtTypeAccess#getAccessedType() accessedType} field of the returned {@link CtTypeAccess}. If the
	 * given {@code accessedType} is unique and cloning is not needed, use
	 * {@link #createTypeAccessWithoutCloningReference(CtTypeReference)} instead of this method.</p>
	 * @param accessedType a type reference to the accessed type.
	 * @param <T> the type of the expression.
	 * @return a accessed type expression.
	 */
	public <T> CtTypeAccess createTypeAccess(CtTypeReference accessedType) {
		if (accessedType == null) {
			return factory.Core().createTypeAccess();
		}
		CtTypeReference access = accessedType.clone();
		// a type access doesn't contain actual type parameters
		access.setActualTypeArguments(null);
		return createTypeAccessWithoutCloningReference(access);
	}

	/**
	 * Creates a accessed type.
	 *
	 * <p>This method sets a <i>clone</i> of the given {@code accessedType} object to the
	 * {@linkplain CtTypeAccess#getAccessedType() accessedType} field of the returned {@link CtTypeAccess}. If the
	 * given {@code accessedType} is unique and cloning is not needed, use
	 * {@link #createTypeAccessWithoutCloningReference(CtTypeReference)} instead of this method.</p>
	 *
	 * @param accessedType
	 * 		a type reference to the accessed type.
	 * @param isImplicit
	 * 		type of the type access is implicit or not.
	 * @param <T>
	 * 		the type of the expression.
	 * @return a accessed type expression.
	 */
	public <T> CtTypeAccess createTypeAccess(CtTypeReference accessedType, boolean isImplicit) {
		return createTypeAccess(accessedType).setImplicit(isImplicit);
	}

	/**
	 * Creates a accessed type, see {@link #createTypeAccess(CtTypeReference)} for details.
	 * @param accessedType a type reference to the accessed type.
	 * @param <T> the type of the expression.
	 * @return a accessed type expression.
	 */
	public <T> CtTypeAccess createTypeAccessWithoutCloningReference(CtTypeReference accessedType) {
		final CtTypeAccess typeAccess = factory.Core().createTypeAccess();
		typeAccess.setAccessedType(accessedType);
		return typeAccess;
	}

	/**
	 * Creates a class access expression of the form <code>C.class</code>.
	 *
	 * @param <T>
	 * 		the actual type of the accessed class if available
	 * @param type
	 * 		a type reference to the accessed class
	 * @return the class access expression.
	 */
	public <T> CtFieldAccess createClassAccess(CtTypeReference type) {
		@SuppressWarnings({ "rawtypes", "unchecked" }) CtTypeReference classType = ((CtTypeReference) (factory.Type().createReference(java.lang.Class.class)));
		CtTypeAccess typeAccess = factory.Code().createTypeAccess(type);

		CtFieldReference fieldReference = factory.Core().createFieldReference();
		fieldReference.setSimpleName("class");
		fieldReference.setType(classType);
		fieldReference.setDeclaringType(type);

		CtFieldRead fieldRead = factory.Core().createFieldRead();
		fieldRead.setType(classType.clone());
		fieldRead.setVariable(fieldReference);
		fieldRead.setTarget(typeAccess);
		return fieldRead;
	}

	/**
	 * Creates a constructor call. The correct constructor is inferred based on parameters
	 *
	 * @param type the decelerating type of the constructor
	 * @param parameters the arguments of the constructor call
	 * @param <T> the actual type of the decelerating type of the constructor if available
	 * @return the constructor call
	 */
	public <T> CtConstructorCall createConstructorCall(CtTypeReference type, CtExpression...parameters) {
		CtConstructorCall constructorCall = factory.Core()
				.createConstructorCall();
		CtExecutableReference executableReference = factory.Core()
				.createExecutableReference();
		executableReference.setType(type);
		executableReference.setDeclaringType(type == null ? type : type.clone());
		executableReference.setSimpleName(CtExecutableReference.CONSTRUCTOR_NAME);
		List<CtTypeReference> typeReferences = new ArrayList<>();
		for (CtExpression parameter : parameters) {
			typeReferences.add(parameter.getType());
		}
		executableReference.setParameters(typeReferences);
		constructorCall.setArguments(Arrays.asList(parameters));
		constructorCall.setExecutable(executableReference);
		return constructorCall;
	}

	/**
	 * Creates a new anonymous class.
	 */
	public <T> CtNewClass createNewClass(CtType superClass, CtExpression...parameters) {
		CtNewClass ctNewClass = factory.Core().createNewClass();
		CtConstructor constructor = ((CtClass) (superClass)).getConstructor(Arrays.stream(parameters).map(x -> x.getType()).toArray(CtTypeReference[]::new));
		if (constructor == null) {
			throw new SpoonException("no appropriate constructor for these parameters " + Arrays.toString(parameters));
		}
		CtExecutableReference executableReference = constructor.getReference();
		ctNewClass.setArguments(Arrays.asList(parameters));
		ctNewClass.setExecutable(executableReference);
		CtClass c = superClass.getFactory().createClass();
		c.setSuperclass(superClass.getReference());
		c.setSimpleName("0");
		ctNewClass.setAnonymousClass(c);
		return ctNewClass;
	}

	/**
	 * Creates an invocation (can be a statement or an expression).
	 *
	 * @param <T>
	 * 		the return type of the invoked method
	 * @param target
	 * 		the target expression
	 * @param executable
	 * 		the invoked executable
	 * @param arguments
	 * 		the argument list
	 * @return the new invocation
	 */
	public <T> CtInvocation createInvocation(CtExpression target, CtExecutableReference executable, CtExpression... arguments) {
		List<CtExpression> ext = new ArrayList<>(arguments.length);
		Collections.addAll(ext, arguments);
		return createInvocation(target, executable, ext);
	}

	/**
	 * Creates an invocation (can be a statement or an expression).
	 *
	 * @param <T>
	 * 		the return type of the invoked method
	 * @param target
	 * 		the target expression (may be null for static methods)
	 * @param executable
	 * 		the invoked executable
	 * @param arguments
	 * 		the argument list
	 * @return the new invocation
	 */
	public <T> CtInvocation createInvocation(CtExpression target, CtExecutableReference executable, List<CtExpression> arguments) {
		return factory.Core().<T>createInvocation().<CtInvocation>setTarget(target).<CtInvocation>setExecutable(executable).setArguments(arguments);
	}

	/**
	 * Creates a literal with a given value.
	 *
	 * @param <T>
	 * 		the type of the literal
	 * @param value
	 * 		the value of the literal
	 * @return a new literal
	 */
	public <T> CtLiteral<T> createLiteral(T value) {
		CtLiteral<T> literal = factory.Core().<T>createLiteral();
		literal.setValue(value);
		if (value != null) {
			literal.setType(((CtTypeReference) (factory.Type().<T>createReference(((Class<T>) (value.getClass()))).unbox())));
		} else {
			literal.setType(((CtTypeReference) (factory.Type().nullType())));
		}
		return literal;
	}

	/**
	 * Creates a TextBlock with the given string value.
	 * @param value
	 * 		the string value of the literal
	 * @return a new literal
	 */
	public CtTextBlock createTextBlock(String value) {
		CtTextBlock textblock = factory.Core().createTextBlock();
		textblock.setValue(value);
		textblock.setType(((CtTypeReference) ( factory.Type().STRING)));
		return textblock;
	}

	/**
	 * Creates a one-dimension array that must only contain literals.
	 */
	@SuppressWarnings("unchecked")
	public <T> CtNewArray createLiteralArray(T[] value) {
		if (!value.getClass().isArray()) {
			throw new RuntimeException("value is not an array");
		}
		if (value.getClass().getComponentType().isArray()) {
			throw new RuntimeException("can only create one-dimension arrays");
		}
		final CtTypeReference componentTypeRef = factory.Type().createReference((Class<T>) value.getClass().getComponentType());
		final CtArrayTypeReference arrayReference = factory.Type().createArrayReference(componentTypeRef);
		CtNewArray array = factory.Core().<T[]>createNewArray().setType(arrayReference);
		for (T e : value) {
			CtLiteral<T> l = factory.Core().createLiteral();
			l.setValue(e);
			array.addElement(l);
		}
		return array;
	}

	/**
	 * Creates a local variable declaration.
	 *
	 * @param <T>
	 * 		the local variable type
	 * @param type
	 * 		the reference to the type
	 * @param name
	 * 		the name of the variable
	 * @param defaultExpression
	 * 		the assigned default expression
	 * @return a new local variable declaration
	 */
	public <T> CtLocalVariable createLocalVariable(CtTypeReference type, String name, CtExpression defaultExpression) {
		return factory.Core().<T>createLocalVariable().<CtLocalVariable>setSimpleName(name).<CtLocalVariable>setType(type).setDefaultExpression(defaultExpression);
	}

	/**
	 * Creates a local variable reference that points to an existing local
	 * variable (strong referencing).
	 */
	public <T> CtLocalVariableReference createLocalVariableReference(CtLocalVariable localVariable) {
		CtLocalVariableReference ref = factory.Core().createLocalVariableReference();
		ref.setType(localVariable.getType() == null ? null : localVariable.getType().clone());
		ref.setSimpleName(localVariable.getSimpleName());
		ref.setParent(localVariable);
		return ref;
	}

	/**
	 * Creates a local variable reference with its name an type (weak
	 * referencing).
	 */
	public <T> CtLocalVariableReference createLocalVariableReference(CtTypeReference type, String name) {
		return factory.Core().<T>createLocalVariableReference().setType(type).setSimpleName(name);
	}

	/**
	 * Creates a catch variable declaration.
	 *
	 * @param <T>
	 * 		the catch variable type
	 * @param type
	 * 		the reference to the type
	 * @param name
	 * 		the name of the variable
	 * @param modifierKinds
	 * 		Modifiers of the catch variable
	 * @return a new catch variable declaration
	 */
	public <T> CtCatchVariable createCatchVariable(CtTypeReference type, String name, ModifierKind... modifierKinds) {
		EnumSet<ModifierKind> modifiers = EnumSet.noneOf(ModifierKind.class);
		modifiers.addAll(Arrays.asList(modifierKinds));
		return factory.Core().<T>createCatchVariable()
				.<CtCatchVariable>setSimpleName(name)
				.<CtCatchVariable>setType(type)
				.setModifiers(modifiers);
	}

	/**
	 * Creates a catch variable reference that points to an existing catch
	 * variable (strong referencing).
	 */
	public <T> CtCatchVariableReference createCatchVariableReference(CtCatchVariable catchVariable) {
		return factory.Core().<T>createCatchVariableReference().setType(catchVariable.getType()).<CtCatchVariableReference>setSimpleName(catchVariable.getSimpleName());
	}

	/**
	 * Creates a new statement list from an existing block.
	 */
	public <R> CtStatementList createStatementList(CtBlock block) {
		CtStatementList l = factory.Core().createStatementList();
		for (CtStatement s : block.getStatements()) {
			l.addStatement(s.clone());
		}
		return l;
	}

	/**
	 * Creates an explicit access to a <code>this</code> variable (of the form
	 * <code>type.this</code>).
	 *
	 * @param <T>
	 * 		the actual type of <code>this</code>
	 * @param type
	 * 		the reference to the type that holds the <code>this</code>
	 * 		variable
	 * @return a <code>type.this</code> expression
	 */
	public <T> CtThisAccess createThisAccess(CtTypeReference type) {
		return createThisAccess(type, false);
	}

	/**
	 * Creates an access to a <code>this</code> variable (of the form
	 * <code>type.this</code>).
	 *
	 * @param <T>
	 * 		the actual type of <code>this</code>
	 * @param type
	 * 		the reference to the type that holds the <code>this</code>
	 * 		variable
	 * @param isImplicit
	 * 		type of the this access is implicit or not.
	 * @return a <code>type.this</code> expression
	 */
	public <T> CtThisAccess createThisAccess(CtTypeReference type, boolean isImplicit) {
		CtThisAccess thisAccess = factory.Core().<T>createThisAccess();
		thisAccess.setImplicit(isImplicit);
		thisAccess.setType(type);
		CtTypeAccess typeAccess = factory.Code().createTypeAccess(type);
		thisAccess.setTarget(typeAccess);
		return thisAccess;
	}

	/**
	 * Creates a variable access for read.
	 */
	public <T> CtVariableAccess createVariableRead(CtVariableReference variable, boolean isStatic) {
		CtVariableAccess va;
		if (variable instanceof CtFieldReference) {
			va = factory.Core().createFieldRead();
			// creates a this target for non-static fields to avoid name conflicts...
			if (!isStatic) {
				((CtFieldAccess) (va)).setTarget(createThisAccess(((CtFieldReference) (variable)).getDeclaringType()));
			}
		} else {
			va = factory.Core().createVariableRead();
		}
		return va.setVariable(variable);
	}

	/**
	 * Creates a list of variable accesses for read.
	 *
	 * @param variables
	 * 		the variables to be accessed
	 */
	public List<CtExpression> createVariableReads(List<? extends CtVariable> variables) {
		List<CtExpression> result = new ArrayList<>(variables.size());
		for (CtVariable v : variables) {
			result.add(createVariableRead(v.getReference(), v.getModifiers().contains(ModifierKind.STATIC)));
		}
		return result;
	}

	/**
	 * Creates a variable access for write.
	 */
	public <T> CtVariableAccess createVariableWrite(CtVariableReference variable, boolean isStatic) {
		CtVariableAccess va;
		if (variable instanceof CtFieldReference) {
			va = factory.Core().createFieldWrite();
			// creates a this target for non-static fields to avoid name conflicts...
			if (!isStatic) {
				((CtFieldAccess) (va)).setTarget(createThisAccess(((CtFieldReference) (variable)).getDeclaringType()));
			}
		} else {
			va = factory.Core().createVariableWrite();
		}
		return va.setVariable(variable);
	}

	/**
	 * Creates a variable assignment (can be an expression or a statement).
	 *
	 * @param <T>
	 * 		the type of the assigned variable
	 * @param variable
	 * 		a reference to the assigned variable
	 * @param isStatic
	 * 		tells if the assigned variable is static or not
	 * @param expression
	 * 		the assigned expression
	 * @return a variable assignment
	 */
	public <A, T extends A> CtAssignment createVariableAssignment(CtVariableReference variable, boolean isStatic, CtExpression expression) {
		CtVariableAccess vaccess = createVariableWrite(variable, isStatic);
		return factory.Core().<A, T>createAssignment().<CtAssignment>setAssignment(expression).setAssigned(vaccess);
	}

	/**
	 * Creates a list of statements that contains the assignments of a set of
	 * variables.
	 *
	 * @param variables
	 * 		the variables to be assigned
	 * @param expressions
	 * 		the assigned expressions
	 * @return a list of variable assignments
	 */
	public <T> CtStatementList createVariableAssignments(List<? extends CtVariable> variables, List<? extends CtExpression> expressions) {
		CtStatementList result = factory.Core().createStatementList();
		for (int i = 0; i < variables.size(); i++) {
			result.addStatement(createVariableAssignment(variables.get(i).getReference(), variables.get(i).getModifiers().contains(ModifierKind.STATIC), expressions.get(i)));
		}
		return result;
	}

	/**
	 * Creates a field.
	 *
	 * @param name
	 * 		Name of the field.
	 * @param type
	 * 		Type of the field.
	 * @param exp
	 * 		Default expression of the field.
	 * @param visibilities
	 * 		All visibilities of the field.
	 * @param <T>
	 * 		Generic type for the type of the field.
	 * @return a field
	 */
	public <T> CtField createCtField(String name, CtTypeReference type, String exp, ModifierKind... visibilities) {
		return factory.Core().createField().<CtField>setModifiers(modifiers(visibilities)).<CtField>setSimpleName(name).<CtField>setType(type)
				.setDefaultExpression(this.<T>createCodeSnippetExpression(exp));
	}

	/**
	 * Creates a block.
	 *
	 * @param element
	 * 		Statement of the block.
	 * @param <T>
	 * 		Subclasses of CtStatement.
	 * @return a block.
	 */
	public <T extends CtStatement> CtBlock createCtBlock(T element) {
		return factory.Core().createBlock().addStatement(element);
	}

	/**
	 * Accepts instance of CtStatement or CtBlock.
	 * If element is CtStatement, then it creates wrapping CtBlock, which contains the element
	 * If element is CtBlock, then it directly returns that element
	 * If element is null, then it returns null.
	 * note: It must not create empty CtBlock - as expected in CtCatch, CtExecutable, CtLoop and CtTry setBody implementations
	 * @param element
	 * @return CtBlock instance
	 */
	public <T extends CtStatement> CtBlock getOrCreateCtBlock(T element) {
		if (element == null) {
			return null;
		}
		if (element instanceof CtBlock) {
			return ((CtBlock) ( element));
		}
		return this.createCtBlock(element);
	}

	/**
	 * Creates a throw.
	 *
	 * @param thrownExp
	 * 		Expression of the throw.
	 * @return a throw.
	 */
	public CtThrow createCtThrow(String thrownExp) {
		return factory.Core().createThrow().setThrownExpression(this.<Throwable>createCodeSnippetExpression(thrownExp));
	}

	/**
	 * Creates a catch element.
	 *
	 * @param nameCatch
	 * 		Name of the variable in the catch.
	 * @param exception
	 * 		Type of the exception.
	 * @param ctBlock
	 * 		Content of the catch.
	 * @return a catch.
	 */
	public CtCatch createCtCatch(String nameCatch, Class<? extends Throwable> exception, CtBlock ctBlock) {
		final CtCatchVariable catchVariable = factory.Core().<Throwable>createCatchVariable().<CtCatchVariable>setType(this.<Throwable>createCtTypeReference(exception))
				.setSimpleName(nameCatch);
		return factory.Core().createCatch().setParameter(catchVariable).setBody(ctBlock);
	}

	/**
	 * Creates a type reference.
	 *
	 * @param originalClass
	 * 		Original class of the reference.
	 * @param <T>
	 * 		Type of the reference.
	 * @return a type reference.
	 */
	public <T> CtTypeReference createCtTypeReference(Class<?> originalClass) {
		if (originalClass == null) {
			return null;
		}
		CtTypeReference typeReference = factory.Core().<T>createTypeReference();
		typeReference.setSimpleName(originalClass.getSimpleName());
		if (originalClass.isPrimitive()) {
			return typeReference;
		}
		if (originalClass.getDeclaringClass() != null) {
			// the inner class reference does not have package
			return typeReference.setDeclaringType(createCtTypeReference(originalClass.getDeclaringClass()));
		}
		return typeReference.setPackage(createCtPackageReference(originalClass.getPackage()));
	}

	/**
	 * Creates a package reference.
	 *
	 * @param originalPackage
	 * 		Original package of the reference.
	 * @return a package reference.
	 */
	public CtPackageReference createCtPackageReference(Package originalPackage) {
		return factory.Core().createPackageReference().setSimpleName(originalPackage.getName());
	}

	/**
	 * Creates an annotation.
	 *
	 * @param annotationType
	 * 		Type of the annotation.
	 * @return an annotation.
	 */
	public <A extends Annotation> CtAnnotation createAnnotation(CtTypeReference annotationType) {
		final CtAnnotation a = factory.Core().createAnnotation();
		a.setAnnotationType(annotationType);
		return a;
	}

	/**
	 * Gets a list of references from a list of elements.
	 *
	 * @param <R>
	 * 		the expected reference type
	 * @param <E>
	 * 		the element type
	 * @param elements
	 * 		the element list
	 * @return the corresponding list of references
	 */
	@SuppressWarnings("unchecked")
	public <R extends CtReference, E extends CtNamedElement> List<R> getReferences(List<E> elements) {
		List<R> refs = new ArrayList<>(elements.size());
		for (E e : elements) {
			refs.add((R) e.getReference());
		}
		return refs;
	}

	/**
	 * Creates a modifier set.
	 *
	 * @param modifiers
	 * 		to put in set
	 * @return Set of given modifiers
	 */
	public Set<ModifierKind> modifiers(ModifierKind... modifiers) {
		Set<ModifierKind> ret = EnumSet.noneOf(ModifierKind.class);
		Collections.addAll(ret, modifiers);
		return ret;
	}

	/**
	 * Creates a Code Snippet expression.
	 *
	 * @param <T>
	 * 		The type of the expression represented by the CodeSnippet
	 * @param expression
	 * 		The string that contains the expression.
	 * @return a new CtCodeSnippetExpression.
	 */
	public <T> CtCodeSnippetExpression createCodeSnippetExpression(String expression) {
		CtCodeSnippetExpression e = factory.Core().createCodeSnippetExpression();
		e.setValue(expression);
		return e;
	}

	/**
	 * Creates a Code Snippet statement.
	 *
	 * @param statement
	 * 		The String containing the statement.
	 * @return a new CtCodeSnippetStatement
	 */
	public CtCodeSnippetStatement createCodeSnippetStatement(String statement) {
		CtCodeSnippetStatement e = factory.Core().createCodeSnippetStatement();
		e.setValue(statement);
		return e;
	}

	/**
	 * Creates a comment
	 *
	 * @param content The content of the comment
	 * @param type The comment type
	 * @return a new CtComment
	 */
	public CtComment createComment(String content, CtComment.CommentType type) {
		if (type == CtComment.CommentType.JAVADOC) {
			return factory.Core().createJavaDoc().setContent(content);
		}
		return factory.Core().createComment().setContent(content).setCommentType(type);
	}

	/**
	 * Creates an inline comment
	 *
	 * @param content The content of the comment
	 * @return a new CtComment
	 */
	public CtComment createInlineComment(String content) {
		if (content.contains(CtComment.LINE_SEPARATOR)) {
			throw new SpoonException("The content of your comment contain at least one line separator. "
					+ "Please consider using a block comment by calling createComment(\"your content\", CtComment.CommentType.BLOCK).");
		}
		return createComment(content, CtComment.CommentType.INLINE);
	}

	/**
	 * Creates a javadoc tag
	 *
	 * @param content The content of the javadoc tag with a possible paramater
	 * @param type The tag type
	 * @return a new CtJavaDocTag
	 */
	public CtJavaDocTag createJavaDocTag(String content, CtJavaDocTag.TagType type) {
		if (content == null) {
			content = "";
		}
		CtJavaDocTag docTag = factory.Core().createJavaDocTag();
		if (type != null && type.hasParam()) {
			int firstWord = content.indexOf(' ');
			int firstLine = content.indexOf('\n');
			if (firstLine < firstWord && firstLine >= 0) {
				firstWord = firstLine;
			}
			if (firstWord == -1) {
				firstWord = content.length();
			}
			String param = content.substring(0, firstWord);
			content = content.substring(firstWord);
			docTag.setParam(param);
		}
		return docTag.setContent(content.trim()).setType(type);
	}

	/**
	 * Creates a javadoc tag
	 *
	 * @param content The content of the javadoc tag with a possible paramater
	 * @param type The tag type
	 * @param realName The real name of the tag
	 * @return a new CtJavaDocTag
	 */
	public CtJavaDocTag createJavaDocTag(String content, CtJavaDocTag.TagType type, String realName) {
		if (content == null) {
			content = "";
		}
		CtJavaDocTag docTag = factory.Core().createJavaDocTag();
		if (type != null && type.hasParam()) {
			int firstWord = content.indexOf(' ');
			int firstLine = content.indexOf('\n');
			if (firstLine < firstWord && firstLine >= 0) {
				firstWord = firstLine;
			}
			if (firstWord == -1) {
				firstWord = content.length();
			}
			String param = content.substring(0, firstWord);
			content = content.substring(firstWord);
			docTag.setParam(param);
		}
		return docTag.setContent(content.trim()).setType(type).setRealName(realName);
	}
}
