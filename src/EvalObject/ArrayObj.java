package EvalObject;

import java.util.LinkedList;
import java.util.List;

public class ArrayObj implements EvalObject {

    public List<EvalObject> elements;

    public ArrayObj(List<EvalObject> elements) {
        this.elements = elements;
    }

    public ArrayObj() {
        elements = new LinkedList<>();
    }

    public List<EvalObject> getElements() {
        return elements;
    }

    public void setElements(List<EvalObject> elements) {
        this.elements = elements;
    }

    @Override
    public String type() {
        return "ARRAY";

    }

    @Override
    public String inspect() {
        StringBuilder output = new StringBuilder();

        List<String> elementsStrings = new LinkedList<>();

        for (EvalObject element : elements) {
            elementsStrings.add(element.inspect());
        }
        output.append("[");
        String joinedElements = String.join(", ", elementsStrings);
        output.append(joinedElements);
        output.append("]");
        return output.toString();
    }

}
