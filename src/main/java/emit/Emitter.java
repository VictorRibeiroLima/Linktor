package emit;

import codeanalysis.binding.BoundProgram;
import codeanalysis.binding.conversion.BoundConversionExpression;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.call.BoundCallExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.declaration.BoundLabelDeclarationStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.expression.BoundReturnStatement;
import codeanalysis.binding.statement.jumpto.BoundConditionalJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.symbol.BuildInFunctions;
import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.symbol.variable.VariableSymbol;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


public class Emitter {
    private MethodVisitor mv;
    private Map<VariableSymbol, Integer> variables;

    private int variableIndex;

    private final ClassWriter cw;

    private final Map<FunctionSymbol, BoundBlockStatement> functions = new HashMap<>();
    private int maxLocals;

    public Emitter(BoundProgram program) {
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        var current = program;
        while (current != null) {
            for (var fb : current.getFunctionsBodies().entrySet()) {
                var function = fb.getKey();
                var body = fb.getValue();
                functions.put(function, body);
            }
            current = current.getPrevious();
        }
    }

    public void emit() throws Exception {
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "GeneratedClass", null, "java/lang/Object", null);
        for (var function : functions.entrySet()) {
            maxLocals = 0;
            createFunction(function.getKey(), function.getValue());
        }
        cw.visitEnd();

        // Write the bytes as a class file
        byte[] bytes = cw.toByteArray();
        try (FileOutputStream stream = new FileOutputStream("GeneratedClass.class")) {
            stream.write(bytes);
        }
    }

    private void createFunction(FunctionSymbol function, BoundBlockStatement block) {

        var descriptor = "";
        if (function.getName().equals("main")) {
            variables = new HashMap<>();
            variables.put(new VariableSymbol("args", TypeSymbol.ANY, false), 0);
            descriptor = "([Ljava/lang/String;)V";
        } else {
            variables = setParams(function);
            descriptor = descriptorBuilder(function);
        }
        variableIndex = variables.size();
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, function.getName(), descriptor, null, null);
        emitStatement(block);
        mv.visitMaxs(64, maxLocals);
        mv.visitEnd();
    }

    private void emitStatement(BoundBlockStatement block) {
        var hasReturned = false;
        mv.visitCode();
        for (var statement : block.getStatements()) {
            switch (statement.getKind()) {
                case EXPRESSION_STATEMENT -> emitExpressionStatement((BoundExpressionStatement) statement);
                case VARIABLE_DECLARATION_STATEMENT ->
                        emitVariableDeclarationStatement((BoundVariableDeclarationStatement) statement);
                case JUMP_TO_STATEMENT -> emitJumpToStatement((BoundJumpToStatement) statement);
                case CONDITIONAL_JUMP_TO_STATEMENT ->
                        emitConditionalJumpToStatement((BoundConditionalJumpToStatement) statement);
                case RETURN_STATEMENT -> {
                    hasReturned = true;
                    emitReturnStatement((BoundReturnStatement) statement);
                }
                case LABEL_DECLARATION_STATEMENT -> emitLabel((BoundLabelDeclarationStatement) statement);
                default -> throw new RuntimeException("Unexpected node " + statement.getKind());
            }
        }
        if (!hasReturned)
            mv.visitInsn(Opcodes.RETURN);
    }

    private void emitExpressionStatement(BoundExpressionStatement statement) {
        emitExpression(statement.getExpression());
        if (statement.getExpression().getType() != TypeSymbol.VOID)
            mv.visitInsn(Opcodes.POP);
    }


    private void emitReturnStatement(BoundReturnStatement statement) {
        if (statement.getExpression() != null)
            emitExpression(statement.getExpression());
        mv.visitInsn(typeReturn(statement.getExpression().getType()));
    }

    private void emitConditionalJumpToStatement(BoundConditionalJumpToStatement statement) {

    }

    private void emitJumpToStatement(BoundJumpToStatement statement) {
    }

    private void emitLabel(BoundLabelDeclarationStatement statement) {

    }

    private void emitVariableDeclarationStatement(BoundVariableDeclarationStatement statement) {
        emitExpression(statement.getInitializer());
        variables.put(statement.getVariable(), variableIndex);
        mv.visitVarInsn(typeStore(statement.getVariable().getType()), variableIndex++);
        maxLocals++;
    }

    private void emitExpression(BoundExpression node) {
        switch (node.getKind()) {
            case LITERAL_EXPRESSION -> emitLiteralExpression((BoundLiteralExpression) node);
            case VARIABLE_EXPRESSION -> emitVariableExpression((BoundVariableExpression) node);
            case ASSIGNMENT_EXPRESSION -> emitAssignmentExpression((BoundAssignmentExpression) node);
            case UNARY_EXPRESSION -> emitUnaryExpression((BoundUnaryExpression) node);
            case BINARY_EXPRESSION -> emitBinaryExpression((BoundBinaryExpression) node);
            case CALL_EXPRESSION -> emitCallExpression((BoundCallExpression) node);
            case CONVERSION_EXPRESSION -> emitConversionExpression((BoundConversionExpression) node);
            default -> throw new RuntimeException("Unexpected node " + node.getKind());
        }
    }

    private void emitConversionExpression(BoundConversionExpression node) {
        emitExpression(node.getExpression());
        var convertingFrom = typesDescriptor(node.getExpression().getType());
        var type = node.getType();
        //TODO: see this later,it's ok for primitive values but with more complex structs it will bug
        if (type == TypeSymbol.STRING || type == TypeSymbol.ANY) {
            if (node.getExpression().getType() != TypeSymbol.STRING && node.getExpression().getType() != TypeSymbol.ANY) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String",
                        "valueOf", "(" + convertingFrom + ")Ljava/lang/String;", false);
            }
        } else if (type == TypeSymbol.BOOLEAN) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean",
                    "valueOf", "(" + convertingFrom + ")Ljava/lang/Boolean;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean",
                    "booleanValue", "()Z", false);
        } else if (type == TypeSymbol.INTEGER) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer",
                    "valueOf", "(" + convertingFrom + ")Ljava/lang/Integer;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer",
                    "intValue", "()I", false);
        } else
            throw new RuntimeException("Unexpected type " + type);
    }

    private void emitCallExpression(BoundCallExpression node) {
        if (node.getFunction().equals(BuildInFunctions.PRINT)) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        } else if (node.getFunction().equals(BuildInFunctions.PRINTF)) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        } else if (node.getFunction().equals(BuildInFunctions.RANDOM)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "random", "()D", false);
        }
        var descriptor = descriptorBuilder(node.getFunction());
        for (var argument : node.getArgs())
            emitExpression(argument);
        if (node.getFunction().equals(BuildInFunctions.PRINT)) {
            var type = typesDescriptor(node.getArgs().get(0).getType());
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(" + type + ")V", false);
        } else if (node.getFunction().equals(BuildInFunctions.PRINTF)) {
            var type = typesDescriptor(node.getArgs().get(0).getType());
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + type + ")V", false);
        } else if (node.getFunction().equals(BuildInFunctions.READ)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "console", "()Ljava/io/Console;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/Console", "readLine", descriptor, false);
        } else if (node.getFunction().equals(BuildInFunctions.RANDOM)) {
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IADD);
            mv.visitInsn(Opcodes.I2D);
            mv.visitInsn(Opcodes.DMUL);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "floor", "(D)D", false);
            mv.visitInsn(Opcodes.D2I);
        } else {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "GeneratedClass", node.getFunction().getName(), descriptor, false);
        }
    }

    private void emitBinaryExpression(BoundBinaryExpression b) {
        switch (b.getOperator().getKind()) {
            case CONCATENATION -> {
                final String stringPath = "java/lang/String";
                final String stringBPath = "java/lang/StringBuilder";
                mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                mv.visitInsn(Opcodes.DUP);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                emitExpression(b.getLeft());
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, stringBPath, "append",
                        "(L" + stringPath + ";)L" + stringBPath + ";", false);
                emitExpression(b.getRight());
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, stringBPath, "append",
                        "(L" + stringPath + ";)L" + stringBPath + ";", false);
                //turn builder to string and throw on stack to be processed
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, stringBPath,
                        "toString", "()L" + stringPath + ";", false);
            }
            case ADDITION -> {
                emitExpression(b.getLeft());
                emitExpression(b.getRight());
                mv.visitInsn(Opcodes.IADD);
            }
            case SUBTRACTION -> {
                emitExpression(b.getLeft());
                emitExpression(b.getRight());
                mv.visitInsn(Opcodes.ISUB);
            }
            case DIVISION -> {
                emitExpression(b.getLeft());
                emitExpression(b.getRight());
                mv.visitInsn(Opcodes.IDIV);
            }
            case MULTIPLICATION -> {
                emitExpression(b.getLeft());
                emitExpression(b.getRight());
                mv.visitInsn(Opcodes.IMUL);
            }
            case MOD -> {
                emitExpression(b.getLeft());
                emitExpression(b.getRight());
                mv.visitInsn(Opcodes.IREM);
            }
            default -> throw new RuntimeException("Unexpected binary operation " + b.getOperator());
        }

    }

    private void emitUnaryExpression(BoundUnaryExpression node) {
    }

    private void emitAssignmentExpression(BoundAssignmentExpression node) {
        emitExpression(node.getBoundExpression());
        var index = variables.get(node.getVariable());
        mv.visitInsn(Opcodes.DUP);
        mv.visitVarInsn(typeStore(node.getType()), index);
    }

    private void emitVariableExpression(BoundVariableExpression node) {
        mv.visitVarInsn(typeLoad(node.getVariable().getType()), variables.get(node.getVariable()));
    }

    private void emitLiteralExpression(BoundLiteralExpression node) {
        mv.visitLdcInsn(node.getValue());
    }


    private String descriptorBuilder(FunctionSymbol function) {
        var builder = new StringBuilder();
        builder.append("(");
        for (var param : function.getParameters()) {
            builder.append(typesDescriptor(param.getType()));
        }
        builder.append(")");
        if (function.getType() != null) {
            builder.append(typesDescriptor(function.getType()));
        } else {
            builder.append("V");
        }
        return builder.toString();
    }

    private Map<VariableSymbol, Integer> setParams(FunctionSymbol function) {
        int variableIndex = 0;
        Map<VariableSymbol, Integer> variables = new HashMap<>();
        for (var param : function.getParameters()) {
            variables.put(param, variableIndex);
            variableIndex++;
        }
        return variables;
    }

    private String typesDescriptor(TypeSymbol type) {
        return switch (type.getName()) {
            case "void" -> "V";
            case "boolean" -> "Z";
            case "int" -> "I";
            case "string", "any" ->
                    "Ljava/lang/String;"; //TODO: see this later,it's ok for primitive values but with more complex structs it will bug
            default -> throw new RuntimeException("Unexpected type");
        };
    }

    private int typeReturn(TypeSymbol type) {
        return switch (type.getName()) {
            case "void" -> Opcodes.RETURN;
            case "boolean", "int" -> Opcodes.IRETURN;
            case "string", "any" -> Opcodes.ARETURN;
            default -> throw new RuntimeException("Unexpected type");
        };
    }

    private int typeLoad(TypeSymbol type) {
        return switch (type.getName()) {
            case "boolean", "int" -> Opcodes.ILOAD;
            case "string", "any" -> Opcodes.ALOAD;
            default -> throw new RuntimeException("Unexpected type");
        };
    }

    private int typeStore(TypeSymbol type) {
        return switch (type.getName()) {
            case "boolean", "int" -> Opcodes.ISTORE;
            case "string", "any" -> Opcodes.ASTORE;
            default -> throw new RuntimeException("Unexpected type");
        };
    }
}
