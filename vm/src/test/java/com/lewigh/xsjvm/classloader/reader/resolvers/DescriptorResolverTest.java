package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.MethodDescriptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DescriptorResolverTest {

    @Test
    void primTypes() {
        assertEquals(Jtype.Primitive.BOOL, DescriptorResolver.resolveType("Z"));
        assertEquals(Jtype.Primitive.BYTE, DescriptorResolver.resolveType("B"));
        assertEquals(Jtype.Primitive.SHORT, DescriptorResolver.resolveType("S"));
        assertEquals(Jtype.Primitive.CHAR, DescriptorResolver.resolveType("C"));
        assertEquals(Jtype.Primitive.INT, DescriptorResolver.resolveType("I"));
        assertEquals(Jtype.Primitive.LONG, DescriptorResolver.resolveType("J"));
        assertEquals(Jtype.Primitive.FLOAT, DescriptorResolver.resolveType("F"));
        assertEquals(Jtype.Primitive.DOUBLE, DescriptorResolver.resolveType("D"));
        assertEquals(Jtype.Primitive.VOID, DescriptorResolver.resolveType("V"));
    }

    @Test
    void simpleString() {
        assertEquals(new Jtype.Reference("java/lang.String"), DescriptorResolver.resolveType("Ljava/lang.String;"));
    }

    @Test
    void arrayOfStrings() {
        assertEquals(new Jtype.Array(new Jtype.Reference("java/lang.String")), DescriptorResolver.resolveType("[Ljava/lang.String;"));
    }

    @Test
    void arrayOfInts() {
        assertEquals(new Jtype.Array(Jtype.Primitive.INT), DescriptorResolver.resolveType("[I"));
    }

    @Test
    void arrayOfArrayOfInts() {
        assertEquals(new Jtype.Array(new Jtype.Array(Jtype.Primitive.INT)), DescriptorResolver.resolveType("[[I"));
    }

    @Test
    void voidArray() {
        assertThrows(Exception.class, () -> DescriptorResolver.resolveType("[V"));
    }

    @Test
    void resolveMethodDescriptor__V() {
        resolveMethodDescriptorChack("()V", Jtype.Primitive.VOID);
    }

    @Test
    void resolveMethodDescriptor_I_V() {
        resolveMethodDescriptorChack("(I)V", Jtype.Primitive.VOID, Jtype.Primitive.INT);
    }

    @Test
    void resolveMethodDescriptor_II_V() {
        resolveMethodDescriptorChack("(II)V", Jtype.Primitive.VOID, Jtype.Primitive.INT, Jtype.Primitive.INT);
    }

    @Test
    void resolveMethodDescriptor_java_lang_Object_Z() {
        resolveMethodDescriptorChack("(Ljava/lang/Object;)Z", Jtype.Primitive.BOOL, new Jtype.Reference("java/lang/Object"));
    }

    @Test
    void resolveMethodDescriptor_java_lang_Object_String_V() {
        resolveMethodDescriptorChack("(Ljava/lang/Object;Ljava/lang/String;)V", Jtype.Primitive.VOID, new Jtype.Reference("java/lang/Object"), new Jtype.Reference("java/lang/String"));
    }

    @Test
    void resolveMethodDescriptor_java_lang_Object_I_V() {
        resolveMethodDescriptorChack("(Ljava/lang/Object;I)V", Jtype.Primitive.VOID, new Jtype.Reference("java/lang/Object"), Jtype.Primitive.INT);
    }

    @Test
    void resolveMethodDescriptor_PrintStreamOrWriter_StackTraceElement_String_String_Set_V() {
        resolveMethodDescriptorChack("(Ljava/lang/Throwable$PrintStreamOrWriter;[Ljava/lang/StackTraceElement;Ljava/lang/String;Ljava/lang/String;Ljava/util/Set;)V",
                Jtype.Primitive.VOID,
                new Jtype.Reference("java/lang/Throwable$PrintStreamOrWriter"),
                new Jtype.Array(new Jtype.Reference("java/lang/StackTraceElement")),
                new Jtype.Reference("java/lang/String"),
                new Jtype.Reference("java/lang/String"),
                new Jtype.Reference("java/util/Set")
        );
    }

    private void resolveMethodDescriptorChack(String input, Jtype retType, Jtype... params) {
        assertEquals(
                new MethodDescriptor(params, retType),
                DescriptorResolver.resolveMethodDescriptor(input));
    }
}
