/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTextBlock;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;

import java.lang.annotation.Annotation;

/**
 * This interface defines the visitor for the Spoon metamodel, as defined in
 * {@link spoon.reflect.declaration}, {@link spoon.reflect.code}, and
 * {@link spoon.reflect.reference}. It declares a visit method for each
 * element of the AST.
 */
public interface CtVisitor {
	/**
	 * Visits an annotation.
	 */
	<A extends Annotation> void visitCtAnnotation(CtAnnotation annotation);

	/**
	 * Visits a code snippet expression.
	 */
	<T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression expression);

	/**
	 * Visits a code snippet statement.
	 */
	void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement);

	/**
	 * Visits an annotation type declaration.
	 */
	<A extends Annotation> void visitCtAnnotationType(CtAnnotationType annotationType);

	/**
	 * Visits an anonymous executable.
	 */
	void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec);

	/**
	 * Visits an array read access.
	 */
	<T> void visitCtArrayRead(CtArrayRead arrayRead);

	/**
	 * Visits an array write access.
	 */
	<T> void visitCtArrayWrite(CtArrayWrite arrayWrite);

	/**
	 * Visits a reference to an array type.
	 */
	<T> void visitCtArrayTypeReference(CtArrayTypeReference reference);

	/**
	 * Visits an assert.
	 */
	<T> void visitCtAssert(CtAssert asserted);

	/**
	 * Visits an assignment.
	 */
	<T, A extends T> void visitCtAssignment(CtAssignment assignement);

	/**
	 * Visits a binary operator.
	 */
	<T> void visitCtBinaryOperator(CtBinaryOperator operator);

	/**
	 * Visits a block of code.
	 */
	<R> void visitCtBlock(CtBlock block);

	/**
	 * Visits a <code>break</code> statement.
	 */
	void visitCtBreak(CtBreak breakStatement);

	/**
	 * Visits a <code>case</code> clause.
	 */
	<S> void visitCtCase(CtCase caseStatement);

	/**
	 * Visits a <code>catch</code> clause.
	 */
	void visitCtCatch(CtCatch catchBlock);

	/**
	 * Visits a class declaration.
	 */
	<T> void visitCtClass(CtClass ctClass);

	/**
	 * Visits a type parameter declaration.
	 */
	void visitCtTypeParameter(CtTypeParameter typeParameter);

	/**
	 * Visits a conditional expression
	 */
	<T> void visitCtConditional(CtConditional conditional);

	/**
	 * Visits a constructor declaration.
	 */
	<T> void visitCtConstructor(CtConstructor c);

	/**
	 * Visits a <code>continue</code> statement.
	 */
	void visitCtContinue(CtContinue continueStatement);

	/**
	 * Visits a <code>do</code> loop.
	 */
	void visitCtDo(CtDo doLoop);

	/**
	 * Visits an enumeration declaration.
	 */
	<T extends Enum<?>> void visitCtEnum(CtEnum ctEnum);

	/**
	 * Visits a reference to an executable.
	 */
	<T> void visitCtExecutableReference(CtExecutableReference reference);

	/**
	 * Visits a field declaration.
	 */
	<T> void visitCtField(CtField f);

	/**
	 * Visits an enum value declaration.
	 */
	<T> void visitCtEnumValue(CtEnumValue enumValue);

	/**
	 * Visits a this access.
	 */
	<T> void visitCtThisAccess(CtThisAccess thisAccess);

	/**
	 * Visits a reference to a field.
	 */
	<T> void visitCtFieldReference(CtFieldReference reference);

	/**
	 * Visits a reference to an unbound field
	 */
	<T> void visitCtUnboundVariableReference(CtUnboundVariableReference reference);

	/**
	 * Visits a <code>for</code> loop.
	 */
	void visitCtFor(CtFor forLoop);

	/**
	 * Visits an enhanced <code>for</code> loop.
	 */
	void visitCtForEach(CtForEach foreach);

	/**
	 * Visits an <code>if</code> statement.
	 */
	void visitCtIf(CtIf ifElement);

	/**
	 * Visits an interface declaration.
	 */
	<T> void visitCtInterface(CtInterface intrface);

	/**
	 * Visits an executable invocation.
	 */
	<T> void visitCtInvocation(CtInvocation invocation);

	/**
	 * Visits a literal expression.
	 */
	<T> void visitCtLiteral(CtLiteral<T> literal);

	/**
	 * Visits a literal TextBlock string.
	 */
	void visitCtTextBlock(CtTextBlock ctTextBlock);

	/**
	 * Visits a local variable declaration.
	 */
	<T> void visitCtLocalVariable(CtLocalVariable localVariable);

	/**
	 * Visits a reference to a local variable.
	 */
	<T> void visitCtLocalVariableReference(CtLocalVariableReference reference);

	/**
	 * Visits a catch variable declaration.
	 */
	<T> void visitCtCatchVariable(CtCatchVariable catchVariable);

	/**
	 * Visits a reference to a catch variable.
	 */
	<T> void visitCtCatchVariableReference(CtCatchVariableReference reference);

	/**
	 * Visits a method declaration.
	 */
	<T> void visitCtMethod(CtMethod m);

	/**
	 * Visits an annotation method declaration.
	 */
	<T> void visitCtAnnotationMethod(CtAnnotationMethod annotationMethod);

	/**
	 * Visits an array construction.
	 */
	<T> void visitCtNewArray(CtNewArray newArray);

	/**
	 * Visits a call to a constructor.
	 */
	<T> void visitCtConstructorCall(CtConstructorCall ctConstructorCall);

	/**
	 * Visits an anonymous class construction.
	 */
	<T> void visitCtNewClass(CtNewClass newClass);

	/**
	 * Visits an anonymous method construction.
	 */
	<T> void visitCtLambda(CtLambda lambda);

	/**
	 * Visits a reference to an executable.
	 */
	<T, E extends CtExpression> void visitCtExecutableReferenceExpression(
			CtExecutableReferenceExpression expression);

	/**
	 * Visits an operator assignment.
	 */
	<T, A extends T> void visitCtOperatorAssignment(
			CtOperatorAssignment assignment);

	/**
	 * Visits a package declaration.
	 */
	void visitCtPackage(CtPackage ctPackage);

	/**
	 * Visits a reference to a package.
	 */
	void visitCtPackageReference(CtPackageReference reference);

	/**
	 * Visits a parameter declaration.
	 */
	<T> void visitCtParameter(CtParameter parameter);

	/**
	 * Visits a reference to a parameter.
	 */
	<T> void visitCtParameterReference(CtParameterReference reference);

	/**
	 * Visits a <code>return</code> statement.
	 */
	<R> void visitCtReturn(CtReturn returnStatement);

	/**
	 * Visits a statement list.
	 */
	<R> void visitCtStatementList(CtStatementList statements);

	/**
	 * Visits a <code>switch</code> statement.
	 */
	<S> void visitCtSwitch(CtSwitch switchStatement);

	/**
	 * Visits a <code>switch</code> expression.
	 */
	<T, S> void visitCtSwitchExpression(CtSwitchExpression switchExpression);

	/**
	 * Visits a <code>synchronized</code> modifier.
	 */
	void visitCtSynchronized(CtSynchronized synchro);

	/**
	 * Visits a <code>throw</code> statement.
	 */
	void visitCtThrow(CtThrow throwStatement);

	/**
	 * Visits a <code>try</code> statement.
	 */
	void visitCtTry(CtTry tryBlock);

	/**
	 * Visits a <code>try</code> with resource statement.
	 */
	void visitCtTryWithResource(CtTryWithResource tryWithResource);

	/**
	 * Visits a reference to a type parameter.
	 */
	void visitCtTypeParameterReference(CtTypeParameterReference ref);

	/**
	 * Visits a reference to a wildcard.
	 */
	void visitCtWildcardReference(CtWildcardReference wildcardReference);

	/**
	 * Visits an intersection type.
	 */
	<T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference reference);

	/**
	 * Visits a reference to a type.
	 */
	<T> void visitCtTypeReference(CtTypeReference reference);

	/**
	 * Visits a type access.
	 */
	<T> void visitCtTypeAccess(CtTypeAccess typeAccess);

	/**
	 * Visits a unary operator.
	 */
	<T> void visitCtUnaryOperator(CtUnaryOperator operator);

	/**
	 * Visits a variable read access.
	 */
	<T> void visitCtVariableRead(CtVariableRead variableRead);

	/**
	 * Visits a variable write access.
	 */
	<T> void visitCtVariableWrite(CtVariableWrite variableWrite);

	/**
	 * Visits a <code>while</code> loop.
	 */
	void visitCtWhile(CtWhile whileLoop);

	/**
	 * Visits a field of an annotation.
	 */
	<T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess annotationFieldAccess);

	/**
	 * Visits a field read access.
	 */
	<T> void visitCtFieldRead(CtFieldRead fieldRead);

	/**
	 * Visits a field write access.
	 */
	<T> void visitCtFieldWrite(CtFieldWrite fieldWrite);

	/**
	 * Visits an access to a super invocation.
	 */
	<T> void visitCtSuperAccess(CtSuperAccess f);

	/**
	 * Visits a comment
	 */
	void visitCtComment(CtComment comment);

	/**
	 * Visits a javadoc comment
	 */
	void visitCtJavaDoc(CtJavaDoc comment);

	/**
	 * Visits a javadoc tag
	 */
	void visitCtJavaDocTag(CtJavaDocTag docTag);

	/**
	 * Visits an import declaration
	 */
	void visitCtImport(CtImport ctImport);

	/**
	 * Visits a module declaration
	 */
	void visitCtModule(CtModule module);

	/**
	 * Visits a module reference
	 */
	void visitCtModuleReference(CtModuleReference moduleReference);

	/**
	 * Visits a package export in a module declaration.
	 */
	void visitCtPackageExport(CtPackageExport moduleExport);

	/**
	 * Visits a "require" directive in a module declaration.
	 */
	void visitCtModuleRequirement(CtModuleRequirement moduleRequirement);

	/**
	 * Visits a "provides" directive in a module declaration.
	 */
	void visitCtProvidedService(CtProvidedService moduleProvidedService);

	/**
	 * Visits a "uses" directive in a module declaration.
	 */
	void visitCtUsedService(CtUsedService usedService);

	/**
	 * Visits a compilation unit
	 */
	void visitCtCompilationUnit(CtCompilationUnit compilationUnit);

	/**
	 * Visits a package declaration
	 */
	void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration);

	/**
	 * Visits an import wildcard static type member reference
	 */
	void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference);

	/**
	 * Visits a yield statement.
	 */
	void visitCtYieldStatement(CtYieldStatement statement);

	/**
	 * Visits a pattern.
	 *
	 * @param pattern the pattern to visit.
	 */
	void visitCtTypePattern(CtTypePattern pattern);
	/**
	 * Visits a record.
	 * @param recordType the record to visit.
	 */
	void visitCtRecord(CtRecord recordType);
	/**
	 * Visits a record component.
	 * @param recordComponent  the record component to visit.
	 */

	void visitCtRecordComponent(CtRecordComponent recordComponent);
}
