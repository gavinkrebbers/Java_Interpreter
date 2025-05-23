package code;

public class Opcode {

    public final byte value;

    public Opcode(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Opcode)) {
            return false;
        }
        Opcode other = (Opcode) o;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(value);
    }
}
