import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Event {

    private String name;
    private String description;
    private Date registrationBegin;
    private Date registrationEnd;
    private String posterPath;
    private String qrCodePath;
    private int maxEntrants;
    private int waitingListSize;
    private List<Entrant> waitList;
    private List<Entrant> roster;
    private FirebaseFirestore db;
    private String organizerId;

    public Event(String name, String description, Date registrationBegin, Date registrationEnd, String posterPath, String qrCodePath, int maxEntrants, int waitingListSize){
        this.name = name;
        this.description = description;
        this.registrationBegin = registrationBegin;
        this.registrationEnd = registrationEnd;
        this.posterPath = posterPath;
        this.qrCodePath = qrCodePath;
        this.maxEntrants = maxEntrants;
        this.waitingListSize = waitingListSize;
        this.waitList = new ArrayList<>();
        this.roster = new ArrayList<>();
    }

    public void addToWaitList(Entrant entrant){
        if(waitList.size() < waitingListSize){
            waitList.add(entrant);
            updateFieldInDb("waitListCount", waitList.size());
        } else {
            throw new IllegalStateException("Waiting list is full");
        }
    }

    public void addEntrant(Entrant entrant){
        if(roster.size() < maxEntrants){
            roster.add(entrant);
            updateFieldInDb("rosterCount", roster.size());
        } else if(waitList.size() < waitingListSize){
            waitList.add(entrant);
            updateFieldInDb("waitListCount", waitList.size());
        } else {
            throw new IllegalStateException("Event is full");
        }
    }

    public String getName() {
        name = getStringFromDb("name", name);
        return name;
    }

    public void setName(String name) {
        this.name = name;
        writeFullDocument();
    }

    public String getDescription() {
        description = getStringFromDb("description", description);
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateFieldInDb("description", description);
    }

    public Date getRegistrationBegin() {
        registrationBegin = getDateFromDb("registrationBegin", registrationBegin);
        return registrationBegin;
    }

    public void setRegistrationBegin(Date registrationBegin) {
        this.registrationBegin = registrationBegin;
        updateFieldInDb("registrationBegin", registrationBegin);
    }

    public Date getRegistrationEnd() {
        registrationEnd = getDateFromDb("registrationEnd", registrationEnd);
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
        updateFieldInDb("registrationEnd", registrationEnd);
    }

    public String getPosterPath() {
        posterPath = getStringFromDb("posterPath", posterPath);
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        updateFieldInDb("posterPath", posterPath);
    }

    public String getQRCodePath() {
        qrCodePath = getStringFromDb("qrCodePath", qrCodePath);
        return qrCodePath;
    }

    public void setQRCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
        updateFieldInDb("qrCodePath", qrCodePath);
    }

    public int getMaxEntrants() {
        maxEntrants = getIntFromDb("maxEntrants", maxEntrants);
        return maxEntrants;
    }

    public void setMaxEntrants(int maxEntrants) {
        this.maxEntrants = maxEntrants;
        updateFieldInDb("maxEntrants", maxEntrants);
    }

    public int getWaitingListSize() {
        waitingListSize = getIntFromDb("waitingListSize", waitingListSize);
        return waitingListSize;
    }

    public void setWaitingListSize(int waitingListSize) {
        this.waitingListSize = waitingListSize;
        updateFieldInDb("waitingListSize", waitingListSize);
    }

    public List<Entrant> getWaitList() {
        int count = getIntFromDb("waitListCount", waitList.size());
        syncListSize(waitList, count);
        return waitList;
    }

    public void setWaitList(List<Entrant> waitList) {
        this.waitList = waitList;
        updateFieldInDb("waitListCount", waitList.size());
    }

    public List<Entrant> getRoster() {
        int count = getIntFromDb("rosterCount", roster.size());
        syncListSize(roster, count);
        return roster;
    }

    public void setRoster(List<Entrant> roster) {
        this.roster = roster;
        updateFieldInDb("rosterCount", roster.size());
    }

    public void attachFirestore(FirebaseFirestore db, String organizerId) {
        this.db = db;
        this.organizerId = organizerId;
        writeFullDocument();
    }

    public Map<String, Object> toFirestoreMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("name", name);
        map.put("organizerId", organizerId);
        map.put("description", description);
        map.put("registrationBegin", registrationBegin);
        map.put("registrationEnd", registrationEnd);
        map.put("posterPath", posterPath);
        map.put("qrCodePath", qrCodePath);
        map.put("maxEntrants", maxEntrants);
        map.put("waitingListSize", waitingListSize);
        map.put("rosterCount", roster.size());
        map.put("waitListCount", waitList.size());

        return map;
    }

    private void writeFullDocument() {
        if (db == null) {
            return;
        }
        String eventId = getFirestoreDocumentId();
        if (eventId.isEmpty()) {
            return;
        }
        db.collection("events").document(eventId).set(toFirestoreMap(), SetOptions.merge());
    }

    private DocumentSnapshot getSnapshot() {
        try {
            return Tasks.await(db.collection("events").document(getFirestoreDocumentId()).get());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read event from Firestore.", ex);
        }
    }

    private void updateFieldInDb(String field, Object value) {
        if (db == null || getFirestoreDocumentId().isEmpty()) {
            return;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);
        db.collection("events")
                .document(getFirestoreDocumentId())
                .set(updates, SetOptions.merge());
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

    private int getIntFromDb(String field, int fallback) {
        if (db == null || getFirestoreDocumentId().isEmpty()) {
            return fallback;
        }
        DocumentSnapshot snapshot = getSnapshot();
        Long value = snapshot.getLong(field);
        return value == null ? fallback : value.intValue();
    }

    private void syncListSize(List<Entrant> list, int targetSize) {
        while (list.size() > targetSize) {
            list.remove(list.size() - 1);
        }
        while (list.size() < targetSize) {
            list.add(null);
        }
    }

    public String getFirestoreDocumentId() {
        return name == null ? "" : name.trim().toLowerCase().replace(" ", "_");
    }

}
