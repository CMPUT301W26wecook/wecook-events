package com.example.wecookproject;

import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Lightweight model used to present entrant data in the organizer waitlist UI. Within the app it
 * acts as a view-model-style adapter object, converting Firestore user documents into display-ready
 * values for list rendering and search matching.
 *
 * Outstanding issues:
 * - Search and avatar-label behavior rely on simple string transformations and do not account for
 *   localization, or more robust name handling.
 */
public class OrganizerWaitlistItem {
    private final String entrantId;
    private final String displayName;
    private final String subtitle;

    /**
     * Creates a waitlist item with preformatted display text.
     *
     * @param entrantId the unique identifier of the entrant represented by the row
     * @param displayName the display name shown to the organizer
     * @param subtitle the secondary label shown beneath the display name
     */
    public OrganizerWaitlistItem(String entrantId, String displayName, String subtitle) {
        this.entrantId = entrantId;
        this.displayName = displayName;
        this.subtitle = subtitle;
    }

    /**
     * Builds a waitlist item from a Firestore entrant profile document.
     *
     * @param entrantId the unique identifier of the entrant whose document was loaded
     * @param snapshot the Firestore document snapshot containing entrant profile data
     * @return a waitlist item populated from the snapshot contents
     */
    public static OrganizerWaitlistItem fromSnapshot(String entrantId, DocumentSnapshot snapshot) {
        String firstName = safe(snapshot.getString("firstName"));
        String lastName = safe(snapshot.getString("lastName"));
        String city = safe(snapshot.getString("city"));
        String email = safe(snapshot.getString("email"));

        String displayName = (firstName + " " + lastName).trim();
        if (displayName.isEmpty()) {
            displayName = entrantId;
        }

        String subtitle = !city.isEmpty() ? city : email;
        if (subtitle.isEmpty()) {
            subtitle = "Entrant ID: " + entrantId;
        }

        return new OrganizerWaitlistItem(entrantId, displayName, subtitle);
    }

    /**
     * Creates a fallback item when an entrant profile cannot be loaded.
     *
     * @param entrantId the identifier of the entrant whose profile is unavailable
     * @return a fallback waitlist item using placeholder display text
     */
    public static OrganizerWaitlistItem fallback(String entrantId) {
        return new OrganizerWaitlistItem(entrantId, entrantId, "Entrant profile unavailable");
    }

    /**
     * Returns the organizer-facing display name for the entrant.
     *
     * @return the formatted entrant display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the secondary text shown under the entrant name.
     *
     * @return the subtitle text for the waitlist row
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Computes the avatar label shown for the entrant.
     *
     * @return the first uppercased character of the display name, or {@code "?"} when unavailable
     */
    public String getAvatarLabel() {
        if (displayName == null || displayName.trim().isEmpty()) {
            return "?";
        }
        return displayName.trim().substring(0, 1).toUpperCase();
    }

    /**
     * Checks whether this waitlist item matches a search query.
     *
     * @param query the user-entered search text to compare against the row contents
     * @return {@code true} when the query matches the entrant id, display name, or subtitle;
     *         otherwise {@code false}
     */
    public boolean matches(String query) {
        String normalized = query == null ? "" : query.toLowerCase();
        return displayName.toLowerCase().contains(normalized)
                || subtitle.toLowerCase().contains(normalized)
                || entrantId.toLowerCase().contains(normalized);
    }

    /**
     * Converts a nullable string value into trimmed display text.
     *
     * @param value the raw string value to normalize
     * @return an empty string when the value is {@code null}; otherwise the trimmed value
     */
    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
