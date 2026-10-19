import java.math.BigDecimal;

public final class FragileParcel extends BoxParcel {
    private final String handlingNote;

    public FragileParcel(String trackingNumber, String receiverName, BigDecimal actualWeight,
                         int length, int width, int height, String handlingNote) {
        super(trackingNumber, receiverName, actualWeight, length, width, height);
        if (actualWeight.compareTo(BigDecimal.valueOf(30)) > 0) {
            throw new IllegalArgumentException("Fragile parcel weight cannot exceed 30 kg.");
        }
        if (handlingNote == null || handlingNote.trim().isEmpty()) {
            throw new IllegalArgumentException("Handling note cannot be empty.");
        }
        this.handlingNote = handlingNote.trim();
    }

    @Override
    public ParcelType getType() {
        return ParcelType.FRAGILE;
    }

    @Override
    protected BigDecimal getBaseFee() {
        return super.getBaseFee().add(BigDecimal.valueOf(100));
    }
}
