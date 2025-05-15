package EvalObject;

public class HashKey {

    public String type;
    public int value;

    public HashKey(String type, int value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value;
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HashKey)) {
            return false;
        }
        HashKey key = (HashKey) object;
        boolean sameKey = key.type.equals(this.type);
        if (!sameKey) {
            return false;
        }
        boolean sameValue = key.value == this.value;
        return sameValue;
    }
}
