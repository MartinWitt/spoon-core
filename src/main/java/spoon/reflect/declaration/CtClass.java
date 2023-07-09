/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtStatement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;
import java.util.Set;

import static spoon.reflect.path.CtRole.CONSTRUCTOR;
import static spoon.reflect.path.CtRole.ANNONYMOUS_EXECUTABLE;

/**
 * This element represents a class declaration.
 *
 * <pre>
 *     // a class definition
 *     class Foo {
 *        int x;
 *     }
 * </pre>
 * @author Renaud Pawlak
 */
public interface CtClass extends CtType, CtStatement, CtSealable {
	/**
	 * Returns the anonymous blocks of this class.
	 * Derived from {@link #getTypeMembers()}
	 */
	@DerivedProperty
	@PropertyGetter(role = ANNONYMOUS_EXECUTABLE)
	List<CtAnonymousExecutable> getAnonymousExecutables();

	/**
	 * Returns the constructor of the class that takes the given argument types.
	 *
	 * Derived from {@link #getTypeMembers()}
	 */
	@DerivedProperty
	@PropertyGetter(role = CONSTRUCTOR)
	CtConstructor getConstructor(CtTypeReference... parameterTypes);

	/**
	 * Returns the constructors of this class. This includes the default
	 * constructor if this class has no constructors explicitly declared.
	 *
	 * Derived from {@link #getTypeMembers()}
	 */
	@DerivedProperty
	@PropertyGetter(role = CONSTRUCTOR)
	Set<CtConstructor> getConstructors();

	/**
	 * Sets the anonymous blocks of this class.
	 */
	@PropertySetter(role = ANNONYMOUS_EXECUTABLE)
	<C extends CtClass> C setAnonymousExecutables(List<CtAnonymousExecutable> e);

	/**
	 * Add an anonymous block to this class.
	 *
	 * @param e
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = ANNONYMOUS_EXECUTABLE)
	<C extends CtClass> C addAnonymousExecutable(CtAnonymousExecutable e);

	/**
	 * Remove an anonymous block to this class.
	 *
	 * @param e
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = ANNONYMOUS_EXECUTABLE)
	boolean removeAnonymousExecutable(CtAnonymousExecutable e);

	/**
	 * Sets the constructors for this class.
	 */
	@PropertySetter(role = CONSTRUCTOR)
	<C extends CtClass> C setConstructors(Set<CtConstructor> constructors);

	/**
	 * Adds a constructor to this class.
	 */
	@PropertySetter(role = CONSTRUCTOR)
	<C extends CtClass> C addConstructor(CtConstructor constructor);

	/**
	 * Removes a constructor from this class.
	 */
	@PropertySetter(role = CONSTRUCTOR)
	void removeConstructor(CtConstructor constructor);

	/**
	 * Return {@code true} if the referenced type is a anonymous type
	 */
	@Override
	boolean isAnonymous();

	@Override
	CtClass clone();

	/**
	 * Creates an instance of this class.
	 *
	 * Requirements:
	 * - the class must have a default constructor.
	 * - All dependencies (superclass, super-interfaces, imports) must be in the classpath,
	 * because the code is actually compiled (otherwise an exception is thrown)
	 *
	 * If the class has super-interfaces, the object can be cast to one of them.
	 * Otherwise, if the class has no super-interfaces, the methods can only be called with reflection.
	 *
	 * This instance is meant to be used for quick-testing, it uses a throwable classloader that
	 * will be garbage-collected with the instance.
	 */
	java.lang.Object newInstance();

	@Override
	@UnsettableProperty
	<C extends CtStatement> C setLabel(String label);
}
