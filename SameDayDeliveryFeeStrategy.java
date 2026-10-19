import java.math.BigDecimal;

public final class SameDayDeliveryFeeStrategy implements DeliveryFeeStrategy {
    @Override
    public BigDecimal calculate(BigDecimal baseFee) {
        return baseFee.multiply(BigDecimal.valueOf(2)).add(BigDecimal.valueOf(100));
    }
}
