import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class InputReader {
    private static final Pattern TRACKING_NUMBER_PATTERN = Pattern.compile("ABC\\d{4}");

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        this.scanner = scanner;
    }

    public ParcelType readParcelType() {
        System.out.println("Parcel type:");
        System.out.println("1. Document");
        System.out.println("2. Box");
        System.out.println("3. Fragile");
        return ParcelType.values()[readInt("Choose a parcel type: ", 1, 3) - 1];
    }

    public DeliveryService readDeliveryService() {
        System.out.println("Delivery service:");
        System.out.println("1. Standard");
        System.out.println("2. Express");
        System.out.println("3. Same Day");
        return DeliveryService.values()[readInt("Choose a delivery service: ", 1, 3) - 1];
    }

    public String readTrackingNumber() {
        while (true) {
            String trackingNumber = readNonEmpty("Tracking number (ABC followed by 4 digits): ")
                    .toUpperCase(Locale.ROOT);
            if (TRACKING_NUMBER_PATTERN.matcher(trackingNumber).matches()) {
                return trackingNumber;
            }
            System.out.println("Invalid tracking number. Use ABC followed by exactly 4 digits.");
        }
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = readLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    public int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String value = readLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (number >= minimum && number <= maximum) {
                    return number;
                }
            } catch (NumberFormatException exception) {
                // Invalid input is reported below.
            }
            System.out.printf("Enter a whole number from %d to %d.%n", minimum, maximum);
        }
    }

    public BigDecimal readDecimal(String prompt, BigDecimal maximum) {
        while (true) {
            System.out.print(prompt);
            String value = readLine().trim();
            try {
                BigDecimal number = new BigDecimal(value);
                if (number.signum() > 0 && number.compareTo(maximum) <= 0) {
                    return number;
                }
            } catch (NumberFormatException exception) {
                // Invalid input is reported below.
            }
            System.out.printf(Locale.US, "Enter a number greater than 0 and no more than %s.%n", maximum);
        }
    }

    private String readLine() {
        if (!scanner.hasNextLine()) {
            throw new InputEndedException();
        }
        return scanner.nextLine();
    }
}
