package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public abstract class PLPASTNode {
	
	final public Token firstToken;

	public PLPASTNode(Token firstToken) {
		super();
		this.firstToken = firstToken;
	}
	
	public abstract Object visit(PLPASTVisitor v, Object arg) throws Exception;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstToken == null) ? 0 : firstToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PLPASTNode other = (PLPASTNode) obj;
		if (firstToken == null) {
			if (other.firstToken != null)
				return false;
		} else if (!firstToken.equals(other.firstToken))
			return false;
		return true;
	}

}
