import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class App {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final List<Parcel> PARCELS = new ArrayList<>();
    private static final Pattern TRACKING_NUMBER_PATTERN = Pattern.compile("ABC\\d{4}");

    public static void main(String[] args) {
        try {
            runMenu();
        } catch (InputEndedException exception) {
            System.out.println("\nInput ended. Exiting.");
        }
    }

    private static void runMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Parcel Delivery System ===");
            System.out.println("1. Add Parcel");
            System.out.println("2. View All Parcels");
            System.out.println("3. Change Delivery Service");
            System.out.println("4. Mark as Delivered");
            System.out.println("5. Exit");

            switch (readInt("Choose an option: ", 1, 5)) {
                case 1:
                    addParcel();
                    break;
                case 2:
                    viewAllParcels();
                    break;
                case 3:
                    changeDeliveryService();
                    break;
                case 4:
                    markAsDelivered();
                    break;
                case 5:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    throw new IllegalStateException("Unexpected menu option");
            }
        }
    }

    private static void addParcel() {
        ParcelType type = readParcelType();
        String trackingNumber = readTrackingNumber();
        while (findParcel(trackingNumber) != null) {
            System.out.println("Tracking number already exists!");
            trackingNumber = readTrackingNumber();
        }

        String receiverName = readNonEmpty("Receiver name: ");
        double weight;
        switch (type) {
            case DOCUMENT:
                weight = readDecimal("Weight in kg (maximum 0.5): ", 0, 0.5);
                int pages = readInt("Number of pages (1-100): ", 1, 100);
                addWithService(new DocumentParcel(trackingNumber, receiverName, weight, pages));
                break;
            case BOX:
                weight = readDecimal("Weight in kg (maximum 50): ", 0, 50);
                int length = readInt("Length in cm: ", 1, Integer.MAX_VALUE);
                int width = readInt("Width in cm: ", 1, Integer.MAX_VALUE);
                int height = readInt("Height in cm: ", 1, Integer.MAX_VALUE);
                addWithService(new BoxParcel(trackingNumber, receiverName, weight, length, width, height));
                break;
            case FRAGILE:
                weight = readDecimal("Weight in kg (maximum 30): ", 0, 30);
                length = readInt("Length in cm: ", 1, Integer.MAX_VALUE);
                width = readInt("Width in cm: ", 1, Integer.MAX_VALUE);
                height = readInt("Height in cm: ", 1, Integer.MAX_VALUE);
                String handlingNote = readNonEmpty("Handling note: ");
                addWithService(new FragileParcel(
                        trackingNumber, receiverName, weight, length, width, height, handlingNote));
                break;
            default:
                throw new IllegalStateException("Unexpected parcel type");
        }
    }

    private static void addWithService(Parcel parcel) {
        DeliveryService service = readDeliveryService();
        while (service == DeliveryService.SAME_DAY && !parcel.isSameDayEligible()) {
            System.out.println("Same Day delivery is only for parcels up to 10 kg!");
            service = readDeliveryService();
        }
        parcel.setDeliveryService(service);
        PARCELS.add(parcel);
        System.out.printf(Locale.US, "Parcel added successfully! Delivery fee: Php %.2f%n",
                parcel.getDeliveryFee());
    }

    private static void viewAllParcels() {
        if (PARCELS.isEmpty()) {
            System.out.println("No parcels to display.");
            return;
        }

        System.out.printf("%-12s %-10s %-22s %13s %-12s %16s %-10s%n",
                "Tracking No.", "Type", "Receiver", "Chargeable kg", "Service", "Delivery fee", "Status");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Parcel parcel : PARCELS) {
            System.out.printf(Locale.US, "%-12s %-10s %-22s %13.2f %-12s Php %11.2f %-10s%n",
                    parcel.getTrackingNumber(), parcel.getType(), truncate(parcel.getReceiverName(), 22),
                    parcel.getChargeableWeight(), parcel.getDeliveryService(), parcel.getDeliveryFee(),
                    parcel.getStatus());
        }
    }

    private static void changeDeliveryService() {
        Parcel parcel = findParcel(readNonEmpty("Tracking number: "));
        if (parcel == null) {
            System.out.println("Parcel not found!");
            return;
        }
        if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            System.out.println("Parcel already delivered!");
            return;
        }

        DeliveryService newService = readDeliveryService();
        if (newService == DeliveryService.SAME_DAY && !parcel.isSameDayEligible()) {
            System.out.println("Same Day delivery is only for parcels up to 10 kg!");
            return;
        }
        parcel.setDeliveryService(newService);
        System.out.printf(Locale.US, "Delivery service changed! New fee: Php %.2f%n",
                parcel.getDeliveryFee());
    }

    private static void markAsDelivered() {
        Parcel parcel = findParcel(readNonEmpty("Tracking number: "));
        if (parcel == null) {
            System.out.println("Parcel not found!");
        } else if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            System.out.println("Parcel was already delivered.");
        } else {
            parcel.markDelivered();
            System.out.println("Parcel delivered successfully!");
        }
    }

    private static ParcelType readParcelType() {
        System.out.println("Parcel type:");
        System.out.println("1. Document");
        System.out.println("2. Box");
        System.out.println("3. Fragile");
        int choice = readInt("Choose a parcel type: ", 1, 3);
        return ParcelType.values()[choice - 1];
    }

    private static DeliveryService readDeliveryService() {
        System.out.println("Delivery service:");
        System.out.println("1. Standard");
        System.out.println("2. Express");
        System.out.println("3. Same Day");
        int choice = readInt("Choose a delivery service: ", 1, 3);
        return DeliveryService.values()[choice - 1];
    }

    private static String readTrackingNumber() {
        while (true) {
            String trackingNumber = readNonEmpty("Tracking number (ABC followed by 4 digits): ")
                    .toUpperCase(Locale.ROOT);
            if (TRACKING_NUMBER_PATTERN.matcher(trackingNumber).matches()) {
                return trackingNumber;
            }
            System.out.println("Invalid tracking number. Use ABC followed by exactly 4 digits.");
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = readLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String value = readLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (number >= minimum && number <= maximum) {
                    return number;
                }
            } catch (NumberFormatException exception) {
                // Report invalid input below.
            }
            System.out.printf("Enter a whole number from %d to %d.%n", minimum, maximum);
        }
    }

    private static double readDecimal(String prompt, double minimumExclusive, double maximum) {
        while (true) {
            System.out.print(prompt);
            String value = readLine().trim();
            try {
                double number = Double.parseDouble(value);
                if (Double.isFinite(number) && number > minimumExclusive && number <= maximum) {
                    return number;
                }
            } catch (NumberFormatException exception) {
                // Report invalid input below.
            }
            System.out.printf(Locale.US, "Enter a number greater than 0 and no more than %.1f.%n", maximum);
        }
    }

    private static String readLine() {
        if (!INPUT.hasNextLine()) {
            throw new InputEndedException();
        }
        return INPUT.nextLine();
    }

    private static Parcel findParcel(String trackingNumber) {
        for (Parcel parcel : PARCELS) {
            if (parcel.getTrackingNumber().equalsIgnoreCase(trackingNumber)) {
                return parcel;
            }
        }
        return null;
    }

    private static String truncate(String value, int maximumLength) {
        if (value.length() <= maximumLength) {
            return value;
        }
        return value.substring(0, maximumLength - 3) + "...";
    }
}

abstract class Parcel {
    private final String trackingNumber;
    private final String receiverName;
    private final double actualWeight;
    private DeliveryService deliveryService;
    private ParcelStatus status = ParcelStatus.PENDING;

    Parcel(String trackingNumber, String receiverName, double actualWeight) {
        this.trackingNumber = trackingNumber;
        this.receiverName = receiverName;
        this.actualWeight = actualWeight;
    }

    abstract ParcelType getType();

    abstract double getChargeableWeight();

    abstract BigDecimal getBaseFee();

    final String getTrackingNumber() {
        return trackingNumber;
    }

    final String getReceiverName() {
        return receiverName;
    }

    final DeliveryService getDeliveryService() {
        return deliveryService;
    }

    final void setDeliveryService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    final ParcelStatus getStatus() {
        return status;
    }

    final void markDelivered() {
        status = ParcelStatus.DELIVERED;
    }

    final boolean isSameDayEligible() {
        return getChargeableWeight() <= 10;
    }

    final BigDecimal getDeliveryFee() {
        BigDecimal baseFee = getBaseFee();
        switch (deliveryService) {
            case STANDARD:
                return baseFee;
            case EXPRESS:
                return baseFee.multiply(new BigDecimal("1.5")).setScale(2, RoundingMode.HALF_UP);
            case SAME_DAY:
                return baseFee.multiply(BigDecimal.valueOf(2)).add(BigDecimal.valueOf(100));
            default:
                throw new IllegalStateException("Unexpected delivery service");
        }
    }

    final double getActualWeight() {
        return actualWeight;
    }
}

final class DocumentParcel extends Parcel {
    private final int numberOfPages;

    DocumentParcel(String trackingNumber, String receiverName, double actualWeight, int numberOfPages) {
        super(trackingNumber, receiverName, actualWeight);
        this.numberOfPages = numberOfPages;
    }

    @Override
    ParcelType getType() {
        return ParcelType.DOCUMENT;
    }

    @Override
    double getChargeableWeight() {
        return getActualWeight();
    }

    @Override
    BigDecimal getBaseFee() {
        return BigDecimal.valueOf(50);
    }
}

class BoxParcel extends Parcel {
    private final int length;
    private final int width;
    private final int height;

    BoxParcel(String trackingNumber, String receiverName, double actualWeight, int length, int width, int height) {
        super(trackingNumber, receiverName, actualWeight);
        this.length = length;
        this.width = width;
        this.height = height;
    }

    @Override
    ParcelType getType() {
        return ParcelType.BOX;
    }

    @Override
    double getChargeableWeight() {
        double volumetricWeight = (double) length * width * height / 3500;
        return Math.ceil(Math.max(getActualWeight(), volumetricWeight));
    }

    @Override
    BigDecimal getBaseFee() {
        return BigDecimal.valueOf(40).multiply(BigDecimal.valueOf((long) getChargeableWeight()));
    }
}

final class FragileParcel extends BoxParcel {
    private final String handlingNote;

    FragileParcel(String trackingNumber, String receiverName, double actualWeight,
                  int length, int width, int height, String handlingNote) {
        super(trackingNumber, receiverName, actualWeight, length, width, height);
        this.handlingNote = handlingNote;
    }

    @Override
    ParcelType getType() {
        return ParcelType.FRAGILE;
    }

    @Override
    BigDecimal getBaseFee() {
        return super.getBaseFee().add(BigDecimal.valueOf(100));
    }
}

enum ParcelType {
    DOCUMENT("Document"),
    BOX("Box"),
    FRAGILE("Fragile");

    private final String label;

    ParcelType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

enum DeliveryService {
    STANDARD("Standard"),
    EXPRESS("Express"),
    SAME_DAY("Same Day");

    private final String label;

    DeliveryService(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

enum ParcelStatus {
    PENDING("Pending"),
    DELIVERED("Delivered");

    private final String label;

    ParcelStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

final class InputEndedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
