package cop5556fa18;

import cop5556fa18.PLPAST.AssignmentStatement;
import cop5556fa18.PLPAST.Block;
import cop5556fa18.PLPAST.Declaration;
import cop5556fa18.PLPAST.ExpressionBinary;
import cop5556fa18.PLPAST.ExpressionBooleanLiteral;
import cop5556fa18.PLPAST.ExpressionCharLiteral;
import cop5556fa18.PLPAST.ExpressionConditional;
import cop5556fa18.PLPAST.ExpressionFloatLiteral;
import cop5556fa18.PLPAST.ExpressionIdentifier;
import cop5556fa18.PLPAST.ExpressionIntegerLiteral;
import cop5556fa18.PLPAST.ExpressionStringLiteral;
import cop5556fa18.PLPAST.ExpressionUnary;
import cop5556fa18.PLPAST.FunctionWithArg;
import cop5556fa18.PLPAST.IfStatement;
import cop5556fa18.PLPAST.LHS;
import cop5556fa18.PLPAST.PLPASTNode;
import cop5556fa18.PLPAST.PLPASTVisitor;
import cop5556fa18.PLPAST.PrintStatement;
import cop5556fa18.PLPAST.Program;
import cop5556fa18.PLPAST.SleepStatement;
import cop5556fa18.PLPAST.VariableDeclaration;
import cop5556fa18.PLPAST.VariableListDeclaration;
import cop5556fa18.PLPAST.WhileStatement;
import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPTypes.Type;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.*;

public class PLPCodeGen implements PLPASTVisitor, Opcodes {

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	boolean intoLHS = false;

	int current_slot = 0;
	List<Declaration> decList = new ArrayList<>();

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	public PLPCodeGen(String sourceFileName, boolean dEVEL, boolean gRADE) {
		super();
		this.sourceFileName = sourceFileName;
		DEVEL = dEVEL;
		GRADE = gRADE;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		for (PLPASTNode node : block.declarationsAndStatements) {
			node.visit(this, null);
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0);
		// If the call to mv.visitMaxs(1, 1) crashes, it is sometimes helpful
		// to temporarily run it without COMPUTE_FRAMES. You probably won't
		// get a completely correct classfile, but you will be able to see the
		// code that was generated.

		className = program.name;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);

		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();

		// add label before first instruction
		Label mainStart = new Label();

		mv.visitLabel(mainStart);

		PLPCodeGenUtils.genLog(DEVEL, mv, "entering main");

		program.block.visit(this, arg);

		// generates code to add string to log
		PLPCodeGenUtils.genLog(DEVEL, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		// Label mainEnd = new Label();
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor, asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(15, 15);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitVariableDeclaration(VariableDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		declaration.slot_number = current_slot++;
		decList.add(declaration);
		int num = declaration.slot_number;

		if (declaration.getType().equals(Type.INTEGER)) {
			declaration.SetJVMType("I");
			System.out.println("declaration I");
			// mv.visitInsn(num);
			if (declaration.expression != null) {
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ISTORE, declaration.slot_number);
			}

		} else if (declaration.getType().equals(Type.FLOAT)) {
			declaration.SetJVMType("F");
			//
			System.out.println("declaration F");

			if (declaration.expression != null) {
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(FSTORE, declaration.slot_number);
			}

		} else if (declaration.getType().equals(Type.BOOLEAN)) {
			declaration.SetJVMType("Z");
			System.out.println("declaration Z");
			if (declaration.expression != null) {
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ISTORE, declaration.slot_number);
			}

		} else if (declaration.getType().equals(Type.CHAR)) {
			declaration.SetJVMType("C");
			System.out.println("declaration C");
			if (declaration.expression != null) {
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ISTORE, declaration.slot_number);
			}

		} else if (declaration.getType().equals(Type.STRING)) {
			declaration.SetJVMType("LJava/lang/String");
			System.out.println("declaration string");
			if (declaration.expression != null) {
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ASTORE, declaration.slot_number);
			}

		}

		return null;
	}

	@Override
	public Object visitVariableListDeclaration(VariableListDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		declaration.slot_number = current_slot++;
		int num = declaration.slot_number;
		decList.add(declaration);

		if (declaration.getType().equals(Type.INTEGER)) {
			declaration.SetJVMType("I");
		} else if (declaration.getType().equals(Type.FLOAT)) {
			declaration.SetJVMType("F");
		} else if (declaration.getType().equals(Type.BOOLEAN)) {
			declaration.SetJVMType("Z");
		} else if (declaration.getType().equals(Type.CHAR)) {
			declaration.SetJVMType("C");
		} else if (declaration.getType().equals(Type.STRING)) {
			declaration.SetJVMType("LJava/lang/String");
		}

		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Label setTrue = new Label();
		Label endTrue = new Label();

		Type e1 = (Type) expressionBinary.leftExpression.visit(this, arg);
		Type e2 = (Type) expressionBinary.rightExpression.visit(this, arg);

		if (e1 == Type.INTEGER && e2 == Type.INTEGER) {
			switch (expressionBinary.op) {

			case OP_PLUS: {
				mv.visitInsn(IADD);
				return Type.INTEGER;
			}
			case OP_MINUS: {
				mv.visitInsn(ISUB);
				return Type.INTEGER;
			}
			case OP_TIMES: {
				mv.visitInsn(IMUL);
				return Type.INTEGER;
			}
			case OP_DIV: {
				mv.visitInsn(IDIV);
				return Type.INTEGER;
			}
			case OP_MOD: {
				mv.visitInsn(IREM);
				return Type.INTEGER;
			}
			case OP_POWER: {
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE, 6);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD, 6);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2I);
				return Type.INTEGER;
			}
			case OP_AND: {
				mv.visitInsn(IAND);
				return Type.INTEGER;
			}
			case OP_OR: {
				mv.visitInsn(IOR);
				return Type.INTEGER;
			}
			case OP_NEQ:
				mv.visitJumpInsn(IF_ICMPNE, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_EQ:
				mv.visitJumpInsn(IF_ICMPEQ, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_LE:
				mv.visitJumpInsn(IF_ICMPLE, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_LT:
				mv.visitJumpInsn(IF_ICMPLT, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_GE:
				mv.visitJumpInsn(IF_ICMPGE, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_GT:
				mv.visitJumpInsn(IF_ICMPGT, setTrue);
				mv.visitLdcInsn(false);
				break;
			}
			mv.visitJumpInsn(GOTO, endTrue);
			mv.visitLabel(setTrue);
			mv.visitLdcInsn(true);
			mv.visitLabel(endTrue);
		} else if (e1 == Type.FLOAT && e2 == Type.FLOAT) {
			switch (expressionBinary.op) {

			case OP_PLUS: {
				mv.visitInsn(FADD);
				return Type.FLOAT;
			}
			case OP_MINUS: {
				mv.visitInsn(FSUB);
				return Type.FLOAT;
			}
			case OP_TIMES: {
				mv.visitInsn(FMUL);
				return Type.FLOAT;
			}
			case OP_DIV: {
				mv.visitInsn(FDIV);
				return Type.FLOAT;
			}
			case OP_POWER: {
				mv.visitInsn(F2D);
				mv.visitVarInsn(DSTORE, 5);
				mv.visitInsn(F2D);
				mv.visitVarInsn(DLOAD, 5);

				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				return Type.FLOAT;
			}
			case OP_NEQ:
				mv.visitJumpInsn(IF_ICMPNE, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_EQ:
				mv.visitJumpInsn(IF_ICMPEQ, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_LE:
				mv.visitJumpInsn(IF_ICMPLE, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_LT:
				mv.visitJumpInsn(IF_ICMPLT, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_GE:
				mv.visitJumpInsn(IF_ICMPGE, setTrue);
				mv.visitLdcInsn(false);
				break;
			case OP_GT:
				mv.visitJumpInsn(IF_ICMPGT, setTrue);
				mv.visitLdcInsn(false);
				break;
			}
			mv.visitJumpInsn(GOTO, endTrue);
			mv.visitLabel(setTrue);
			mv.visitLdcInsn(true);
			mv.visitLabel(endTrue);
		} else if (e1 == Type.INTEGER && e2 == Type.FLOAT) {
			switch (expressionBinary.op) {

			case OP_PLUS: {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);
				return Type.FLOAT;
			}
			case OP_MINUS: {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FSUB);

				return Type.FLOAT;
			}
			case OP_TIMES: {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
				return Type.FLOAT;
			}
			case OP_DIV: {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FDIV);

				return Type.FLOAT;
			}
			case OP_POWER: { // TODO
				mv.visitInsn(F2D);
				mv.visitVarInsn(DSTORE, 6);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD, 6);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				return Type.FLOAT;
			}
			}
		} else if (e1 == Type.FLOAT && e2 == Type.INTEGER) {
			switch (expressionBinary.op) {

			case OP_PLUS: {
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);

				return Type.FLOAT;
			}
			case OP_MINUS: {
				mv.visitInsn(I2F);
				mv.visitInsn(FSUB);

				return Type.FLOAT;
			}
			case OP_TIMES: {
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
				return Type.FLOAT;
			}
			case OP_DIV: {
				mv.visitInsn(I2F);
				mv.visitInsn(FDIV);
				return Type.FLOAT;
			}
			case OP_POWER: { // TODO
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE, 6);
				mv.visitInsn(F2D);
				mv.visitVarInsn(DLOAD, 6);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);

				return Type.FLOAT;

			}
			}
		} else if (e1 == Type.STRING && e2 == Type.STRING) {
			switch (expressionBinary.op) {

			case OP_PLUS: {
				ExpressionStringLiteral s1 = (ExpressionStringLiteral) expressionBinary.leftExpression;
				ExpressionStringLiteral s2 = (ExpressionStringLiteral) expressionBinary.rightExpression;
				mv.visitLdcInsn(s1.text + s2.text);

				return Type.STRING;
			}
			}
		} else if (e1 == Type.BOOLEAN && e2 == Type.BOOLEAN) {
			switch (expressionBinary.op) {
			case OP_AND: {
				mv.visitInsn(IAND);
				return Type.BOOLEAN;
			}
			case OP_OR: {
				mv.visitInsn(IOR);
				return Type.BOOLEAN;
			}
			case OP_NEQ:
				mv.visitJumpInsn(IF_ICMPNE, setTrue);
				mv.visitLdcInsn(false);
				return Type.BOOLEAN;
			case OP_EQ:
				mv.visitJumpInsn(IF_ICMPEQ, setTrue);
				mv.visitLdcInsn(false);
				return Type.BOOLEAN;
			case OP_LE:
				mv.visitJumpInsn(IF_ICMPLE, setTrue);
				mv.visitLdcInsn(false);
				return Type.BOOLEAN;
			case OP_LT:
				mv.visitJumpInsn(IF_ICMPLT, setTrue);
				mv.visitLdcInsn(false);
				return Type.BOOLEAN;
			case OP_GE:
				mv.visitJumpInsn(IF_ICMPGE, setTrue);
				mv.visitLdcInsn(false);
				return Type.BOOLEAN;
			case OP_GT:
				mv.visitJumpInsn(IF_ICMPGT, setTrue);
				mv.visitLdcInsn(false);
				return Type.BOOLEAN;
			}
			mv.visitJumpInsn(GOTO, endTrue);
			mv.visitLabel(setTrue);
			mv.visitLdcInsn(true);
			mv.visitLabel(endTrue);
		}

		return null;
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type t = null;
		expressionConditional.condition.visit(this, arg);
		Label L0 = new Label();
		Label L1 = new Label();
		mv.visitJumpInsn(IFNE, L0);
		t = (Type) expressionConditional.falseExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, L1);
		mv.visitLabel(L0);
		t = (Type) expressionConditional.trueExpression.visit(this, arg);
		mv.visitLabel(L1);

		return t;
	}

	@Override
	public Object visitFunctionWithArg(FunctionWithArg FunctionWithArg, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Label start = new Label();
		Label end = new Label();
		Type t = (Type) FunctionWithArg.expression.visit(this, arg);
		if (t.equals(Type.INTEGER)) {
			if (FunctionWithArg.functionName.equals(Kind.KW_abs)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
				return Type.INTEGER;
			} else if (FunctionWithArg.functionName.equals(Kind.KW_float)) {
				mv.visitInsn(I2F);
				return Type.FLOAT;
			}
		} else if (t.equals(Type.FLOAT)) {
			if (FunctionWithArg.functionName.equals(Kind.KW_abs)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
				return Type.FLOAT;
			} else if (FunctionWithArg.functionName.equals(Kind.KW_sin)) {
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
				mv.visitInsn(D2F);
				return Type.FLOAT;
			} else if (FunctionWithArg.functionName.equals(Kind.KW_cos)) {
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
				mv.visitInsn(D2F);
				return Type.FLOAT;
			} else if (FunctionWithArg.functionName.equals(Kind.KW_atan)) {
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan", "(D)D", false);
				mv.visitInsn(D2F);
				return Type.FLOAT;
			} else if (FunctionWithArg.functionName.equals(Kind.KW_log)) {
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "log", "(D)D", false);
				mv.visitInsn(D2F);
				return Type.FLOAT;
			} else if (FunctionWithArg.functionName.equals(Kind.KW_float)) {
				if (t.equals(Type.INTEGER)) {
					mv.visitInsn(I2F);
				}
				return Type.FLOAT;
			} else if (FunctionWithArg.functionName.equals(Kind.KW_int)) {
				if (t.equals(Type.FLOAT)) {
					mv.visitInsn(F2I);
				}

				return Type.INTEGER;
			}
		}

		return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdentifier expressionIdent, Object arg) throws Exception {
		// TODO Auto-generated method stub
		int num;
		PLPTypes type = new PLPTypes();
		Type t = null;
		System.out.println("ExpressionIdent:f");

		if (expressionIdent.decc != null) {
			num = expressionIdent.decc.get_current_slot();
			t = PLPTypes.getType(expressionIdent.decc.type);
			System.out.println("ExpressionIdent:decc");
		} else {
			num = expressionIdent.dec.get_current_slot();
			t = PLPTypes.getType(expressionIdent.dec.type);
			System.out.println("ExpressionIdent:dec");
		}

		if (t == Type.INTEGER || t == Type.BOOLEAN || t == Type.CHAR) {
			mv.visitIntInsn(ILOAD, num);
			System.out.println("ExpressionIdent:ILOAD");
			// mv.visitVarInsn(ISTORE, expressionIdent.get_current_slot());
		} else if (t == Type.FLOAT) {
			mv.visitIntInsn(FLOAD, num);
			System.out.println("ExpressionIdent:FLOAD");
		} else if (t == Type.STRING) {
			mv.visitIntInsn(ALOAD, num);
			System.out.println("ExpressionIdent:ALOAD");
		}

		return t;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		System.out.println("IntegerLiteral");
		return Type.INTEGER;
	}

	@Override
	public Object visitExpressionStringLiteral(ExpressionStringLiteral expressionStringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionStringLiteral.text);
		System.out.println("StringLiteral");
		return Type.STRING;
	}

	@Override
	public Object visitExpressionCharLiteral(ExpressionCharLiteral expressionCharLiteral, Object arg) throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionCharLiteral.text);
		System.out.println("CharLiteral");
		return Type.CHAR;
	}

	@Override
	public Object visitExpressionBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		return Type.BOOLEAN;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub

		mv.visitLdcInsn(expressionFloatLiteral.value);
		System.out.println("visitExpressionFloatLiteral");

		return Type.FLOAT;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("assign!");

		Type t = (Type) statementAssign.expression.visit(this, arg);
		Type tlhs = (Type) statementAssign.lhs.visit(this, arg);
		//

		if (t.equals(Type.INTEGER)) {
			if (statementAssign.expression instanceof ExpressionIntegerLiteral) {
				ExpressionIntegerLiteral temp = (ExpressionIntegerLiteral) statementAssign.expression;
				mv.visitIntInsn(BIPUSH, temp.value);
			}
		} else if (t.equals(Type.FLOAT)) {
			if (statementAssign.expression instanceof ExpressionFloatLiteral) {
				ExpressionFloatLiteral temp = (ExpressionFloatLiteral) statementAssign.expression;
				mv.visitLdcInsn(new Float(Float.toString(temp.value)));
			}

		} else if (t.equals(Type.BOOLEAN)) {

			if (statementAssign.lhs.dec != null) {
				mv.visitInsn(statementAssign.lhs.dec.slot_number);
			} else {
				mv.visitInsn(statementAssign.lhs.decc.slot_number);
			}
		} else if (t.equals(Type.CHAR)) {
			if (statementAssign.expression instanceof ExpressionCharLiteral) {
				ExpressionCharLiteral temp = (ExpressionCharLiteral) statementAssign.expression;
				mv.visitIntInsn(BIPUSH, (temp.text - '0') + 96);
			}

		} else if (t.equals(Type.STRING)) {
			if (statementAssign.expression instanceof ExpressionStringLiteral) {
				ExpressionStringLiteral temp = (ExpressionStringLiteral) statementAssign.expression;
				mv.visitLdcInsn(temp.text);
			}
		}

		return null;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		PLPTypes type = new PLPTypes();
		Type t = null;
		System.out.println("LHS:f");
		intoLHS = true;

		if (lhs.decc != null) {

			t = lhs.decc.getType();
			System.out.println("LHS:decc");
		} else {
			t = lhs.dec.getType();
			System.out.println("LHS:dec");
		}

		if (t == Type.INTEGER || t == Type.BOOLEAN || t == Type.CHAR) {
			if (lhs.dec != null) {

				mv.visitVarInsn(ISTORE, lhs.dec.get_current_slot());
			} else if (lhs.decc != null) {
				mv.visitVarInsn(ISTORE, lhs.decc.get_current_slot());
			}
		} else if (t == Type.STRING) {
			if (lhs.dec != null) {
				mv.visitVarInsn(ASTORE, lhs.dec.get_current_slot());
			} else if (lhs.decc != null) {
				mv.visitVarInsn(ASTORE, lhs.decc.get_current_slot());
			}
		} else if (t == Type.FLOAT) {
			if (lhs.dec != null) {
				mv.visitVarInsn(FSTORE, lhs.dec.get_current_slot());
			} else if (lhs.decc != null) {
				mv.visitVarInsn(FSTORE, lhs.decc.get_current_slot());
			}
		}

		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub

		ifStatement.condition.visit(this, arg);
		Label afterIf = new Label();
		mv.visitJumpInsn(IFEQ, afterIf);
		ifStatement.block.visit(this, arg);
		// Label startIf = new Label();
		mv.visitLabel(afterIf); // added (if want to get back, delete this line and uncomment this other
								// commented line)


		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Label whileExpression = new Label();
		Label whileBlock = new Label();
		mv.visitJumpInsn(GOTO, whileExpression);
		mv.visitLabel(whileBlock);
		whileStatement.b.visit(this, arg);
		Label endwhile = new Label();
		mv.visitLabel(endwhile);
		mv.visitLabel(whileExpression);
		whileStatement.condition.visit(this, arg);
		Label endWhileExpr = new Label();
		mv.visitLabel(endWhileExpr);
		mv.visitJumpInsn(IFNE, whileBlock);

		return null;
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg) throws Exception {
		/**
		 * TODO refactor and complete implementation.
		 * 
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */

		Type type = (Type) printStatement.expression.visit(this, arg);

		switch (type) {
		case INTEGER: {

			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

			mv.visitInsn(Opcodes.SWAP);
			// printStatement.expression.visit(this, arg);
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		}
			break;
		case BOOLEAN: {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			// TODO implement functionality
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
			// throw new UnsupportedOperationException();
		}
			break;
		// break; commented out because currently unreachable. You will need
		// it.
		case FLOAT: {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			// TODO implement functionality
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
			// throw new UnsupportedOperationException();
		}
			break;
		// break; commented out because currently unreachable. You will need
		// it.
		case CHAR: {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			// TODO implement functionality
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(C)V", false);
			// throw new UnsupportedOperationException();
		}
			break;
		// break; commented out because currently unreachable. You will need
		// it.
		case STRING: {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			// TODO implement functionality
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			printStatement.expression.visit(this, arg);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
			// throw new UnsupportedOperationException();
		}
			break;
		}

		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		sleepStatement.time.visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Inside the unary function");

		Type type = (Type) expressionUnary.expression.visit(this, arg);
		Type returnType = Type.FLOAT;
		Label set = new Label();
		Label l = new Label();
		if (expressionUnary.op.equals(Kind.OP_MINUS)) {
			if (type.equals(Type.INTEGER)) {
				mv.visitInsn(DUP);
				mv.visitVarInsn(ISTORE, 7);
				mv.visitJumpInsn(IFGT, set);
				returnType = Type.INTEGER;
			} else if (type.equals(Type.FLOAT)) {
				mv.visitInsn(DUP);
				mv.visitVarInsn(FSTORE, 7);
				mv.visitLdcInsn((float) 7);
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGT, set);
				returnType = Type.FLOAT;
			}
		} else if (expressionUnary.op.equals(Kind.OP_EXCLAMATION)) {
			if (type.equals(Type.INTEGER)) {
				mv.visitLdcInsn(-1);
				mv.visitInsn(IXOR);
				returnType = Type.INTEGER;
			} else if (type.equals(Type.BOOLEAN)) {
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(IF_ICMPEQ, set);
				mv.visitLdcInsn(true);
				returnType = Type.BOOLEAN;
			}
		}
		if (type.equals(Type.INTEGER) && expressionUnary.op.equals(Kind.OP_MINUS)) {
			mv.visitVarInsn(ILOAD, 7);
			returnType = Type.INTEGER;
		}
		if (type.equals(Type.FLOAT) && expressionUnary.op.equals(Kind.OP_MINUS)) {
			mv.visitVarInsn(ILOAD, 7);
			returnType = Type.FLOAT;
		}

		mv.visitJumpInsn(GOTO, l);
		mv.visitLabel(set);

		if (type.equals(Type.INTEGER) && expressionUnary.op.equals(Kind.OP_MINUS)) {
			mv.visitVarInsn(ILOAD, 7);
			mv.visitInsn(INEG);
			returnType = Type.INTEGER;
		}
		if (type.equals(Type.FLOAT) && expressionUnary.op.equals(Kind.OP_MINUS)) {
			mv.visitVarInsn(ILOAD, 7);
			mv.visitInsn(FNEG);
			returnType = Type.FLOAT;
		}
		if (type.equals(Type.BOOLEAN) && expressionUnary.op.equals(Kind.OP_EXCLAMATION)) {
			mv.visitLdcInsn(false);
			returnType = Type.BOOLEAN;
		}
		mv.visitLabel(l);
		mv.visitLdcInsn(false);

		return returnType;
	}

}
