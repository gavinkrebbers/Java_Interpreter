package code;

public class Definition {

    private final String name;
    private final int[] operandWidths;

    public Definition(String name, int[] operandWidths) {
        this.name = name;
        this.operandWidths = operandWidths;
    }

    public String getName() {
        return name;
    }

    public int[] getOperandWidths() {
        return operandWidths;
    }
}
