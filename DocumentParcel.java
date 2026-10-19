import java.math.BigDecimal;

public final class DocumentParcel extends Parcel {
    private final int numberOfPages;

    public DocumentParcel(String trackingNumber, String receiverName, BigDecimal actualWeight, int numberOfPages) {
        super(trackingNumber, receiverName, actualWeight);
        if (actualWeight.compareTo(new BigDecimal("0.5")) > 0) {
            throw new IllegalArgumentException("Document weight cannot exceed 0.5 kg.");
        }
        if (numberOfPages < 1 || numberOfPages > 100) {
            throw new IllegalArgumentException("Number of pages must be between 1 and 100.");
        }
        this.numberOfPages = numberOfPages;
    }

    @Override
    public ParcelType getType() {
        return ParcelType.DOCUMENT;
    }

    @Override
    public BigDecimal getChargeableWeight() {
        return getActualWeight();
    }

    @Override
    protected BigDecimal getBaseFee() {
        return BigDecimal.valueOf(50);
    }
}
