package cop5556fa18.PLPAST;

import java.util.List;

import cop5556fa18.PLPScanner.Token;

public class Block extends PLPASTNode {
	
	public final List<PLPASTNode> declarationsAndStatements;
	
	public PLPASTNode declarationsAndStatements(int index) {
		return declarationsAndStatements.get(index);
	}

	public Block(Token firstToken, List<PLPASTNode> declarationsAndStatements) {
		super(firstToken);
		this.declarationsAndStatements = declarationsAndStatements;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitBlock(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((declarationsAndStatements == null) ? 0 : declarationsAndStatements.hashCode());
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
		Block other = (Block) obj;
		if (declarationsAndStatements == null) {
			if (other.declarationsAndStatements != null)
				return false;
		} else if (!declarationsAndStatements.equals(other.declarationsAndStatements))
			return false;
		return true;
	}

}
