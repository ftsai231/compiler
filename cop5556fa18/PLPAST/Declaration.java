package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;
import cop5556fa18.PLPTypes.Type;

public abstract class Declaration extends PLPASTNode {
//	public Type type;
	
	public Declaration(Token firstToken) {
		super(firstToken);
	}
}
