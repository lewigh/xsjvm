package com.lewigh.xsjvm.classloader.reader.info.attribute;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"SpellCheckingInspection", "java:S1192"})
@Getter
@RequiredArgsConstructor
public enum OpCode {

    NOP(0, 0, "Do nothing"),

    // PUSH
    ACONST_NULL(1, 0, "Push null"),
    ICONST_M_1(2, 0, "Push int constant"),
    ICONST_0(3, 0, "Push int constant"),
    ICONST_1(4, 0, "Push int constant"),
    ICONST_2(5, 0, "Push int constant"),
    ICONST_3(6, 0, "Push int constant"),
    ICONST_4(7, 0, "Push int constant"),
    ICONST_5(8, 0, "Push int constant"),
    LCONST_0(9, 0, "Push int constant"),
    LCONST_1(10, 0, "Push int constant"),
    FCONST_0(11, 0, "Push float"),
    FCONST_1(12, 0, "Push float"),
    FCONST_2(13, 0, "Push float"),
    DCONST_0(14, 0, "Push double"),
    DCONST_1(15, 0, "Push double"),
    BIPUSH(16, 1, "Push byte"),
    SIPUSH(17, 0, "Push short"),
    LDC(18, 1, "Push item from run-time constant pool"),
    LDC_W(19, 2, "Push item from run-time constant pool (wide index)"),
    LDC_2_W(20, 2, "Push long or double from run-time constant pool (wide index)"),

    // LOAD

    ILOAD(21, 1, "Load float from local variable"),
    LLOAD(22, 1, "Load float from local variable"),
    FLOAD(23, 1, "Load float from local variable"),
    DLOAD(24, 1, "Load double from local variable"),
    ALOAD(25, 1, "Load reference from local variable"),
    ILOAD_0(26, 0, "Load float from local variable"),
    ILOAD_1(27, 0, "Load float from local variable"),
    ILOAD_2(28, 0, "Load float from local variable"),
    ILOAD_3(29, 0, "Load float from local variable"),
    LLOAD_0(30, 0, "Load float from local variable"),
    LLOAD_1(31, 0, "Load float from local variable"),
    LLOAD_2(32, 0, "Load float from local variable"),
    LLOAD_3(33, 0, "Load float from local variable"),
    FLOAD_0(34, 0, "Load float from local variable"),
    FLOAD_1(35, 0, "Load float from local variable"),
    FLOAD_2(36, 0, "Load float from local variable"),
    FLOAD_3(37, 0, "Load float from local variable"),
    DLOAD_0(38, 0, "Load double from local variable"),
    DLOAD_1(39, 0, "Load double from local variable"),
    DLOAD_2(40, 0, "Load double from local variable"),
    DLOAD_3(41, 0, "Load double from local variable"),
    ALOAD_0(42, 0, "Load reference from local variable"),
    ALOAD_1(43, 0, "Load reference from local variable"),
    ALOAD_2(44, 0, "Load reference from local variable"),
    ALOAD_3(45, 0, "Load reference from local variable"),

    // ARRAY LOAD
    IALOAD(46, 0, "Load float from array"),
    LALOAD(47, 0, "Load float from array"),
    FALOAD(48, 0, "Load float from array"),
    DALOAD(49, 0, "Load double from array"),
    AALOAD(50, 0, "Load reference from array"),
    BALOAD(51, 0, "Load byte or boolean from array"),
    CALOAD(52, 0, "Load char from array"),
    SALOAD(53, 0, "Load short from array"),

    // STORE
    ISTORE(54, 1, "Store float into local variable"),
    LSTORE(55, 1, "Store float into local variable"),
    FSTORE(56, 1, "Store float into local variable"),
    DSTORE(57, 1, "Store double into local variable"),
    ASTORE(58, 1, "Store reference into local variable"),
    ISTORE_0(59, 0, "Store float into local variable"),
    ISTORE_1(60, 0, "Store float into local variable"),
    ISTORE_2(61, 0, "Store float into local variable"),
    ISTORE_3(62, 0, "Store float into local variable"),
    LSTORE_0(63, 0, "Store float into local variable"),
    LSTORE_1(64, 0, "Store float into local variable"),
    LSTORE_2(65, 0, "Store float into local variable"),
    LSTORE_3(66, 0, "Store float into local variable"),
    FSTORE_0(67, 0, "Store float into local variable"),
    FSTORE_1(68, 0, "Store float into local variable"),
    FSTORE_2(69, 0, "Store float into local variable"),
    FSTORE_3(70, 0, "Store float into local variable"),
    DSTORE_0(71, 0, "Store double into local variable"),
    DSTORE_1(72, 0, "Store double into local variable"),
    DSTORE_2(73, 0, "Store double into local variable"),
    DSTORE_3(74, 0, "Store double into local variable"),
    ASTORE_0(75, 0, "Store reference into local variable"),
    ASTORE_1(76, 0, "Store reference into local variable"),
    ASTORE_2(77, 0, "Store reference into local variable"),
    ASTORE_3(78, 0, "Store reference into local variable"),

    // ARRAY STORE
    IASTORE(79, 0, "Store into float array"),
    LASTORE(80, 0, "Store into float array"),
    FASTORE(81, 0, "Store into float array"),
    DASTORE(82, 0, "Store into double array"),
    AASTORE(83, 0, "Store into reference array"),
    BASTORE(84, 0, "Store into byte or boolean array"),
    CASTORE(85, 0, "Store into char array"),
    SASTORE(86, 0, "Store into short array"),

    // POP
    POP(87, 0, "Pop the top operand stack index"),
    POP_2(88, 0, "Pop the top one or two operand stack values"),

    // DUP
    DUP(89, 0, "Duplicate the top operand stack index"),
    DUP_X_1(90, 0, "Duplicate the top operand stack index and insert two values down"),
    DUP_X_2(91, 0, "Duplicate the top operand stack index and insert two or three values down"),
    DUP_2(92, 0, "Duplicate the top one or two operand stack values"),
    DUP_2_X_1(93, 0, "Duplicate the top one or two operand stack values and insert two or three values down"),
    DUP_2_X_2(94, 0, "Duplicate the top one or two operand stack values and insert two, three, or four values down"),

    //SWAP
    SWAP(95, 0, "Swap the top two operand stack values"),

    // ADD
    IADD(96, 0, "Add float"),
    LADD(97, 0, "Add float"),
    FADD(98, 0, "Add float"),
    DADD(99, 0, "Add double"),

    // SUB
    ISUB(100, 0, "Subtract float"),
    LSUB(101, 0, "Subtract float"),
    FSUB(102, 0, "Subtract float"),
    DSUB(103, 0, "Subtract double"),

    // MUL
    IMUL(104, 0, "Multiply float"),
    LMUL(105, 0, "Multiply float"),
    FMUL(106, 0, "Multiply float"),
    DMUL(107, 0, "Multiply double"),


    //DIV
    IDIV(108, 0, "Divide float"),
    LDIV(109, 0, "Divide float"),
    FDIV(110, 0, "Divide float"),
    DDIV(111, 0, "Divide double"),

    // REM
    IREM(112, 0, "Remainder float"),
    LREM(113, 0, "Remainder float"),
    FREM(114, 0, "Remainder float"),
    DREM(115, 0, "Remainder double"),


    // NEG
    INEG(116, 0, "Negate float"),
    LNEG(117, 0, "Negate float"),
    FNEG(118, 0, "Negate float"),
    DNEG(119, 0, "Negate double"),


    ISHL(120, 0, "Shift left int"),
    LSHL(121, 0, "Shift left int"),
    ISHR(122, 0, "Arithmetic shift right int"),
    LSHR(123, 0, "Arithmetic shift right int"),
    IUSHR(124, 0, "Logical shift right int"),
    LUSHR(125, 0, "Logical shift right long"),
    IAND(126, 0, "Boolean AND int"),
    LAND(127, 0, "Boolean AND int"),
    IOR(128, 0, "Invoke dynamic method"),
    LOR(129, 0, "Invoke dynamic method"),
    IXOR(130, 0, "Boolean XOR int"),
    LXOR(131, 0, "Boolean XOR long"),
    IINC(132, 2, "Increment local variable by constant"),

    // Convert
    I_2_L(133, 0, "Convert float to long"),
    I_2_F(134, 0, "Convert int to float"),
    I_2_D(135, 0, "Convert float to double"),
    L_2_I(136, 0, "Convert long to int"),
    L_2_F(137, 0, "Convert int to float"),
    L_2_D(138, 0, "Convert float to double"),
    F_2_I(139, 0, "Convert float to int"),
    F_2_L(140, 0, "Convert float to long"),
    F_2_D(141, 0, "Convert float to double"),
    D_2_I(142, 0, "Convert double to int"),
    D_2_L(143, 0, "Convert double to long"),
    D_2_F(144, 0, "Convert double to float"),
    I_2_B(145, 0, "Convert int to byte"),
    I_2_C(146, 0, "Convert int to char"),
    I_2_S(147, 0, "Convert int to short"),

    // Compare
    LCMP(148, 0, "Compare long"),
    FCMPL(149, 0, "Compare float"),
    FCMPG(150, 0, "Compare float"),
    DCMPL(151, 0, "Compare double"),
    DCMPG(152, 0, "Compare double"),

    // BRANCH
    IFEQ(153, 2, "Branch if int comparison with zero succeeds"),
    IFNE(154, 2, "Branch if int comparison with zero succeeds"),
    IFLT(155, 2, "Branch if int comparison with zero succeeds"),
    IFGE(156, 2, "Branch if int comparison with zero succeeds"),
    IFGT(157, 2, "Branch if int comparison with zero succeeds"),
    IFLE(158, 2, "Branch if int comparison with zero succeeds"),
    IF_ICMPEQ(159, 2, "Branch if int comparison succeeds"),
    IF_ICMPNE(160, 2, "Branch if int comparison succeeds"),
    IF_ICMPLT(161, 2, "Branch if int comparison succeeds"),
    IF_ICMPGE(162, 2, "Branch if int comparison succeeds"),
    IF_ICMPGT(163, 2, "Branch if int comparison succeeds"),
    IF_ICMPLE(164, 2, "Branch if int comparison succeeds"),
    IF_ACMPEQ(165, 2, "Branch if reference comparison succeeds"),
    IF_ACMPNE(166, 2, "Branch if reference comparison succeeds"),

    // JUMP
    GOTO(167, 2, "Branch always"),
    JSR(168, 2, "Jump subroutine"),
    RET(169, 1, "Return from subroutine"),
    TABLESWITCH(170, 0, "Access jump table by index and jump"),

    LOOKUPSWITCH(171, 0, "Negate float"),


    //RETURN
    IRETURN(172, 0, "Return float from method"),
    LRETURN(173, 0, "Return float from method"),
    FRETURN(174, 0, "Return float from method"),
    DRETURN(175, 0, "Return double from method"),
    ARETURN(176, 0, "Return reference from method"),
    RETURN(177, 0, "Return void from method"),

    // FIELDS
    GETSTATIC(178, 2, "Get static field from class"),
    PUTSTATIC(179, 2, "Set static field in class"),
    GETFIELD(180, 2, "Fetch field from object"),
    PUTFIELD(181, 2, "Set field in object"),

    // INVOKE
    INVOKEVIRTUAL(182, 2, "Invoke instance method; dispatch based on class"),
    INVOKESPECIAL(183, 2, "Invoke instance method; special handling for superclass, private, and instance initialization method invocations"),
    INVOKESTATIC(184, 2, "Invoke a class (static) method"),
    INVOKEINTERFACE(185, 4, "Invoke dynamic method"),
    INVOKEDYNAMIC(186, 4, "Invoke dynamic method"),

    // NEW
    NEW(187, 2, "Create new object"),
    NEWARRAY(188, 1, "Create new array"),
    ANEWARRAY(189, 2, "Create new array of reference"),

    // ARRAY
    ARRAYLENGTH(190, 0, "Get length of array"),

    // THROW
    ATHROW(191, 0, "Throw exception or error"),


    // CHECK TYPES
    CHECKCAST(192, 2, "Check whether object is of given type"),
    INSTANCEOF(193, 2, "Determine if object is of given type"),

    // MONITORS
    MONITORENTER(194, 0, "Enter monitor for object"),
    MONITOREXIT(195, 0, "Exit monitor for object"),

    WIDE(196, 0, "Extend local variable index by additional bytes"),

    // MULTY ARRAY
    MULTIANEWARRAY(197, 3, "Create new multidimensional array"),

    GOTO_W(200, 4, "Branch always (wide index)"),
    IFNONNULL(199, 0, "Branch if reference not null"),
    IFNULL(198, 0, "Branch if reference is null"),
    JSR_W(201, 4, "Jump subroutine (wide index)");


    private final int code;
    private final int operandAmount;
    private final String description;

    public static OpCode byCode(int code) {
        List<OpCode> result = Arrays.stream(values())
                .filter(a -> a.code == code)
                .toList();

        if (result.size() > 1) {
            throw new IllegalArgumentException("More than one match was found for the %s code, variations: %s".formatted(code, result));
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("No matching operation was found for the %s code".formatted(code));
        }

        return result.get(0);
    }

    public static OpCode[] resolveFrom(byte[] bytes) {

        OpCode[] opCodes = new OpCode[bytes.length];

        for (int i = 0; i < bytes.length; i++) {

            byte b = bytes[i];
            int b1 = Byte.toUnsignedInt(b);

            opCodes[i] = OpCode.byCode(b1);

        }

        return opCodes;
    }

    public OpCode by(byte b) {
        int id = Byte.toUnsignedInt(b);

        return Arrays.stream(values())
                .filter(a -> a.code == id)
                .findFirst()
                .orElseThrow();
    }

}
