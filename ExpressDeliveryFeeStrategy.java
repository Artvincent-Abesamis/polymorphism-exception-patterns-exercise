import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ExpressDeliveryFeeStrategy implements DeliveryFeeStrategy {
    @Override
    public BigDecimal calculate(BigDecimal baseFee) {
        return baseFee.multiply(new BigDecimal("1.5")).setScale(2, RoundingMode.HALF_UP);
    }
}
