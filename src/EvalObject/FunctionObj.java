package EvalObject;

import ast.BlockStatement;
import ast.Identifier;
import java.util.LinkedList;
import java.util.List;

public class FunctionObj implements EvalObject {

    public List<Identifier> parameters;
    public BlockStatement body;
    public Environment env;

    public FunctionObj(List<Identifier> parameters, BlockStatement body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    @Override
    public String type() {
        return "FUNCTION";

    }

    @Override
    public String inspect() {
        StringBuilder output = new StringBuilder();
        output.append("fn");
        output.append("(");
        List<String> paramsStr = new LinkedList<>();

        for (Identifier param : this.parameters) {
            paramsStr.add(param.value);
        }
        String allParams = String.join(",", paramsStr);
        output.append(allParams);
        output.append("){\n");
        output.append(this.body.toString());
        output.append("\n}");

        return output.toString();

    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public Environment getEnv() {
        return env;
    }
}
