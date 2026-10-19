import java.math.BigDecimal;

public final class StandardDeliveryFeeStrategy implements DeliveryFeeStrategy {
    @Override
    public BigDecimal calculate(BigDecimal baseFee) {
        return baseFee;
    }
}
