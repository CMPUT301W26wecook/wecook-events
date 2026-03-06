import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Organizer {
    private String name;
    private Date dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private ArrayList<Event> eventList;
    private FirebaseFirestore db;

    public Organizer(String name, Date dateOfBirth, String address, String city, String postalCode, String country) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.eventList = new ArrayList<>();
    }

    public void createEvent(Event event) {
        eventList.add(event);
        if (db != null) {
            event.attachFirestore(db, getFirestoreDocumentId());
        }
        updateFieldInDb("managedEventIds", getEventIds());
    }

    public void changeRegistrationPeriod(Event event, Date startDate, Date endDate) {
        event.setRegistrationBegin(startDate);
        event.setRegistrationEnd(endDate);
    }

    public void uploadEventPoster(Event event, String posterPath) {
        event.setPosterPath(posterPath);
    }

    public void changeEventPoster(Event event, String newPosterPath) {
        event.setPosterPath(newPosterPath);
    }

    // Getters and Setters
    public String getName() {
        name = getStringFromDb("name", name);
        return name;
    }

    public void setName(String name) {
        String oldId = getFirestoreDocumentId();
        this.name = name;
        if (db != null) {
            for (Event event : eventList) {
                event.attachFirestore(db, getFirestoreDocumentId());
            }
        }
        writeFullDocument();
        if (db != null && !oldId.isEmpty() && !oldId.equals(getFirestoreDocumentId())) {
            db.collection("organizers").document(oldId).delete();
        }
    }

    public Date getDateOfBirth() {
        dateOfBirth = getDateFromDb("dateOfBirth", dateOfBirth);
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        updateFieldInDb("dateOfBirth", dateOfBirth);
    }

    public String getAddress() {
        address = getStringFromDb("address", address);
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        updateFieldInDb("address", address);
    }

    public String getCity() {
        city = getStringFromDb("city", city);
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        updateFieldInDb("city", city);
    }

    public String getPostalCode() {
        postalCode = getStringFromDb("postalCode", postalCode);
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        updateFieldInDb("postalCode", postalCode);
    }

    public String getCountry() {
        country = getStringFromDb("country", country);
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        updateFieldInDb("country", country);
    }

    public ArrayList<Event> getEventList() {
        List<String> ids = getStringListFromDb("managedEventIds", getEventIds());
        if (ids != null) {
            eventList.removeIf(event -> !ids.contains(event.getFirestoreDocumentId()));
        }
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
        if (db != null) {
            for (Event event : eventList) {
                event.attachFirestore(db, getFirestoreDocumentId());
            }
        }
        updateFieldInDb("managedEventIds", getEventIds());
    }

    public void attachFirestore(FirebaseFirestore db) {
        this.db = db;
        for (Event event : eventList) {
            event.attachFirestore(db, getFirestoreDocumentId());
        }
        writeFullDocument();
    }

    public Map<String, Object> toFirestoreMap() {
        Map<String, Object> organizerMap = new HashMap<>();
        organizerMap.put("name", name);
        organizerMap.put("dateOfBirth", dateOfBirth);
        organizerMap.put("address", address);
        organizerMap.put("city", city);
        organizerMap.put("postalCode", postalCode);
        organizerMap.put("country", country);

        List<String> eventIds = new ArrayList<>();
        for (Event event : eventList) {
            eventIds.add(event.getFirestoreDocumentId());
        }
        organizerMap.put("managedEventIds", eventIds);
        return organizerMap;
    }

    public String getFirestoreDocumentId() {
        return name == null ? "" : name.trim().toLowerCase().replace(" ", "_");
    }

    private List<String> getEventIds() {
        List<String> ids = new ArrayList<>();
        for (Event event : eventList) {
            ids.add(event.getFirestoreDocumentId());
        }
        return ids;
    }

    private void writeFullDocument() {
        if (db == null) {
            return;
        }
        String organizerId = getFirestoreDocumentId();
        if (organizerId.isEmpty()) {
            return;
        }
        db.collection("organizers").document(organizerId).set(toFirestoreMap(), SetOptions.merge());
    }

    private DocumentSnapshot getSnapshot() {
        try {
            return Tasks.await(db.collection("organizers").document(getFirestoreDocumentId()).get());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read organizer from Firestore.", ex);
        }
    }

    private void updateFieldInDb(String field, Object value) {
        if (db == null || getFirestoreDocumentId().isEmpty()) {
            return;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);
        db.collection("organizers").document(getFirestoreDocumentId()).set(updates, SetOptions.merge());
    }

    private String getStringFromDb(String field, String fallback) {
        if (db == null || getFirestoreDocumentId().isEmpty()) {
            return fallback;
        }
        DocumentSnapshot snapshot = getSnapshot();
        String value = snapshot.getString(field);
        return value == null ? fallback : value;
    }

    private Date getDateFromDb(String field, Date fallback) {
        if (db == null || getFirestoreDocumentId().isEmpty()) {
            return fallback;
        }
        DocumentSnapshot snapshot = getSnapshot();
        Date value = snapshot.getDate(field);
        return value == null ? fallback : value;
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringListFromDb(String field, List<String> fallback) {
        if (db == null || getFirestoreDocumentId().isEmpty()) {
            return fallback;
        }
        DocumentSnapshot snapshot = getSnapshot();
        List<String> value = (List<String>) snapshot.get(field);
        return value == null ? fallback : value;
    }
}
