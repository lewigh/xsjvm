package com.lewigh.xsjvm.engine;


import com.lewigh.xsjvm.classloader.AppClassLoader;
import com.lewigh.xsjvm.classloader.reader.info.attribute.ExceptionTable;
import com.lewigh.xsjvm.classloader.reader.info.attribute.Instruction;
import com.lewigh.xsjvm.classloader.reader.pool.Constant;
import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.engine.runtime.*;
import com.lewigh.xsjvm.mem.VmMemoryManager;
import com.lewigh.xsjvm.support.Logger;
import lombok.NonNull;

import java.util.*;

import static com.lewigh.xsjvm.SymbolTable.ENTRY_POINT_METHOD_DESC;
import static com.lewigh.xsjvm.SymbolTable.ENTRY_POINT_METHOD_NAME;

@SuppressWarnings({"java:S1119", "DuplicateBranchesInSwitch"})
public class ExecutionEngine {

    private final AppClassLoader classLoader;
    private final VmMemoryManager memoryManager;

    private final Map<Constant.MethodRefInfo, ClassAndMethodDesc> methodDescriptors = new HashMap<>();
    private final Map<Constant.FieldInfo, ClassAndFieldDesc> fieldDescriptors = new HashMap<>();


    public ExecutionEngine(AppClassLoader appClassLoader, VmMemoryManager allocator) {
        this.classLoader = appClassLoader;
        this.memoryManager = allocator;
    }


    public void execute(String className) {
        var threadStack = new ThreadStack();
        var mainClass = getClass(className, threadStack);
        var mainMethod = mainClass.findMethod(ENTRY_POINT_METHOD_NAME, ENTRY_POINT_METHOD_DESC, InvokeType.STATIC);
        var mainFrame = StackFrame.create(mainClass, mainMethod);

        threadStack.push(mainFrame);

        runThreadLoop(threadStack);
    }


    private void runThreadLoop(ThreadStack threadStack) {
        for (; ; ) {
            if (executeMethod(threadStack)) break;
        }
    }

    private boolean executeMethod(ThreadStack threadStack) {
        try {

            if (threadStack.isEmpty()) {
                Logger.debug("End of thread%n");
                return true;
            }

            var frame = threadStack.top();
            var method = frame.getMethod();

            Logger.invoke(frame);

            loop:
            for (var i = frame.ip; i < method.instructions().length; i++) {
                frame.setIp(i);
                var instruction = method.instructions()[i];
                var opCode = instruction.opCode();

                switch (opCode) {
                    case ACONST_NULL -> {
                        frame.inc();
                        frame.push(new Value.Ref.Null());
                    }
                    case ICONST_0 -> {
                        frame.inc();
                        iconst(frame, 0);
                    }
                    case ICONST_1 -> {
                        frame.inc();
                        iconst(frame, 1);
                    }
                    case ICONST_2 -> {
                        frame.inc();
                        iconst(frame, 2);
                    }
                    case ICONST_3 -> {
                        frame.inc();
                        iconst(frame, 3);
                    }
                    case ICONST_4 -> {
                        frame.inc();
                        iconst(frame, 4);
                    }
                    case ICONST_5 -> {
                        frame.inc();
                        iconst(frame, 5);
                    }
                    case FCONST_2 -> {
                        frame.inc();
                        frame.push(new Value.Float(2.0f));
                    }
                    case LDC, LDC_W -> ldc(frame, instruction);
                    case ISTORE -> {
                        frame.inc();

                        frame.popAndStoreTo(instruction.firstOperand());
                    }

                    case ISTORE_0 -> {
                        frame.inc();
                        frame.popAndStoreTo(0);
                    }
                    case ISTORE_1 -> {
                        frame.inc();
                        frame.popAndStoreTo(1);
                    }
                    case ISTORE_2 -> {
                        frame.inc();
                        frame.popAndStoreTo(2);
                    }

                    case ISTORE_3 -> {
                        frame.inc();
                        frame.popAndStoreTo(3);
                    }
                    case FSTORE -> frame.popAndStoreTo(instruction.firstOperand());
                    case FSTORE_0 -> {
                        frame.inc();
                        frame.popAndStoreTo(0);
                    }
                    case ILOAD_0 -> {
                        frame.inc();
                        frame.push(frame.load(0));
                    }
                    case ILOAD_1 -> {
                        frame.inc();
                        frame.push(frame.load(1));
                    }
                    case ILOAD_2 -> {
                        frame.push(frame.load(2));
                    }
                    case FLOAD_0 -> {
                        frame.push(frame.load(0));
                    }
                    case IADD -> {
                        frame.inc();
                        Value operA = frame.pop();
                        Value operB = frame.pop();
                        if (operA instanceof Value.Int a && operB instanceof Value.Int b) {
                            frame.push(new Value.Int(a.value() + b.value()));
                        } else {
                            throw StackFrame.Exception.create("Operands have wrong types %s %s for adding.".formatted(operA, operB), frame);
                        }
                    }
                    case IRETURN, LRETURN, FRETURN, DRETURN, ARETURN -> {
                        var cur = threadStack.pop();
                        Value retVal = cur.pop();
                        threadStack.top().push(retVal);
                        Logger.retval(frame, retVal);
                        break loop;
                    }
                    case RETURN -> {
                        threadStack.pop();
                        Logger.ret(frame);
                        break loop;
                    }
                    case GETSTATIC -> getStatic(frame, instruction, threadStack);
                    case PUTSTATIC -> putStatic(frame, instruction, threadStack);
                    case GETFIELD -> getField(threadStack, frame, instruction);
                    case PUTFIELD -> putField(threadStack, frame, instruction);
                    case INVOKESTATIC -> {
                        invoke(InvokeType.STATIC, threadStack, frame, instruction);
                        break loop;
                    }
                    case INVOKESPECIAL -> {
                        invoke(InvokeType.SPECIAL, threadStack, frame, instruction);
                        break loop;
                    }
                    case INVOKEVIRTUAL -> {
                        invoke(InvokeType.VIRTUAL, threadStack, frame, instruction);
                        break loop;
                    }
                    case INVOKEINTERFACE -> {
                        invoke(InvokeType.INTERFACE, threadStack, frame, instruction);
                        break loop;
                    }
                    case DUP -> {
                        frame.inc();

                        Value poped = frame.pop();

                        frame.push(poped);
                        frame.push(poped);
                    }
                    case POP -> {
                        frame.inc();
                        frame.pop();
                    }
                    case NEW -> newObject(threadStack, frame, instruction);
                    case NEWARRAY -> newArray(frame, instruction);
                    case ANEWARRAY -> newReferenceArray(threadStack, frame, instruction);
                    case BIPUSH -> {
                        frame.inc();
                        frame.push(new Value.Byte((byte) instruction.firstOperand()));
                    }
                    case ASTORE_0 -> {
                        frame.inc();
                        frame.popAndStoreTo(0);
                    }
                    case ASTORE_1 -> {
                        frame.inc();
                        frame.popAndStoreTo(1);
                    }
                    case ASTORE_2 -> {
                        frame.inc();
                        frame.popAndStoreTo(2);
                    }
                    case ASTORE_3 -> {
                        frame.inc();
                        frame.popAndStoreTo(3);
                    }
                    case ASTORE -> {
                        frame.inc();
                        short localId = instruction.firstOperand();
                        frame.popAndStoreTo(localId);
                    }
                    case ALOAD -> {
                        frame.inc();

                        short operand = instruction.firstOperand();

                        frame.push(frame.load(operand));
                    }
                    case ALOAD_0 -> {
                        frame.inc();

                        frame.push(frame.load(0));
                    }
                    case ALOAD_1 -> {
                        frame.inc();

                        frame.push(frame.load(1));
                    }
                    case IASTORE -> storeArrayElement(frame, Jtype.Primitive.INT);
                    case BASTORE -> storeArrayElement(frame, Jtype.Primitive.BYTE);
                    case FASTORE -> storeArrayElement(frame, Jtype.Primitive.FLOAT);
                    case DASTORE -> storeArrayElement(frame, Jtype.Primitive.DOUBLE);
                    case CASTORE -> storeArrayElement(frame, Jtype.Primitive.CHAR);
                    case SASTORE -> storeArrayElement(frame, Jtype.Primitive.SHORT);
                    case LASTORE -> storeArrayElement(frame, Jtype.Primitive.LONG);
                    case AASTORE -> storeArrayElement(frame, Jtype.Primitive.REFERENCE);
                    case IALOAD -> loadArrayElement(frame, Jtype.Primitive.INT);
                    case BALOAD -> loadArrayElement(frame, Jtype.Primitive.BYTE);
                    case FALOAD -> loadArrayElement(frame, Jtype.Primitive.FLOAT);
                    case DALOAD -> loadArrayElement(frame, Jtype.Primitive.DOUBLE);
                    case CALOAD -> loadArrayElement(frame, Jtype.Primitive.CHAR);
                    case SALOAD -> loadArrayElement(frame, Jtype.Primitive.SHORT);
                    case LALOAD -> loadArrayElement(frame, Jtype.Primitive.LONG);
                    case AALOAD -> loadArrayElement(frame, Jtype.Primitive.REFERENCE);

                    case IF_ICMPEQ -> {
                        if (icmp(CmpType.EQ, frame, instruction)) {
                            break loop;
                        }
                    }
                    case IF_ICMPNE -> {
                        if (icmp(CmpType.NE, frame, instruction)) {
                            break loop;
                        }
                    }
                    case IF_ICMPLT -> {
                        if (icmp(CmpType.LT, frame, instruction)) {
                            break loop;
                        }
                    }
                    case IF_ICMPGE -> {
                        if (icmp(CmpType.GE, frame, instruction)) {
                            break loop;
                        }
                    }
                    case IF_ICMPGT -> {
                        if (icmp(CmpType.GT, frame, instruction)) {
                            break loop;
                        }
                    }
                    case IF_ICMPLE -> {
                        if (icmp(CmpType.LE, frame, instruction)) {
                            break loop;
                        }
                    }
                    case IFNULL -> ifNullable(frame, instruction, true);
                    case IFNONNULL -> ifNullable(frame, instruction, false);
                    case I_2_L -> {
                        frame.inc();
                        Value.Int v = frame.popInt();

                        frame.push(new Value.Long(v.value()));
                    }
                    case ARRAYLENGTH -> arrayLength(frame);
                    case ATHROW -> aThrow(frame);
                    case INSTANCEOF -> instanceOf(frame, instruction, threadStack);
                    case CHECKCAST -> checkCast(frame, instruction, threadStack);
                    default -> throw StackFrame.Exception.create("Unrecognized operation %s".formatted(opCode), frame);
                }
            }
            return false;
        } catch (ThreadStack.Exception e) {
            throw e;
        } catch (Exception e) {
            throw ThreadStack.Exception.create(threadStack, e);
        }
    }

    private boolean hasTargetSuperKlass(KlassDesc targetKlass, KlassDesc refKlass) {
        KlassDesc superKlassDesc = refKlass.superKlass();
        while (superKlassDesc != null) {
            if (superKlassDesc.id() == targetKlass.id()) {
                return true;
            }
            superKlassDesc = superKlassDesc.superKlass();
        }

        return false;
    }

    private boolean hasTargetInterface(KlassDesc target, KlassDesc[] interfaces) {
        if (interfaces.length == 0) {
            return false;
        }

        ArrayDeque<KlassDesc> queue = new ArrayDeque<>();
        Collections.addAll(queue, interfaces);

        while (!queue.isEmpty()) {
            KlassDesc klassDesc = queue.pollFirst();
            if (klassDesc.id() == target.id()) {
                return true;
            }

            Collections.addAll(queue, klassDesc.interfaces());
        }

        return false;
    }

    private boolean hasTargetInterface(KlassDesc target, KlassDesc ref) {
        KlassDesc[] interfaces = ref.interfaces();
        if (hasTargetInterface(target, interfaces)) {
            return true;
        }

        KlassDesc superKlassDesc = ref.superKlass();
        if (superKlassDesc != null) {
            return hasTargetInterface(target, superKlassDesc);
        }

        return false;
    }

    private static void ldc(StackFrame frame, Instruction instruction) {
        frame.inc();
        short cpRef = instruction.firstOperand();

        Constant constant = frame.getPool().get(cpRef);

        if (constant instanceof IntoValue i) {
            frame.push(i.into());
        } else {
            throw StackFrame.Exception.create("Cannot operate LDC with CP value that is not IntoValue [%s]".formatted(constant), frame);
        }
    }

    private static void iconst(StackFrame frame, int number) {
        frame.push(new Value.Int(number));
    }

    private void arrayLength(StackFrame frame) {
        frame.inc();

        long ref = frame.pop().asRef();

        int size = memoryManager.arrayLength(ref);

        frame.push(new Value.Int(size));
    }

    private void loadArrayElement(StackFrame frame, Jtype.Primitive primitive) {
        frame.inc();

        var index = frame.pop().asInt();
        var arrRef = frame.pop().asRef();

        Value res = memoryManager.getArrayElement(arrRef, index, primitive);

        frame.push(res);
    }


    private void storeArrayElement(StackFrame frame, Jtype.Primitive type) {
        frame.inc();

        var value = frame.pop();
        var index = frame.pop().asInt();
        var arrRef = frame.pop().asRef();

        memoryManager.setArrayElement(arrRef, index, type, value.asNumber());
    }

    private boolean icmp(CmpType cmpType, StackFrame frame, Instruction instruction) {
        frame.inc();
        var jumpIp = instruction.firstOperand();

        var b = frame.pop().asInt();
        var a = frame.pop().asInt();

        boolean condRes = (cmpType == CmpType.EQ && a == b)
                || (cmpType == CmpType.NE && a != b)
                || (cmpType == CmpType.LT && a < b)
                || (cmpType == CmpType.GE && a >= b)
                || (cmpType == CmpType.GT && a > b)
                || (cmpType == CmpType.LE && a <= b);

        if (condRes) {
            frame.goTo(jumpIp);
            return true;
        }
        return false;
    }

    private boolean ifNullable(StackFrame frame, Instruction instruction, boolean mustBeNull) {
        frame.inc();
        var jumpIp = instruction.firstOperand();

        var value = frame.pop();

        if (value instanceof Value.Ref ref) {
            if (mustBeNull == ref.isNull()) {
                frame.goTo(jumpIp);
                return true;
            } else {
                return false;
            }
        }
        throw new IllegalStateException("Value %s is not a reference".formatted(value));
    }

    enum NullDir {

    }

    private void putStatic(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        frame.inc();

        ClassAndFieldDesc cnf = obtainField(frame, instruction, threadStack);
        KlassDesc klass = cnf.klass();
        FieldDesc field = cnf.field();
        long address = klass.staticAddress() + field.offset();
        Jtype.Primitive type = field.type().primitive();

        var value = (Number) frame.pop().getVal();

        memoryManager.putWithType(address, type, value);
    }

    private void checkCast(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        frame.inc();

        Value peekedValue = frame.peek();
        if (peekedValue instanceof Value.Ref ref && !ref.isNull()) {
            short operand = instruction.firstOperand();
            ConstantPool cp = frame.getPool();
            String targetClassName = cp.resolveClassRef(operand);
            KlassDesc targetKlassDesc = getClass(targetClassName, threadStack);

            long refAddress = peekedValue.asRef();
            int refClassId = memoryManager.getClassId(refAddress);
            KlassDesc refKlassDesc = classLoader.load(refClassId);

            boolean castable = false;
            if (targetKlassDesc.id() == refKlassDesc.id()) {
                castable = true;
            } else {
                if (targetKlassDesc.isInterface() && hasTargetInterface(targetKlassDesc, refKlassDesc)) {
                    castable = true;
                } else {
                    if (hasTargetSuperKlass(targetKlassDesc, refKlassDesc)) {
                        castable = true;
                    }
                }
            }

            if (!castable) {
                throw new ClassCastException("Class %s cannot cast to class %s.".formatted(refKlassDesc.name(), targetKlassDesc.name()));
            }
        }
    }

    private void instanceOf(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        frame.inc();
        Value val = frame.pop();
        if (val instanceof Value.Ref ref) {
            if (ref.isNull()) {
                frame.push(new Value.Bool(false));
            } else {
                long objAddress = val.asRef();
                int classId = memoryManager.getClassId(objAddress);
                KlassDesc refKlass = classLoader.load(classId);

                short operand = instruction.firstOperand();
                ConstantPool cp = frame.getPool();
                String className = cp.resolveClassRef(operand);
                KlassDesc targetClass = getClass(className, threadStack);

                if (targetClass.id() == refKlass.id()) {
                    frame.push(new Value.Bool(true));
                } else {
                    if (targetClass.isInterface() && hasTargetInterface(targetClass, refKlass)) {
                        frame.push(new Value.Bool(true));
                    } else {
                        if (hasTargetSuperKlass(targetClass, refKlass)) {
                            frame.push(new Value.Bool(true));
                        } else {
                            frame.push(new Value.Bool(false));
                        }
                    }
                }
            }
        }
    }

    private void getStatic(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        frame.inc();

        ClassAndFieldDesc cnf = obtainField(frame, instruction, threadStack);

        var address = cnf.klass().staticAddress() + cnf.field().offset();
        var type = cnf.field().type().primitive();

        Value value = memoryManager.getWithType(address, type);

        frame.push(value);
    }

    private void putField(ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        frame.inc();

        ClassAndFieldDesc classAndField = obtainField(frame, instruction, threadStack);
        FieldDesc targetField = classAndField.field();

        Object value = frame.pop().getVal();
        long objRef = frame.pop().asRef();

        memoryManager.putWithType(
                objRef + targetField.offset(),
                targetField.type().primitive(),
                (Number) value
        );
    }

    private void getField(ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        frame.inc();

        ClassAndFieldDesc classAndField = obtainField(frame, instruction, threadStack);
        FieldDesc targetField = classAndField.field();

        long objRef = frame.pop().asRef();

        Value value = memoryManager.getWithType(
                objRef + targetField.offset(),
                targetField.type().primitive()
        );

        frame.push(value);
    }


    private record ClassAndFieldDesc(@NonNull KlassDesc klass, @NonNull FieldDesc field) {
    }

    public void invoke(InvokeType invokeType, ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        var methodIdx = instruction.firstOperand();

        ClassAndMethodDesc cnm = obtainMethodDesc(methodIdx, invokeType, threadStack, frame.getPool());
        MethodDesc method = cnm.method();
        KlassDesc klass = cnm.klass();

        if (method.fNative()) {
            System.out.printf("Call native method %s%n", method.name());
            frame.inc();

        } else {
            StackFrame stackFrame = frame.fork(klass, method);

            threadStack.push(stackFrame);

            frame.inc();
        }

    }

    public void newObject(ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        frame.inc();

        ConstantPool cp = frame.getPool();

        var classConstId = instruction.firstOperand();

        String className = cp.resolveClassRef(classConstId);

        KlassDesc newInstanceClassInfo = getClass(className, threadStack);

        Collection<FieldDesc> values = newInstanceClassInfo.fieldGroup().fields().values().stream().filter(a -> !a.accStatic()).toList();

        long address = memoryManager.allocateObject(
                newInstanceClassInfo.id(),
                values,
                newInstanceClassInfo.fieldGroup().instanceSize());

        frame.push(Value.Ref.from(address));
    }

    private void newArray(StackFrame frame, Instruction instruction) {
        frame.inc();

        byte typeCode = (byte) instruction.firstOperand();

        var s = frame.pop();

        int size = s instanceof Value.Byte b
                ? b.value()
                : s.asInt();


        if (size < 0) {
            throw new NegativeArraySizeException();
        }

        ArrayType arrayType = ArrayType.byCode(typeCode);

        long arrayRef = memoryManager.allocateArray(arrayType.getPrimitive(), size);

        frame.push(Value.Ref.from(arrayRef));

    }

    private void newReferenceArray(ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        frame.inc();

        var s = frame.pop();

        int size = s instanceof Value.Byte b ? b.value() : s.asInt();

        if (size < 0) {
            throw new NegativeArraySizeException();
        }

        short operand = instruction.firstOperand();
        var cp = frame.getPool();
        var classRef = cp.resolveClassRef(operand);
        KlassDesc klassDesc = getClass(classRef, threadStack);
        Collection<FieldDesc> fieldDescValues = klassDesc.fieldGroup().fields().values().stream().filter(a -> !a.accStatic()).toList();

        long arrayRef = memoryManager.allocateArray(
                klassDesc.id(),
                fieldDescValues,
                klassDesc.fieldGroup().instanceSize(),
                size
        );

        frame.push(Value.Ref.from(arrayRef));
    }

    private void aThrow(StackFrame frame) {
        frame.inc();

        ExceptionTable[] exceptionTables = frame.getMethod().exceptionTable();
    }

    private KlassDesc getClass(String className, ThreadStack threadStack) {

        KlassDesc loaded = classLoader.load(className);

        if (!loaded.isInit()) {
            loaded.setInit(true);
            initClass(threadStack, loaded);
        }

        return loaded;
    }

    private void initClass(ThreadStack threadStack, KlassDesc klass) {
        MethodDesc clinit = klass.getClinit();

        if (clinit != null) {
            System.out.printf("  Init    %s%n", klass.name());

            var staticFields = new ArrayList<>(klass.fieldGroup().fields().values());

            long classObjectAddress = memoryManager.allocateObject(klass.id(), staticFields, klass.fieldGroup().staticSize());
            klass.setStaticAddress(classObjectAddress);

            StackFrame clinitFrame = StackFrame.create(klass, clinit);

            threadStack.push(clinitFrame);

            executeMethod(threadStack);
        }
    }

    record ClassAndMethodDesc(@NonNull KlassDesc klass, @NonNull MethodDesc method) {
    }

    private ClassAndMethodDesc obtainMethodDesc(short methodIdx, InvokeType invokeType, ThreadStack threadStack, ConstantPool cp) {

        var methodRefInfo = cp.resolveMethorRefInfo(methodIdx);

        ClassAndMethodDesc cnm = methodDescriptors.get(methodRefInfo);

        if (cnm != null) {
            return cnm;
        }

        var className = cp.resolveClassRef(methodRefInfo.classIndex());

        var nameAndTypeInfo = cp.resolveNameAndTypeInfo(methodRefInfo.nameAndTypeIndex());

        var methodName = cp.resolveUtf8Ref(nameAndTypeInfo.nameIndex());

        var methodDescriptor = cp.resolveUtf8Ref(nameAndTypeInfo.descriptorIndex());

        // PROCESSING REFERENCES OF CLASS RECEIVING OBJECTS AND COMPARING THE CLASS WITH A VALID ONE

        KlassDesc targetClass = getClass(className, threadStack);

        MethodDesc method = targetClass.findMethod(methodName, methodDescriptor, invokeType);

        cnm = new ClassAndMethodDesc(targetClass, method);

//        if (cnm.method.fStatic() || cnm.method.)

        methodDescriptors.put(methodRefInfo, cnm);

        return cnm;
    }

    private ClassAndFieldDesc obtainField(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        var fieldId = instruction.firstOperand();

        ConstantPool cp = frame.getPool();

        Constant.FieldInfo fieldInfo = cp.resolveFieldInfo(fieldId);

        ClassAndFieldDesc cnf = fieldDescriptors.get(fieldInfo);

        if (cnf != null) {
            return cnf;
        }

        String className = cp.resolveClassRef(fieldInfo.classIndex());

        Constant.NameAndTypeInfo nameAndTypeInfo = cp.resolveNameAndTypeInfo(fieldInfo.nameAndTypeIndex());

        String fieldName = cp.resolveUtf8Ref(nameAndTypeInfo.nameIndex());

        KlassDesc klass = getClass(className, threadStack);

        FieldDesc targetField = klass.fieldGroup().fields().get(className + "." + fieldName);

        cnf = new ClassAndFieldDesc(klass, targetField);

        fieldDescriptors.put(fieldInfo, cnf);

        return cnf;
    }

}
