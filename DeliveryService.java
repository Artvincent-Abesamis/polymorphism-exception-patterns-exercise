import java.math.BigDecimal;

public enum DeliveryService {
    STANDARD("Standard", new StandardDeliveryFeeStrategy()),
    EXPRESS("Express", new ExpressDeliveryFeeStrategy()),
    SAME_DAY("Same Day", new SameDayDeliveryFeeStrategy());

    private final String label;
    private final DeliveryFeeStrategy feeStrategy;

    DeliveryService(String label, DeliveryFeeStrategy feeStrategy) {
        this.label = label;
        this.feeStrategy = feeStrategy;
    }

    public BigDecimal calculateFee(BigDecimal baseFee) {
        return feeStrategy.calculate(baseFee);
    }

    @Override
    public String toString() {
        return label;
    }
}
