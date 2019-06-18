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


//import org.objectweb.asm.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class PLPCodeGen implements PLPASTVisitor, Opcodes {

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;


	public int current_slot = 0;

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
		Label startLabel = new Label();
		Label endLabel = new Label();
		for (PLPASTNode node : block.declarationsAndStatements) {
			if (node.getClass() == VariableDeclaration.class) {
				VariableDeclaration dec = (VariableDeclaration) node;
				dec.setSlot(current_slot);
				current_slot++;
			}
			else if (node.getClass() == VariableListDeclaration.class) {
				VariableListDeclaration decc = (VariableListDeclaration) node;
				for (String name : decc.names) {
					decc.setSlot(name, current_slot);
					current_slot++;
				}
			}
		}
		
		mv.visitLabel(startLabel);
		for (PLPASTNode node : block.declarationsAndStatements) {
			node.visit(this, null);
		}
		mv.visitLabel(endLabel);
		
		for (PLPASTNode node : block.declarationsAndStatements) {
			if (node.getClass() == VariableDeclaration.class) {
				VariableDeclaration dec = (VariableDeclaration)node;
				switch(dec.type) {
					case KW_int:{
						mv.visitLocalVariable(dec.name, "I", null, startLabel, endLabel, dec.getSlot());
					}
					break;
					case KW_float:{
						mv.visitLocalVariable(dec.name, "F", null, startLabel, endLabel, dec.getSlot());
					}
					break;
					case KW_boolean:{
						mv.visitLocalVariable(dec.name, "Z", null, startLabel, endLabel, dec.getSlot());
					}
					break;
					case KW_char:{
						mv.visitLocalVariable(dec.name, "C", null, startLabel, endLabel, dec.getSlot());
					}
					break;
					case KW_string:{
						mv.visitLocalVariable(dec.name, "Ljava/lang/String;", null, startLabel, endLabel, dec.getSlot());
					}
					break;
					default: {
						throw new Exception("Type error");
					}
				}
			} else if (node.getClass() == VariableListDeclaration.class) {
				VariableListDeclaration decc = (VariableListDeclaration) node;
				for (String name : decc.names) {
					switch(decc.type) {
						case KW_int:{
							mv.visitLocalVariable(name, "I", null, startLabel, endLabel, decc.getSlot(name));
						}
						break;
						case KW_float:{
							mv.visitLocalVariable(name, "F", null, startLabel, endLabel, decc.getSlot(name));
						}
						break;
						case KW_boolean:{
							mv.visitLocalVariable(name, "Z", null, startLabel, endLabel, decc.getSlot(name));
						}
						break;
						case KW_char:{
							mv.visitLocalVariable(name, "C", null, startLabel, endLabel, decc.getSlot(name));
						}
						break;
						case KW_string:{
							mv.visitLocalVariable(name, "Ljava/lang/String;", null, startLabel, endLabel, decc.getSlot(name));
						}
						break;
						default: {
							throw new Exception("Type error");
						}
					}
				}
			}
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
		mv.visitMaxs(0, 0);

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
		if (declaration.expression != null) {
			if (declaration.getType().equals(Type.INTEGER)) {
				declaration.SetJVMType("I");
				System.out.println("declaration I");
				
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ISTORE, declaration.getSlot());
				
			} else if (declaration.getType().equals(Type.FLOAT)) {
				declaration.SetJVMType("F");
				System.out.println("declaration F");
	
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(FSTORE, declaration.getSlot());
	
			} else if (declaration.getType().equals(Type.BOOLEAN)) {
				declaration.SetJVMType("Z");
				System.out.println("declaration Z");
	
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ISTORE, declaration.getSlot());
	
			} else if (declaration.getType().equals(Type.CHAR)) {
				declaration.SetJVMType("C");
				System.out.println("declaration C");
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ISTORE, declaration.getSlot());
	
			} else if (declaration.getType().equals(Type.STRING)) {
				declaration.SetJVMType("LJava/lang/String");
				System.out.println("declaration string");
	
				declaration.expression.visit(this, arg);
				mv.visitVarInsn(ASTORE, declaration.getSlot());
				}
		}

		return null;
	}

	@Override
	public Object visitVariableListDeclaration(VariableListDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(String name : declaration.names) {
			declaration.setSlot(name, current_slot);
			current_slot++;
		}
		
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
				// break;
			}
			case OP_MINUS: {
				mv.visitInsn(ISUB);
				return Type.INTEGER;
				// break;
			}
			case OP_TIMES: {
				mv.visitInsn(IMUL);
				return Type.INTEGER;
				// break;
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
				Label l1 = new Label();
				Label l2 = new Label();
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPEQ, l1);   //jump to label if the two integer refs are equal
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFEQ, l1);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l2);
				mv.visitLabel(l1);
				mv.visitLdcInsn(false);
				mv.visitLabel(l2);
				
				return Type.BOOLEAN;
				
			case OP_EQ:
				Label l3 = new Label();
				Label l4 = new Label();
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPNE, l3);	//jump to label if the two integer refs are equal
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFNE, l3);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l4);
				mv.visitLabel(l3);
				mv.visitLdcInsn(false);
				mv.visitLabel(l4);
				return Type.BOOLEAN;
			case OP_LE:{
				Label l11 = new Label();
				Label l21 = new Label();
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPGT, l11);
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFLT, l11);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l21);
				mv.visitLabel(l11);
				mv.visitLdcInsn(false);
				mv.visitLabel(l21);
				return Type.BOOLEAN;
			}
			
			case OP_LT:
			{
				Label l11 = new Label();
				Label l21 = new Label();
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPGE, l11);
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFLE, l11);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l21);
				mv.visitLabel(l11);
				mv.visitLdcInsn(false);
				mv.visitLabel(l21);	
				return Type.BOOLEAN;
			}
			case OP_GE:{
				Label l11 = new Label();
				Label l21 = new Label();
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPLT, l11);
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFGT, l11);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l21);
				mv.visitLabel(l11);
				mv.visitLdcInsn(false);
				mv.visitLabel(l21);
				return Type.BOOLEAN;

			}
			case OP_GT:
			{
				Label l11 = new Label();
				Label l21 = new Label();
				
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPLE, l11);
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFGE, l11);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l21);
				mv.visitLabel(l11);
				mv.visitLdcInsn(false);
				mv.visitLabel(l21);
				return Type.BOOLEAN;
			}
			}
			
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
				Label l1 = new Label();
				Label l2 = new Label();
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPEQ, l1);
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFEQ, l1);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l2);
				mv.visitLabel(l1);
				mv.visitLdcInsn(false);
				mv.visitLabel(l2);
				return Type.BOOLEAN;
				
			case OP_EQ:
				Label l3 = new Label();
				Label l4 = new Label();
				if(e1 == Type.INTEGER || e1 == Type.BOOLEAN)
				{
					mv.visitJumpInsn(IF_ICMPNE, l3);
				}
				else if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFNE, l3);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l4);
				mv.visitLabel(l3);
				mv.visitLdcInsn(false);
				mv.visitLabel(l4);
				return Type.BOOLEAN;
				
			case OP_LE:{
				Label l11 = new Label();
				Label l21 = new Label();
				if (e1 == Type.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFGT, l11);
				}
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l21);
				mv.visitLabel(l11);
				mv.visitLdcInsn(false);
				mv.visitLabel(l21);
				return Type.BOOLEAN;
			}
			
			case OP_LT:
			{
				Label l11 = new Label();
				Label l21 = new Label();

				if(e1==(Type.INTEGER) || e2==(Type.BOOLEAN))
				{
					mv.visitJumpInsn(IF_ICMPLT, l11);
				}
				else
				{
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFLT, l11);
				}
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, l21);
			    mv.visitLabel(l11);
			    mv.visitLdcInsn(true);
			    mv.visitLabel(l21);
				return Type.BOOLEAN;
			}
				
			case OP_GE:{
				Label l11 = new Label();
				Label l21 = new Label();

				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLT, l11);
				
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l21);
				mv.visitLabel(l11);
				mv.visitLdcInsn(false);
				mv.visitLabel(l21);
				return Type.BOOLEAN;
			}
			
			case OP_GT:
			{
				Label l11 = new Label();
				Label l21 = new Label();

				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLE, l11);
				
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO, l21);
				mv.visitLabel(l11);
				mv.visitLdcInsn(false);
				mv.visitLabel(l21);
				return Type.BOOLEAN;
			}
			
			}

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
				mv.visitInsn(SWAP);
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
				mv.visitInsn(SWAP);
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
				expressionBinary.leftExpression.visit(this, null);
				expressionBinary.rightExpression.visit(this, null);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitVarInsn(ASTORE, 1);
				mv.visitVarInsn(ASTORE, 2);
				mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 2);
//				mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);


				return Type.STRING;
			}
			}
		} else if (e1 == Type.BOOLEAN && e2 == Type.BOOLEAN) {
			switch (expressionBinary.op) {
			case OP_AND: {
				mv.visitInsn(IAND);
//				mv.visitJumpInsn(GOTO, endTrue);
//				mv.visitLabel(setTrue);
//				mv.visitLdcInsn(true);
//				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			}
			case OP_OR: {
				mv.visitInsn(IOR);
//				mv.visitJumpInsn(GOTO, endTrue);
//				mv.visitLabel(setTrue);
//				mv.visitLdcInsn(true);
//				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			}
			case OP_NEQ:
				mv.visitJumpInsn(IF_ICMPNE, setTrue);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, endTrue);
				mv.visitLabel(setTrue);
				mv.visitLdcInsn(true);
				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			case OP_EQ:
				mv.visitJumpInsn(IF_ICMPEQ, setTrue);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, endTrue);
				mv.visitLabel(setTrue);
				mv.visitLdcInsn(true);
				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			case OP_LE:
				mv.visitJumpInsn(IF_ICMPLE, setTrue);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, endTrue);
				mv.visitLabel(setTrue);
				mv.visitLdcInsn(true);
				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			case OP_LT:
				mv.visitJumpInsn(IF_ICMPLT, setTrue);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, endTrue);
				mv.visitLabel(setTrue);
				mv.visitLdcInsn(true);
				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			case OP_GE:
				mv.visitJumpInsn(IF_ICMPGE, setTrue);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, endTrue);
				mv.visitLabel(setTrue);
				mv.visitLdcInsn(true);
				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			case OP_GT:
				mv.visitJumpInsn(IF_ICMPGT, setTrue);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, endTrue);
				mv.visitLabel(setTrue);
				mv.visitLdcInsn(true);
				mv.visitLabel(endTrue);
				return Type.BOOLEAN;
			}
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
		mv.visitJumpInsn(IFNE, L0);  // if that's true
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
			}else if(FunctionWithArg.functionName.equals(Kind.KW_int)) {
				return Type.INTEGER;
			}
		} else if (t.equals(Type.FLOAT)) {
			if (FunctionWithArg.functionName.equals(Kind.KW_abs)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false);
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
			num = expressionIdent.decc.getSlot(expressionIdent.name);
			t = PLPTypes.getType(expressionIdent.decc.type);
			
			System.out.println("ExpressionIdent:decc");
		} else {
			num = expressionIdent.dec.getSlot();
			t = PLPTypes.getType(expressionIdent.dec.type);
			System.out.println("ExpressionIdent:dec");
		}

		
		if (expressionIdent.dec!=null) {
			VariableDeclaration declartion = (VariableDeclaration) expressionIdent.dec;
			switch (declartion.type) {
				case KW_int:{
					mv.visitVarInsn(ILOAD, declartion.getSlot());
				}
				break;
				case KW_float:{
					mv.visitVarInsn(FLOAD, declartion.getSlot());
				}
				break;
				case KW_boolean:{
					mv.visitVarInsn(ILOAD, declartion.getSlot());
				}
				break;
				case KW_char:{
					mv.visitVarInsn(ILOAD, declartion.getSlot());
				}
				break;
				case KW_string:{
					mv.visitVarInsn(ALOAD, declartion.getSlot());
				}
				break;
			}
		}
		else if (expressionIdent.decc!=null) {
			VariableListDeclaration declartion = (VariableListDeclaration) expressionIdent.decc;
			switch (declartion.type) {
				case KW_int:{
					mv.visitVarInsn(ILOAD, declartion.getSlot(expressionIdent.name));
				}
				break;
				case KW_float:{
					mv.visitVarInsn(FLOAD, declartion.getSlot(expressionIdent.name));
				}
				break;
				case KW_boolean:{
					mv.visitVarInsn(ILOAD, declartion.getSlot(expressionIdent.name));
				}
				break;
				case KW_char:{
					mv.visitVarInsn(ILOAD, declartion.getSlot(expressionIdent.name));
				}
				break;
				case KW_string:{
					mv.visitVarInsn(ALOAD, declartion.getSlot(expressionIdent.name));
				}
				break;
			}
		}

		return t;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// if(intoLHS) mv.visitIntInsn(BIPUSH, expressionIntegerLiteral.value);
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

		Type t = (Type) statementAssign.expression.visit(this, null);
		Type tlhs = (Type) statementAssign.lhs.visit(this, null);
		
		//this part of code is redundant, and I suffer from this for a long time!
		//the mv.visitInsn has already done in other parts!
		
//		if (t.equals(Type.INTEGER)) {
//			if (statementAssign.expression instanceof ExpressionIntegerLiteral) {
//				if (statementAssign.lhs.dec != null) {
//					mv.visitInsn(statementAssign.lhs.dec.getSlot());
//				} else {
//					mv.visitInsn(statementAssign.lhs.decc.getSlot(statementAssign.lhs.identifier));
//				}
//			}
//		} else if (t.equals(Type.FLOAT)) {
//			if (statementAssign.expression instanceof ExpressionFloatLiteral) {
//				if (statementAssign.lhs.dec != null) {
//					mv.visitInsn(statementAssign.lhs.dec.getSlot());
//				} else {
//					mv.visitInsn(statementAssign.lhs.decc.getSlot(statementAssign.lhs.identifier));
//				}
//			}
//
//		} else if (t.equals(Type.BOOLEAN)) {
//			if (statementAssign.expression instanceof ExpressionBooleanLiteral) {
//				if (statementAssign.lhs.dec != null) {
//					mv.visitInsn(statementAssign.lhs.dec.getSlot());
//				} else {
//					mv.visitInsn(statementAssign.lhs.decc.getSlot(statementAssign.lhs.identifier));
//				}
//			}
//		} else if (t.equals(Type.CHAR)) {
//			if (statementAssign.expression instanceof ExpressionCharLiteral) {
//				if (statementAssign.lhs.dec != null) {
//					mv.visitInsn(statementAssign.lhs.dec.getSlot());
//				} else {
//					mv.visitInsn(statementAssign.lhs.decc.getSlot(statementAssign.lhs.identifier));
//				}
//			}
//
//		} else if (t.equals(Type.STRING)) {
//			if (statementAssign.expression instanceof ExpressionStringLiteral) {
//				if (statementAssign.lhs.dec != null) {
//					mv.visitInsn(statementAssign.lhs.dec.getSlot());
//				} else {
//					mv.visitInsn(statementAssign.lhs.decc.getSlot(statementAssign.lhs.identifier));
//				}
//			}
//		}
		return null;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		PLPTypes type = new PLPTypes();
		Type t = null;
		// t = lhs.dec.getType();
		System.out.println("LHS:f");
		

		if (lhs.dec != null) {

			t = lhs.dec.getType();
			System.out.println("LHS:dec");
		} else {
			t = lhs.decc.getType();
			System.out.println("LHS:decc");
		}


		if (t == Type.INTEGER || t == Type.BOOLEAN || t == Type.CHAR) {
			if (lhs.dec != null) {
				
				mv.visitVarInsn(ISTORE, lhs.dec.getSlot());
			} else if (lhs.decc != null) {
				mv.visitVarInsn(ISTORE, lhs.decc.getSlot(lhs.identifier));
			}
		} else if (t == Type.STRING) {
			if (lhs.dec != null) {
				mv.visitVarInsn(ASTORE, lhs.dec.getSlot());
			} else if (lhs.decc != null) {
				mv.visitVarInsn(ASTORE, lhs.decc.getSlot(lhs.identifier));
			}
		} else if (t == Type.FLOAT) {
			if (lhs.dec != null) {
				mv.visitVarInsn(FSTORE, lhs.dec.getSlot());
			} else if (lhs.decc != null) {
				mv.visitVarInsn(FSTORE, lhs.decc.getSlot(lhs.identifier));
			}
		}

		return t;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Label afterIf = new Label();
		ifStatement.condition.visit(this, arg);
		mv.visitJumpInsn(IFEQ, afterIf);     //jump to label if value is zero	1 -> true	0 -> false
		ifStatement.block.visit(this, arg);
		mv.visitLabel(afterIf); 

		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitJumpInsn(GOTO, l1);
		
		mv.visitLabel(l2);
		whileStatement.b.visit(this, arg);
		mv.visitLabel(l1);
		whileStatement.condition.visit(this, arg);
		mv.visitJumpInsn(IFNE, l2);     // IFNE: jump to label if value is not zero
		
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
				mv.visitInsn(SWAP);
				PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
			}
				break;
			case BOOLEAN: {
				PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
				// TODO implement functionality
				mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				// printStatement.expression.visit(this, arg);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
				// throw new UnsupportedOperationException();
			}
				break;

			case FLOAT: {
				PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
				// TODO implement functionality
				mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				// printStatement.expression.visit(this, arg);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
				// throw new UnsupportedOperationException();
			}
				break;

			case CHAR: {
				PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
				// TODO implement functionality
				mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				// printStatement.expression.visit(this, arg);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(C)V", false);
				// throw new UnsupportedOperationException();
			}
				break;

			case STRING: {
	
				PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(Ljava/lang/String;)V", false);
			}
				break;
		}

		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		sleepStatement.time.visit(this, null);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
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
				mv.visitInsn(INEG);
				returnType = Type.INTEGER;
			} else if (type.equals(Type.FLOAT)) {
				mv.visitInsn(FNEG);
				returnType = Type.FLOAT;
			}
		} else if (expressionUnary.op.equals(Kind.OP_EXCLAMATION)) {
			if (type.equals(Type.INTEGER)) {
				mv.visitLdcInsn(-1);
				mv.visitInsn(IXOR);
				returnType = Type.INTEGER;
			} else if (type.equals(Type.BOOLEAN)) {
				Label l1 = new Label();
				Label l2 = new Label();
				mv.visitJumpInsn(IFEQ, l1);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, l2);
				mv.visitLabel(l1);
				mv.visitLdcInsn(true);
				mv.visitLabel(l2);

				returnType = Type.BOOLEAN;
			}
		}
		if (type.equals(Type.INTEGER) && expressionUnary.op.equals(Kind.OP_MINUS)) {
			returnType = Type.INTEGER;
		}
		if (type.equals(Type.FLOAT) && expressionUnary.op.equals(Kind.OP_MINUS)) {
			returnType = Type.FLOAT;
		}

		if (type.equals(Type.INTEGER) && expressionUnary.op.equals(Kind.OP_MINUS)) {

			returnType = Type.INTEGER;
		}
		if (type.equals(Type.FLOAT) && expressionUnary.op.equals(Kind.OP_MINUS)) {

			returnType = Type.FLOAT;
		}
		if (type.equals(Type.BOOLEAN) && expressionUnary.op.equals(Kind.OP_EXCLAMATION)) {
			returnType = Type.BOOLEAN;
		}

		return returnType;
	}

}
