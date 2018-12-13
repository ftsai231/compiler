package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class ExpressionIdentifier extends Expression {
	
	public final String name;
	public VariableDeclaration dec;                    //added by me
	public VariableListDeclaration decc;               //added by me
	

	public ExpressionIdentifier(Token firstToken, String name) {
		super(firstToken);
		this.name = name;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionIdent(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ExpressionIdentifier other = (ExpressionIdentifier) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
	

}
