public enum ParcelType {
    DOCUMENT("Document"),
    BOX("Box"),
    FRAGILE("Fragile");

    private final String label;

    ParcelType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
