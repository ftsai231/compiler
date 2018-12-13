package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;
import cop5556fa18.PLPScanner.Kind;

public class FunctionWithArg extends Expression {

	public final Kind functionName;
	public final Expression expression;
	
	public FunctionWithArg(Token firstToken, Kind functionName, Expression e) {
		super(firstToken);
		this.functionName = functionName;
		this.expression = e;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitFunctionWithArg(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((functionName == null) ? 0 : functionName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionWithArg other = (FunctionWithArg) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (functionName != other.functionName)
			return false;
		return true;
	}	
}
