import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class App {
    private final InputReader input = new InputReader(new Scanner(System.in));
    private final ParcelService parcelService = ParcelService.getInstance();

    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        try {
            runMenu();
        } catch (InputEndedException exception) {
            System.out.println("\nInput ended. Exiting.");
        }
    }

    private void runMenu() {
        while (true) {
            System.out.println("\n=== Parcel Delivery System ===");
            System.out.println("1. Add Parcel");
            System.out.println("2. View All Parcels");
            System.out.println("3. Change Delivery Service");
            System.out.println("4. Mark as Delivered");
            System.out.println("5. Exit");

            switch (input.readInt("Choose an option: ", 1, 5)) {
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
                    System.out.println("Goodbye!");
                    return;
                default:
                    throw new IllegalStateException("Unexpected menu option.");
            }
        }
    }

    private void addParcel() {
        ParcelType type = input.readParcelType();
        String trackingNumber = input.readTrackingNumber();
        while (parcelService.findParcel(trackingNumber) != null) {
            System.out.println("Tracking number already exists!");
            trackingNumber = input.readTrackingNumber();
        }

        String receiverName = input.readNonEmpty("Receiver name: ");
        Parcel parcel = createParcel(type, trackingNumber, receiverName);
        DeliveryService deliveryService = input.readDeliveryService();
        while (deliveryService == DeliveryService.SAME_DAY && !parcel.isSameDayEligible()) {
            System.out.println("Same Day delivery is only for parcels up to 10 kg!");
            deliveryService = input.readDeliveryService();
        }

        parcel.setDeliveryService(deliveryService);
        parcelService.addParcel(parcel);
        System.out.printf(Locale.US, "Parcel added successfully! Delivery fee: Php %.2f%n",
                parcel.getDeliveryFee());
    }

    private Parcel createParcel(ParcelType type, String trackingNumber, String receiverName) {
        switch (type) {
            case DOCUMENT:
                BigDecimal documentWeight = input.readDecimal(
                        "Weight in kg (maximum 0.5): ", new BigDecimal("0.5"));
                int pages = input.readInt("Number of pages (1-100): ", 1, 100);
                return new DocumentParcel(trackingNumber, receiverName, documentWeight, pages);
            case BOX:
                BigDecimal boxWeight = input.readDecimal("Weight in kg (maximum 50): ", BigDecimal.valueOf(50));
                return new BoxParcel(trackingNumber, receiverName, boxWeight,
                        readDimension("Length in cm: "),
                        readDimension("Width in cm: "),
                        readDimension("Height in cm: "));
            case FRAGILE:
                BigDecimal fragileWeight = input.readDecimal("Weight in kg (maximum 30): ", BigDecimal.valueOf(30));
                int length = readDimension("Length in cm: ");
                int width = readDimension("Width in cm: ");
                int height = readDimension("Height in cm: ");
                String handlingNote = input.readNonEmpty("Handling note: ");
                return new FragileParcel(trackingNumber, receiverName, fragileWeight,
                        length, width, height, handlingNote);
            default:
                throw new IllegalStateException("Unexpected parcel type.");
        }
    }

    private int readDimension(String prompt) {
        return input.readInt(prompt, 1, Integer.MAX_VALUE);
    }

    private void viewAllParcels() {
        List<Parcel> parcels = parcelService.getAllParcels();
        if (parcels.isEmpty()) {
            System.out.println("No parcels to display.");
            return;
        }

        String header = String.format("%-12s %-10s %-22s %15s %-12s %18s %-10s",
                "Tracking No.", "Type", "Receiver", "Chargeable kg", "Service", "Delivery fee", "Status");
        System.out.println(header);
        System.out.println("-".repeat(header.length()));
        for (Parcel parcel : parcels) {
            System.out.printf(Locale.US, "%-12s %-10s %-22s %15.2f %-12s Php %14.2f %-10s%n",
                    parcel.getTrackingNumber(), parcel.getType(), truncate(parcel.getReceiverName(), 22),
                    parcel.getChargeableWeight().doubleValue(), parcel.getDeliveryService(),
                    parcel.getDeliveryFee(), parcel.getStatus());
        }
    }

    private void changeDeliveryService() {
        Parcel parcel = parcelService.findParcel(input.readNonEmpty("Tracking number: "));
        if (parcel == null) {
            System.out.println("Parcel not found!");
            return;
        }
        if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            System.out.println("Parcel already delivered!");
            return;
        }

        DeliveryService newService = input.readDeliveryService();
        if (newService == DeliveryService.SAME_DAY && !parcel.isSameDayEligible()) {
            System.out.println("Same Day delivery is only for parcels up to 10 kg!");
            return;
        }
        parcel.setDeliveryService(newService);
        System.out.printf(Locale.US, "Delivery service changed! New fee: Php %.2f%n",
                parcel.getDeliveryFee());
    }

    private void markAsDelivered() {
        Parcel parcel = parcelService.findParcel(input.readNonEmpty("Tracking number: "));
        if (parcel == null) {
            System.out.println("Parcel not found!");
        } else if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            System.out.println("Parcel was already delivered.");
        } else {
            parcel.markDelivered();
            System.out.println("Parcel delivered successfully!");
        }
    }

    private String truncate(String value, int maximumLength) {
        if (value.length() <= maximumLength) {
            return value;
        }
        return value.substring(0, maximumLength - 3) + "...";
    }
}
