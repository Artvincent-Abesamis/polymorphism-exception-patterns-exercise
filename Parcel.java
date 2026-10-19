import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class Parcel {
    private static final Pattern TRACKING_NUMBER_PATTERN =
            Pattern.compile("ABC\\d{4}", Pattern.CASE_INSENSITIVE);

    private final String trackingNumber;
    private final String receiverName;
    private final BigDecimal actualWeight;
    private DeliveryService deliveryService = DeliveryService.STANDARD;
    private ParcelStatus status = ParcelStatus.PENDING;

    protected Parcel(String trackingNumber, String receiverName, BigDecimal actualWeight) {
        if (trackingNumber == null || !TRACKING_NUMBER_PATTERN.matcher(trackingNumber).matches()) {
            throw new IllegalArgumentException("Tracking number must be ABC followed by 4 digits.");
        }
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver name cannot be empty.");
        }
        if (actualWeight == null || actualWeight.signum() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0.");
        }
        this.trackingNumber = trackingNumber.toUpperCase(Locale.ROOT);
        this.receiverName = receiverName.trim();
        this.actualWeight = actualWeight;
    }

    public abstract ParcelType getType();

    public abstract BigDecimal getChargeableWeight();

    protected abstract BigDecimal getBaseFee();

    public final String getTrackingNumber() {
        return trackingNumber;
    }

    public final String getReceiverName() {
        return receiverName;
    }

    public final DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public final void setDeliveryService(DeliveryService deliveryService) {
        if (deliveryService == null) {
            throw new IllegalArgumentException("Delivery service cannot be null.");
        }
        if (deliveryService == DeliveryService.SAME_DAY && !isSameDayEligible()) {
            throw new IllegalArgumentException("Same Day delivery is only for parcels up to 10 kg!");
        }
        this.deliveryService = deliveryService;
    }

    public final ParcelStatus getStatus() {
        return status;
    }

    public final void markDelivered() {
        status = ParcelStatus.DELIVERED;
    }

    public final boolean isSameDayEligible() {
        return getChargeableWeight().compareTo(BigDecimal.TEN) <= 0;
    }

    public final BigDecimal getDeliveryFee() {
        return deliveryService.calculateFee(getBaseFee());
    }

    protected final BigDecimal getActualWeight() {
        return actualWeight;
    }
}
