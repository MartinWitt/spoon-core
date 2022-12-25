package io.github.martinwitt.spoonrebuilder.generics;

import java.util.ArrayList;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

public class GenericVisitor extends CtScanner {

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        if (ctClass.getSuperclass() != null && ctClass.getSuperclass().getDeclaration() != null) {
            var clazzRef = ctClass.getSuperclass();
            var clazz = clazzRef.getDeclaration();
            handleRef(clazzRef, clazz);
        }

        for (CtTypeReference<?> superReference : ctClass.getSuperInterfaces()) {
            handleRef(superReference, superReference.getDeclaration());
        }
        super.visitCtClass(ctClass);
    }

    private void handleRef(CtTypeReference<?> clazzRef, CtType<?> clazz) {
        if (clazz != null
                && !clazz.isParameterized()
                && !clazzRef.getActualTypeArguments().isEmpty()
                && isNotCtLiteral(clazz)) {
            var list = new ArrayList<>(clazzRef.getActualTypeArguments());
            list.forEach(clazzRef::removeActualTypeArgument);
            markElementForSniperPrinting(clazzRef);
        }
    }

    private boolean isNotCtLiteral(CtType<?> clazz) {
        return !clazz.isSubtypeOf(clazz.getFactory().createCtTypeReference(CtLiteral.class));
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> intrface) {
        for (CtTypeReference<?> ref : intrface.getSuperInterfaces()) {
            handleRef(ref, ref.getDeclaration());
        }
        super.visitCtInterface(intrface);
    }

    @Override
    public <T> void visitCtNewClass(CtNewClass<T> newClass) {
        var ref = newClass.getType();
        for (var typeArgument : ref.getActualTypeArguments()) {
            handleRef(typeArgument, typeArgument.getDeclaration());
        }
        markElementForSniperPrinting(newClass);
        newClass.replace(newClass.clone());
        super.visitCtNewClass(newClass);
    }

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> invocation) {
        if (!invocation.getActualTypeArguments().isEmpty()) {
            for (CtTypeReference<?> ref : invocation.getActualTypeArguments()) {
                handleRef(ref, ref.getDeclaration());
            }
            markElementForSniperPrinting(invocation);
        }
        super.visitCtInvocation(invocation);
    }

    /**
     * Modify an element such that the sniper printer detects it as modified, without changing its final content. This
     * forces it to be sniper-printed "as-is".
     */
    private static void markElementForSniperPrinting(CtElement element) {
        SourcePosition pos = element.getPosition();
        element.setPosition(SourcePosition.NOPOSITION);
        element.setPosition(pos);
    }
}
