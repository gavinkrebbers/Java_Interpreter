package ast;

import java.util.List;

public class Program implements ProgramNode {

    public List<Statement> statements;

    @Override
    public String TokenLiteral() {
        if (statements != null && !statements.isEmpty()) {
            return statements.get(0).TokenLiteral();
        }
        return "";
    }

}
