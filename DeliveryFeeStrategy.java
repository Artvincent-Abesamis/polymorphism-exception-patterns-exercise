import java.math.BigDecimal;

public interface DeliveryFeeStrategy {
    BigDecimal calculate(BigDecimal baseFee);
}
