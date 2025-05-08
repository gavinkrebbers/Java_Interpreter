package ast;

import java.util.ArrayList;
import java.util.List;

public class Program implements ProgramNode {

    public List<Statement> statements;

    public Program() {
        this.statements = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Statement stmt : statements) {
            out.append(stmt.toString());
        }
        return out.toString();
    }

    public void addStatement(Statement stmt) {
        this.statements.add(stmt);
    }

    @Override
    public String TokenLiteral() {
        if (statements != null && !statements.isEmpty()) {
            return statements.get(0).TokenLiteral();
        }
        return "";
    }

}
