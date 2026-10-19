import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ParcelService {
    private static final ParcelService INSTANCE = new ParcelService();

    private final List<Parcel> parcels = new ArrayList<>();
    private final List<Parcel> readOnlyParcels = Collections.unmodifiableList(parcels);

    private ParcelService() {
    }

    public static ParcelService getInstance() {
        return INSTANCE;
    }

    public synchronized void addParcel(Parcel parcel) {
        if (parcel == null) {
            throw new IllegalArgumentException("Parcel cannot be null.");
        }
        if (findParcel(parcel.getTrackingNumber()) != null) {
            throw new IllegalArgumentException("Tracking number already exists!");
        }
        parcels.add(parcel);
    }

    public synchronized Parcel findParcel(String trackingNumber) {
        if (trackingNumber == null) {
            return null;
        }
        for (Parcel parcel : parcels) {
            if (parcel.getTrackingNumber().equalsIgnoreCase(trackingNumber)) {
                return parcel;
            }
        }
        return null;
    }

    public synchronized List<Parcel> getAllParcels() {
        return readOnlyParcels;
    }
}
