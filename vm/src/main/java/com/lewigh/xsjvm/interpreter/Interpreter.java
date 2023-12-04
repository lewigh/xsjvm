package com.lewigh.xsjvm.interpreter;


import com.lewigh.xsjvm.classloader.AppClassLoader;
import com.lewigh.xsjvm.gc.UnsafeMemoryManager;
import com.lewigh.xsjvm.interpreter.runtime.*;
import com.lewigh.xsjvm.reader.info.attribute.Instruction;
import com.lewigh.xsjvm.reader.pool.Constant;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import com.lewigh.xsjvm.support.Logger;
import lombok.NonNull;

import java.util.*;

import static com.lewigh.xsjvm.SymbolTable.ENTRY_POINT_METHOD__DESC;
import static com.lewigh.xsjvm.SymbolTable.ENTRY_POINT_METHOD__NAME;

@SuppressWarnings({"java:S1119", "DuplicateBranchesInSwitch"})
public class Interpreter {

    private final AppClassLoader classLoader;
    private final UnsafeMemoryManager memoryManager;

    public Interpreter(AppClassLoader appClassLoader, UnsafeMemoryManager allocator) {
        this.classLoader = appClassLoader;
        this.memoryManager = allocator;
    }


    public void execute(String className) {
        ThreadStack threadStack = new ThreadStack();

        Klass mainClass = getClass(className, threadStack);

        Method mainMethod = mainClass.findMethod(ENTRY_POINT_METHOD__NAME, ENTRY_POINT_METHOD__DESC, InvokeType.STATIC);

        StackFrame mainFrame = StackFrame.create(mainClass, mainMethod);

        threadStack.push(mainFrame);

        executeThreadStack(threadStack);
    }


    private void executeThreadStack(ThreadStack threadStack) {
        for (; ; ) {
            if (executeMethod(threadStack)) break;
        }
    }

    private boolean executeMethod(ThreadStack threadStack) {
        if (threadStack.isEmpty()) {
            Logger.debug("End of thread%n");
            return true;
        }

        StackFrame frame = threadStack.top();
        Method method = frame.getMethod();

        Logger.invoke(frame);

        loop:
        for (int i = frame.ip; i < method.instructions().length; i++) {

            var instruction = method.instructions()[i];
            var opCode = instruction.opCode();

            switch (opCode) {
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
                case FCONST_2 -> {
                    frame.inc();
                    frame.push(new Value.Float(2.0f));
                }
//                case LDC -> {
//                    frame.inc();
//
//                    byte[] arguments = instruction.arguments();
//
//                    frame.push(frame.getConstantPool().get(arguments[0]));
//                }
                case ISTORE -> {
                    frame.inc();

                    byte[] arguments = instruction.arguments();

                    frame.popAndStoreTo(arguments[0]);
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
                case FSTORE -> {
                    byte[] arguments = instruction.arguments();

                    frame.popAndStoreTo(arguments[0]);
                }
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
                    if (frame.pop() instanceof Value.Int a && frame.pop() instanceof Value.Int b) {
                        frame.push(new Value.Int(a.value() + b.value()));
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                case IRETURN -> {
                    StackFrame cur = threadStack.pop();
                    Value retVal = cur.pop();
                    threadStack.top().push(retVal);
                    Logger.retval(frame, retVal);
                    break loop;
                }
                case FRETURN -> {
                    StackFrame cur = threadStack.pop();
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
                case NEW -> newObject(threadStack, frame, instruction);
                case NEWARRAY -> newArray(threadStack, frame, instruction);
                case BIPUSH -> {
                    frame.inc();
                    frame.push(new Value.Byte(instruction.firstdByteArg()));
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
                case ALOAD_0 -> {
                    frame.inc();

                    frame.push(frame.load(0));
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
                case ARRAYLENGTH -> arrayLength(frame);
                default -> throw new IllegalStateException("Unrecognized operation %s".formatted(opCode));
            }
        }
        return false;
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
        var jumpIp = instruction.firstDoubledByteArg();

        var b = frame.pop().asInt();
        var a = frame.pop().asInt();

        boolean condRes = (cmpType == CmpType.EQ && a == b)
                || (cmpType == CmpType.NE && a != b)
                || (cmpType == CmpType.LT && a < b)
                || (cmpType == CmpType.GE && a >= b)
                || (cmpType == CmpType.GT && a > b)
                || (cmpType == CmpType.LE && a <= b);

        if (condRes) {
            frame.goTo((short) jumpIp);
            return true;
        }
        return false;
    }

    private void putStatic(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        frame.inc();

        ClassAndField cnf = resolveField(frame, instruction, threadStack);
        Klass klass = cnf.klass();
        Field targetField = cnf.targetField();

        memoryManager.putWithType(
                klass.state().getStaticAddress() + targetField.offset(),
                targetField.type().primitive(),
                (Number) frame.pop().getVal()
        );
    }

    private void getStatic(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        frame.inc();

        ClassAndField cnf = resolveField(frame, instruction, threadStack);

        Value value = memoryManager.getWithType(
                cnf.klass().state().getStaticAddress() + cnf.targetField().offset(),
                cnf.targetField().type().primitive()
        );

        frame.push(value);
    }

    private void putField(ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        frame.inc();

        ClassAndField classAndField = resolveField(frame, instruction, threadStack);
        Field targetField = classAndField.targetField();

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

        ClassAndField classAndField = resolveField(frame, instruction, threadStack);
        Field targetField = classAndField.targetField();

        long objRef = frame.pop().asRef();

        Value value = memoryManager.getWithType(
                objRef + targetField.offset(),
                targetField.type().primitive()
        );

        frame.push(value);
    }

    private ClassAndField resolveField(StackFrame frame, Instruction instruction, ThreadStack threadStack) {
        var fieldId = instruction.firstDoubledByteArg();

        ConstantPool cp = frame.getPoll();

        Constant.FieldInfo fieldInfo = cp.resolveFielderInfo((short) fieldId);

        String className = cp.resolveClassRef(fieldInfo.classIndex());

        Constant.NameAndTypeInfo nameAndTypeInfo = cp.resolveNameAndTypeInfo(fieldInfo.nameAndTypeIndex());

        String fieldName = cp.resolveUtf8Ref(nameAndTypeInfo.nameIndex());

        Klass klass = getClass(className, threadStack);

        Field targetField = klass.fieldGroup().fields().get(className + "." + fieldName);

        return new ClassAndField(klass, targetField);
    }

    private record ClassAndField(@NonNull Klass klass, @NonNull Field targetField) {
    }

    public void invoke(InvokeType invokeType, ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        ConstantPool cp = frame.getPoll();

        var methodIdx = instruction.firstDoubledByteArg();

        Constant.MethodRefInfo methodRefInfo = cp.resolveMethorRefInfo((short) methodIdx);

        String className = cp.resolveClassRef(methodRefInfo.classIndex());

        Constant.NameAndTypeInfo nameAndTypeInfo = cp.resolveNameAndTypeInfo(methodRefInfo.nameAndTypeIndex());

        String methodName = cp.resolveUtf8Ref(nameAndTypeInfo.nameIndex());
        String methodDescriptor = cp.resolveUtf8Ref(nameAndTypeInfo.descriptorIndex());

        // PROCESSING REFERENCES OF CLASS RECEIVING OBJECTS AND COMPARING THE CLASS WITH A VALID ONE

        Klass targetClass = getClass(className, threadStack);
        Method method = targetClass.findMethod(methodName, methodDescriptor, invokeType);

        if (method.fNative()) {
            System.out.printf("Call native method %s%n", method.name());

        } else {
            StackFrame stackFrame = frame.fork(targetClass, method);

            threadStack.push(stackFrame);

            frame.inc();
        }

    }

    public void newObject(ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        frame.inc();

        ConstantPool cp = frame.getPoll();

        var classConstId = instruction.firstDoubledByteArg();

        String className = cp.resolveClassRef((short) classConstId);

        Klass newInstanceClassInfo = getClass(className, threadStack);

        Collection<Field> values = newInstanceClassInfo.fieldGroup().fields().values().stream().filter(a -> !a.accStatic()).toList();

        long address = memoryManager.allocateObject(
                newInstanceClassInfo.id(),
                values,
                newInstanceClassInfo.fieldGroup().instanceSize());

        frame.push(Value.Reference.from(address));
    }

    private void newArray(ThreadStack threadStack, StackFrame frame, Instruction instruction) {
        frame.inc();

        byte typeCode = instruction.arguments()[0];

        var s = frame.pop();

        int size = s instanceof Value.Byte b
                ? b.value()
                : s.asInt();


        if (size < 0) {
            throw new NegativeArraySizeException();
        }

        ArrayType arrayType = ArrayType.byCode(typeCode);

        long arrayRef = memoryManager.allocateArray(arrayType.getPrimitive(), size);

        frame.push(Value.Reference.from(arrayRef));

    }

    private Klass getClass(String className, ThreadStack threadStack) {

        Klass loaded = classLoader.load(className);

        Klass.State state = loaded.state();

        if (!state.isInit()) {
            state.setInit(true);
            initClass(threadStack, loaded);
        }

        return loaded;
    }

    private void initClass(ThreadStack threadStack, Klass klass) {
        Method clinit = klass.getClinit();

        if (clinit != null) {

            var staticFields = new ArrayList<>(klass.fieldGroup().fields().values());

            long classObjectAddress = memoryManager.allocateObject(klass.id(), staticFields, klass.fieldGroup().staticSize());
            klass.state().setStaticAddress(classObjectAddress);

            StackFrame clinitFrame = StackFrame.create(klass, clinit);

            threadStack.push(clinitFrame);

            executeMethod(threadStack);
        }
    }

}
