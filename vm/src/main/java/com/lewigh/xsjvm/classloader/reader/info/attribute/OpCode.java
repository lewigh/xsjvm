package com.lewigh.xsjvm.classloader.reader.info.attribute;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.lewigh.xsjvm.classloader.reader.info.attribute.OpCode.OperandsType.*;

@SuppressWarnings({"SpellCheckingInspection", "java:S1192"})
@Getter
@RequiredArgsConstructor
public enum OpCode {

    NOP(0, EMPTY, "Do nothing"),

    // PUSH
    ACONST_NULL(1, EMPTY, "Push null"),
    ICONST_M_1(2, EMPTY, "Push int constant"),
    ICONST_0(3, EMPTY, "Push int constant"),
    ICONST_1(4, EMPTY, "Push int constant"),
    ICONST_2(5, EMPTY, "Push int constant"),
    ICONST_3(6, EMPTY, "Push int constant"),
    ICONST_4(7, EMPTY, "Push int constant"),
    ICONST_5(8, EMPTY, "Push int constant"),
    LCONST_0(9, EMPTY, "Push int constant"),
    LCONST_1(10, EMPTY, "Push int constant"),
    FCONST_0(11, EMPTY, "Push float"),
    FCONST_1(12, EMPTY, "Push float"),
    FCONST_2(13, EMPTY, "Push float"),
    DCONST_0(14, EMPTY, "Push double"),
    DCONST_1(15, EMPTY, "Push double"),
    BIPUSH(16, UBYTE, "Push byte"),
    SIPUSH(17, C_SHORT, "Push short"),
    LDC(18, UBYTE, "Push item from run-time constant pool"),
    LDC_W(19, C_SHORT, "Push item from run-time constant pool (wide index)"),
    LDC2_W(20, C_SHORT, "Push long or double from run-time constant pool (wide index)"),

    // LOAD

    ILOAD(21, UBYTE, "Load float from local variable"),
    LLOAD(22, UBYTE, "Load float from local variable"),
    FLOAD(23, UBYTE, "Load float from local variable"),
    DLOAD(24, UBYTE, "Load double from local variable"),
    ALOAD(25, UBYTE, "Load reference from local variable"),
    ILOAD_0(26, EMPTY, "Load float from local variable"),
    ILOAD_1(27, EMPTY, "Load float from local variable"),
    ILOAD_2(28, EMPTY, "Load float from local variable"),
    ILOAD_3(29, EMPTY, "Load float from local variable"),
    LLOAD_0(30, EMPTY, "Load float from local variable"),
    LLOAD_1(31, EMPTY, "Load float from local variable"),
    LLOAD_2(32, EMPTY, "Load float from local variable"),
    LLOAD_3(33, EMPTY, "Load float from local variable"),
    FLOAD_0(34, EMPTY, "Load float from local variable"),
    FLOAD_1(35, EMPTY, "Load float from local variable"),
    FLOAD_2(36, EMPTY, "Load float from local variable"),
    FLOAD_3(37, EMPTY, "Load float from local variable"),
    DLOAD_0(38, EMPTY, "Load double from local variable"),
    DLOAD_1(39, EMPTY, "Load double from local variable"),
    DLOAD_2(40, EMPTY, "Load double from local variable"),
    DLOAD_3(41, EMPTY, "Load double from local variable"),
    ALOAD_0(42, EMPTY, "Load reference from local variable"),
    ALOAD_1(43, EMPTY, "Load reference from local variable"),
    ALOAD_2(44, EMPTY, "Load reference from local variable"),
    ALOAD_3(45, EMPTY, "Load reference from local variable"),

    // ARRAY LOAD
    IALOAD(46, EMPTY, "Load float from array"),
    LALOAD(47, EMPTY, "Load float from array"),
    FALOAD(48, EMPTY, "Load float from array"),
    DALOAD(49, EMPTY, "Load double from array"),
    AALOAD(50, EMPTY, "Load reference from array"),
    BALOAD(51, EMPTY, "Load byte or boolean from array"),
    CALOAD(52, EMPTY, "Load char from array"),
    SALOAD(53, EMPTY, "Load short from array"),

    // STORE
    ISTORE(54, UBYTE, "Store float into local variable"),
    LSTORE(55, UBYTE, "Store float into local variable"),
    FSTORE(56, UBYTE, "Store float into local variable"),
    DSTORE(57, UBYTE, "Store double into local variable"),
    ASTORE(58, UBYTE, "Store reference into local variable"),
    ISTORE_0(59, EMPTY, "Store float into local variable"),
    ISTORE_1(60, EMPTY, "Store float into local variable"),
    ISTORE_2(61, EMPTY, "Store float into local variable"),
    ISTORE_3(62, EMPTY, "Store float into local variable"),
    LSTORE_0(63, EMPTY, "Store float into local variable"),
    LSTORE_1(64, EMPTY, "Store float into local variable"),
    LSTORE_2(65, EMPTY, "Store float into local variable"),
    LSTORE_3(66, EMPTY, "Store float into local variable"),
    FSTORE_0(67, EMPTY, "Store float into local variable"),
    FSTORE_1(68, EMPTY, "Store float into local variable"),
    FSTORE_2(69, EMPTY, "Store float into local variable"),
    FSTORE_3(70, EMPTY, "Store float into local variable"),
    DSTORE_0(71, EMPTY, "Store double into local variable"),
    DSTORE_1(72, EMPTY, "Store double into local variable"),
    DSTORE_2(73, EMPTY, "Store double into local variable"),
    DSTORE_3(74, EMPTY, "Store double into local variable"),
    ASTORE_0(75, EMPTY, "Store reference into local variable"),
    ASTORE_1(76, EMPTY, "Store reference into local variable"),
    ASTORE_2(77, EMPTY, "Store reference into local variable"),
    ASTORE_3(78, EMPTY, "Store reference into local variable"),

    // ARRAY STORE
    IASTORE(79, EMPTY, "Store into float array"),
    LASTORE(80, EMPTY, "Store into float array"),
    FASTORE(81, EMPTY, "Store into float array"),
    DASTORE(82, EMPTY, "Store into double array"),
    AASTORE(83, EMPTY, "Store into reference array"),
    BASTORE(84, EMPTY, "Store into byte or boolean array"),
    CASTORE(85, EMPTY, "Store into char array"),
    SASTORE(86, EMPTY, "Store into short array"),

    // POP
    POP(87, EMPTY, "Pop the top operand stack index"),
    POP_2(88, EMPTY, "Pop the top one or two operand stack values"),

    // DUP
    DUP(89, EMPTY, "Duplicate the top operand stack index"),
    DUP_X_1(90, EMPTY, "Duplicate the top operand stack index and insert two values down"),
    DUP_X_2(91, EMPTY, "Duplicate the top operand stack index and insert two or three values down"),
    DUP_2(92, EMPTY, "Duplicate the top one or two operand stack values"),
    DUP_2_X_1(93, EMPTY, "Duplicate the top one or two operand stack values and insert two or three values down"),
    DUP_2_X_2(94, EMPTY, "Duplicate the top one or two operand stack values and insert two, three, or four values down"),

    //SWAP
    SWAP(95, EMPTY, "Swap the top two operand stack values"),

    // ADD
    IADD(96, EMPTY, "Add float"),
    LADD(97, EMPTY, "Add float"),
    FADD(98, EMPTY, "Add float"),
    DADD(99, EMPTY, "Add double"),

    // SUB
    ISUB(100, EMPTY, "Subtract float"),
    LSUB(101, EMPTY, "Subtract float"),
    FSUB(102, EMPTY, "Subtract float"),
    DSUB(103, EMPTY, "Subtract double"),

    // MUL
    IMUL(104, EMPTY, "Multiply float"),
    LMUL(105, EMPTY, "Multiply float"),
    FMUL(106, EMPTY, "Multiply float"),
    DMUL(107, EMPTY, "Multiply double"),


    //DIV
    IDIV(108, EMPTY, "Divide float"),
    LDIV(109, EMPTY, "Divide float"),
    FDIV(110, EMPTY, "Divide float"),
    DDIV(111, EMPTY, "Divide double"),

    // REM
    IREM(112, EMPTY, "Remainder float"),
    LREM(113, EMPTY, "Remainder float"),
    FREM(114, EMPTY, "Remainder float"),
    DREM(115, EMPTY, "Remainder double"),


    // NEG
    INEG(116, EMPTY, "Negate float"),
    LNEG(117, EMPTY, "Negate float"),
    FNEG(118, EMPTY, "Negate float"),
    DNEG(119, EMPTY, "Negate double"),


    ISHL(120, EMPTY, "Shift left int"),
    LSHL(121, EMPTY, "Shift left int"),
    ISHR(122, EMPTY, "Arithmetic shift right int"),
    LSHR(123, EMPTY, "Arithmetic shift right int"),
    IUSHR(124, EMPTY, "Logical shift right int"),
    LUSHR(125, EMPTY, "Logical shift right long"),
    IAND(126, EMPTY, "Boolean AND int"),
    LAND(127, EMPTY, "Boolean AND int"),
    IOR(128, EMPTY, "Invoke dynamic method"),
    LOR(129, EMPTY, "Invoke dynamic method"),
    IXOR(130, EMPTY, "Boolean XOR int"),
    LXOR(131, EMPTY, "Boolean XOR long"),
    IINC(132, UBYTE_BYTE, "Increment local variable by constant"),

    // Convert
    I_2_L(133, EMPTY, "Convert float to long"),
    I_2_F(134, EMPTY, "Convert int to float"),
    I_2_D(135, EMPTY, "Convert float to double"),
    L_2_I(136, EMPTY, "Convert long to int"),
    L_2_F(137, EMPTY, "Convert int to float"),
    L_2_D(138, EMPTY, "Convert float to double"),
    F_2_I(139, EMPTY, "Convert float to int"),
    F_2_L(140, EMPTY, "Convert float to long"),
    F_2_D(141, EMPTY, "Convert float to double"),
    D_2_I(142, EMPTY, "Convert double to int"),
    D_2_L(143, EMPTY, "Convert double to long"),
    D_2_F(144, EMPTY, "Convert double to float"),
    I_2_B(145, EMPTY, "Convert int to byte"),
    I_2_C(146, EMPTY, "Convert int to char"),
    I_2_S(147, EMPTY, "Convert int to short"),

    // Compare
    LCMP(148, EMPTY, "Compare long"),
    FCMPL(149, EMPTY, "Compare float"),
    FCMPG(150, EMPTY, "Compare float"),
    DCMPL(151, EMPTY, "Compare double"),
    DCMPG(152, EMPTY, "Compare double"),

    // BRANCH
    IFEQ(153, C_SHORT, "Branch if int comparison with zero succeeds"),
    IFNE(154, C_SHORT, "Branch if int comparison with zero succeeds"),
    IFLT(155, C_SHORT, "Branch if int comparison with zero succeeds"),
    IFGE(156, C_SHORT, "Branch if int comparison with zero succeeds"),
    IFGT(157, C_SHORT, "Branch if int comparison with zero succeeds"),
    IFLE(158, C_SHORT, "Branch if int comparison with zero succeeds"),
    IF_ICMPEQ(159, C_SHORT, "Branch if int comparison succeeds"),
    IF_ICMPNE(160, C_SHORT, "Branch if int comparison succeeds"),
    IF_ICMPLT(161, C_SHORT, "Branch if int comparison succeeds"),
    IF_ICMPGE(162, C_SHORT, "Branch if int comparison succeeds"),
    IF_ICMPGT(163, C_SHORT, "Branch if int comparison succeeds"),
    IF_ICMPLE(164, C_SHORT, "Branch if int comparison succeeds"),
    IF_ACMPEQ(165, C_SHORT, "Branch if reference comparison succeeds"),
    IF_ACMPNE(166, C_SHORT, "Branch if reference comparison succeeds"),

    // JUMP
    GOTO(167, C_SHORT, "Branch always"),
    JSR(168, C_SHORT, "Jump subroutine"),
    RET(169, UBYTE, "Return from subroutine"),
    TABLESWITCH(170, EMPTY, "Access jump table by index and jump"),

    LOOKUPSWITCH(171, EMPTY, "Negate float"),


    //RETURN
    IRETURN(172, EMPTY, "Return float from method"),
    LRETURN(173, EMPTY, "Return float from method"),
    FRETURN(174, EMPTY, "Return float from method"),
    DRETURN(175, EMPTY, "Return double from method"),
    ARETURN(176, EMPTY, "Return reference from method"),
    RETURN(177, EMPTY, "Return void from method"),

    // FIELDS
    GETSTATIC(178, C_SHORT, "Get static field from class"),
    PUTSTATIC(179, C_SHORT, "Set static field in class"),
    GETFIELD(180, C_SHORT, "Fetch field from object"),
    PUTFIELD(181, C_SHORT, "Set field in object"),

    // INVOKE
    INVOKEVIRTUAL(182, C_SHORT, "Invoke instance method; dispatch based on class"),
    INVOKESPECIAL(183, C_SHORT, "Invoke instance method; special handling for superclass, private, and instance initialization method invocations"),
    INVOKESTATIC(184, C_SHORT, "Invoke a class (static) method"),
    INVOKEINTERFACE(185, C_SHORT_UBYTE_ZERO, "Invoke dynamic method"),
    INVOKEDYNAMIC(186, C_SHORT_ZERO_ZERO, "Invoke dynamic method"),

    // NEW
    NEW(187, C_SHORT, "Create new object"),
    NEWARRAY(188, UBYTE, "Create new array"),
    ANEWARRAY(189, C_SHORT, "Create new array of reference"),

    // ARRAY
    ARRAYLENGTH(190, EMPTY, "Get length of array"),

    // THROW
    ATHROW(191, EMPTY, "Throw exception or error"),


    // CHECK TYPES
    CHECKCAST(192, C_SHORT, "Check whether object is of given type"),
    INSTANCEOF(193, C_SHORT, "Determine if object is of given type"),

    // MONITORS
    MONITORENTER(194, EMPTY, "Enter monitor for object"),
    MONITOREXIT(195, EMPTY, "Exit monitor for object"),

    WIDE(196, EMPTY, "Extend local variable index by additional bytes"),

    // MULTY ARRAY
    MULTIANEWARRAY(197, C_SHORT_UBYTE, "Create new multidimensional array"),

    GOTO_W(200, C_INT, "Branch always (wide index)"),
    IFNONNULL(199, EMPTY, "Branch if reference not null"),
    IFNULL(198, EMPTY, "Branch if reference is null"),
    JSR_W(201, C_INT, "Jump subroutine (wide index)");


    private final int code;
    private final OperandsType operandsType;
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


    @Getter
    @RequiredArgsConstructor
    public enum OperandsType {
        EMPTY(0),
        UBYTE(1),
        UBYTE_UBYTE(2),
        UBYTE_BYTE(2),
        C_SHORT(2),
        C_SHORT_UBYTE(3),
        C_SHORT_ZERO_ZERO(4),
        C_SHORT_UBYTE_ZERO(4),
        C_INT(4);

        private final int bytesLen;

        public short[] retrieve(byte[] buffer) {
            return switch (this) {
                case UBYTE, UBYTE_UBYTE -> {
                    short[] shorts = new short[buffer.length];
                    for (int i = 0; i < buffer.length; i++) {
                        shorts[i] = (short) Byte.toUnsignedInt(buffer[i]);
                    }
                    yield shorts;
                }
                case UBYTE_BYTE -> new short[]{
                        (short) Byte.toUnsignedInt(buffer[0]),
                        buffer[1]
                };
                case C_SHORT, C_SHORT_ZERO_ZERO -> new short[]{(short) ((buffer[0] << 8) | buffer[1] & 0xff)};
                case C_SHORT_UBYTE, C_SHORT_UBYTE_ZERO -> new short[]{
                        (short) ((buffer[0] << 8) | buffer[1] & 0xff),
                        (short) Byte.toUnsignedInt(buffer[2])
                };
                case C_INT -> new short[]{(short) ((buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | buffer[3] & 0xff)};
                case EMPTY -> new short[0];
            };
        }
    }

}
