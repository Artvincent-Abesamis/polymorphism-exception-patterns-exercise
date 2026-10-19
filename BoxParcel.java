import java.math.BigDecimal;
import java.math.RoundingMode;

public class BoxParcel extends Parcel {
    private final int length;
    private final int width;
    private final int height;

    public BoxParcel(String trackingNumber, String receiverName, BigDecimal actualWeight,
                     int length, int width, int height) {
        super(trackingNumber, receiverName, actualWeight);
        if (actualWeight.compareTo(BigDecimal.valueOf(50)) > 0) {
            throw new IllegalArgumentException("Box weight cannot exceed 50 kg.");
        }
        validateDimension(length);
        validateDimension(width);
        validateDimension(height);
        this.length = length;
        this.width = width;
        this.height = height;
    }

    private static void validateDimension(int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Each dimension must be greater than 0 cm.");
        }
    }

    @Override
    public ParcelType getType() {
        return ParcelType.BOX;
    }

    @Override
    public BigDecimal getChargeableWeight() {
        BigDecimal volumetricWeight = BigDecimal.valueOf(length)
                .multiply(BigDecimal.valueOf(width))
                .multiply(BigDecimal.valueOf(height))
                .divide(BigDecimal.valueOf(3500), 10, RoundingMode.HALF_UP);
        return getActualWeight().max(volumetricWeight).setScale(0, RoundingMode.CEILING);
    }

    @Override
    protected BigDecimal getBaseFee() {
        return BigDecimal.valueOf(40).multiply(getChargeableWeight());
    }
}
