package com.example.wecookproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for organizers to edit an existing event's configurable details and optionally replace
 * its poster image. Within the app it acts as the UI controller for the organizer edit-event flow,
 * coordinating form input, validation, and Firebase persistence in a single screen.
 *
 * Outstanding issues:
 * - Existing event fields are not preloaded into the form, so the screen currently behaves like a
 *   partial-update form rather than a full edit view.
 * - Firestore and Storage access are handled directly in the Activity, which is not implemented yet,
 *   as connecting to Firebase storage require addition setup that might incur extra costs.
 */
public class OrganizerEditEventActivity extends AppCompatActivity {
    private Date registrationStartDate;
    private Date registrationEndDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String eventId;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private TextInputLayout tilEventName;
    private TextInputLayout tilRegistrationStartDate;
    private TextInputLayout tilRegistrationEndDate;
    private TextInputLayout tilMaxWaitlist;
    private TextInputEditText etEventName;
    private TextInputEditText etRegistrationStartDate;
    private TextInputEditText etRegistrationEndDate;
    private TextInputEditText etMaxWaitlist;
    private String originalPosterUrl;
    private String pendingPosterUrl;
    private boolean posterCommitted;
    private ActivityResultLauncher<String> posterPickerLauncher;

    /**
     * Initializes the organizer event editing screen and its form bindings.
     *
     * @param savedInstanceState the previously saved instance state, or {@code null} when the
     *                           activity is created for the first time
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_edit_event);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tilEventName = findViewById(R.id.til_event_name);
        tilRegistrationStartDate = findViewById(R.id.til_registration_start_date);
        tilRegistrationEndDate = findViewById(R.id.til_registration_end_date);
        tilMaxWaitlist = findViewById(R.id.til_max_waitlist);
        etEventName = findViewById(R.id.et_event_name);
        etRegistrationStartDate = findViewById(R.id.et_registration_start_date);
        etRegistrationEndDate = findViewById(R.id.et_registration_end_date);
        etMaxWaitlist = findViewById(R.id.et_max_waitlist);
        FrameLayout flPosterUpload = findViewById(R.id.fl_poster_upload);

        posterPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handlePosterSelection
        );

        // Opens the start-date picker when the organizer taps the start-date field.
        etRegistrationStartDate.setOnClickListener(v -> showStartDatePicker(etRegistrationStartDate));
        // Opens the end-date picker when the organizer taps the end-date field.
        etRegistrationEndDate.setOnClickListener(v -> showEndDatePicker(etRegistrationEndDate));

        loadCurrentPosterUrl();
        // Launches the system picker so the organizer can choose a replacement poster image.
        flPosterUpload.setOnClickListener(v -> posterPickerLauncher.launch("image/*"));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                startActivity(new Intent(this, OrganizerHomeActivity.class));
                return true;
            } else if (id == R.id.nav_create_events) {
                startActivity(new Intent(this, OrganizerCreateEventActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, OrganizerProfileActivity.class));
                return true;
            }
            return true;
        });

        // Closes the screen and discards any uncommitted poster upload.
        findViewById(R.id.iv_back).setOnClickListener(v -> cancelAndExit());
        // Cancels event editing and exits without saving pending changes.
        findViewById(R.id.btn_cancel).setOnClickListener(v -> cancelAndExit());
        // Validates and saves the organizer's event edits.
        findViewById(R.id.btn_update_event).setOnClickListener(v -> updateEvent());

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            /**
             * Handles system back navigation by cleaning up unsaved poster uploads first.
             *
             * @return no value
             */
            @Override
            public void handleOnBackPressed() {
                cancelAndExit();
            }
        });
    }

    /**
     * Shows a date picker for the registration start date field.
     *
     * @param editText the input field that should receive the chosen start date
     * @return no value
     */
    private void showStartDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                registrationStartDate = calendar.getTime();
                editText.setText(dateFormat.format(registrationStartDate));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    /**
     * Shows a date picker for the registration end date field.
     *
     * @param editText the input field that should receive the chosen end date
     * @return no value
     */
    private void showEndDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        if (registrationStartDate != null) {
            calendar.setTime(registrationStartDate);
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                registrationEndDate = calendar.getTime();
                editText.setText(dateFormat.format(registrationEndDate));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to start date if selected
        if (registrationStartDate != null) {
            datePickerDialog.getDatePicker().setMinDate(registrationStartDate.getTime());
        }
        
        datePickerDialog.show();
    }

    /**
     * Validates the edit form and persists any changed event fields to Firestore.
     *
     * @return no value
     */
    private void updateEvent() {
        tilEventName.setError(null);
        tilRegistrationStartDate.setError(null);
        tilRegistrationEndDate.setError(null);
        tilMaxWaitlist.setError(null);

        String eventName = getTrimmedText(etEventName);
        String maxWaitlistText = getTrimmedText(etMaxWaitlist);

        boolean hasError = false;
        Map<String, Object> updates = new HashMap<>();

        if (!TextUtils.isEmpty(eventName)) {
            updates.put("eventName", eventName);
        }

        if (registrationStartDate != null) {
            updates.put("registrationStartDate", registrationStartDate);
        }

        if (registrationEndDate != null) {
            updates.put("registrationEndDate", registrationEndDate);
        }

        if (!TextUtils.isEmpty(maxWaitlistText)) {
            try {
                int maxWaitlist = Integer.parseInt(maxWaitlistText);
                if (maxWaitlist <= 0) {
                    tilMaxWaitlist.setError("Maximum waitlist must be greater than 0");
                    hasError = true;
                } else {
                    updates.put("maxWaitlist", maxWaitlist);
                }
            } catch (NumberFormatException e) {
                tilMaxWaitlist.setError("Enter a valid number");
                hasError = true;
            }
        }

        if (!TextUtils.isEmpty(pendingPosterUrl)) {
            updates.put("posterPath", pendingPosterUrl);
        }

        if (hasError) {
            return;
        }

        if (updates.isEmpty()) {
            Toast.makeText(this, "Enter at least one valid field to update", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events")
                .document(eventId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    if (!TextUtils.isEmpty(pendingPosterUrl)) {
                        String replacedPosterUrl = originalPosterUrl;
                        originalPosterUrl = pendingPosterUrl;
                        pendingPosterUrl = null;
                        posterCommitted = true;
                        deleteStorageFile(replacedPosterUrl);
                    }
                    Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show());
    }

    /**
     * Validates and uploads a newly selected poster image for the event.
     *
     * @param imageUri the content URI of the selected poster image
     * @return no value
     */
    private void handlePosterSelection(Uri imageUri) {
        if (imageUri == null) {
            return;
        }

        String mimeType = getContentResolver().getType(imageUri);
        if (!isValidPosterMimeType(mimeType)) {
            Toast.makeText(this, "Only JPG, JPEG, and PNG images are allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (TextUtils.isEmpty(extension)) {
            extension = "jpg";
        }

        StorageReference posterRef = storage.getReference()
                .child("event_posters")
                .child(eventId)
                .child(UUID.randomUUID() + "." + extension);

        posterRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return posterRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    String previousPendingPoster = pendingPosterUrl;
                    pendingPosterUrl = downloadUri.toString();
                    posterCommitted = false;
                    deleteStorageFile(previousPendingPoster);
                    Toast.makeText(this, "Poster uploaded. Tap Update Event to save it.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to upload poster", Toast.LENGTH_SHORT).show());
    }

    /**
     * Checks whether the selected poster MIME type is allowed.
     *
     * @param mimeType the MIME type reported for the selected image
     * @return {@code true} when the MIME type is a supported poster image format; otherwise
     *         {@code false}
     */
    private boolean isValidPosterMimeType(String mimeType) {
        return "image/jpeg".equalsIgnoreCase(mimeType)
                || "image/jpg".equalsIgnoreCase(mimeType)
                || "image/png".equalsIgnoreCase(mimeType);
    }

    /**
     * Loads the currently saved poster URL for the event being edited.
     *
     * @return no value
     */
    private void loadCurrentPosterUrl() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    originalPosterUrl = documentSnapshot.getString("posterPath");
                    if (TextUtils.isEmpty(originalPosterUrl)) {
                        originalPosterUrl = documentSnapshot.getString("posterUrl");
                    }
                });
    }

    /**
     * Cancels the edit flow and deletes any uncommitted uploaded poster file.
     *
     * @return no value
     */
    private void cancelAndExit() {
        if (!posterCommitted && !TextUtils.isEmpty(pendingPosterUrl)) {
            deleteStorageFile(pendingPosterUrl);
            pendingPosterUrl = null;
        }
        finish();
    }

    /**
     * Deletes a file from Firebase Storage when a valid download URL is available.
     *
     * @param fileUrl the Firebase Storage download URL of the file to delete
     * @return no value
     */
    private void deleteStorageFile(String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }

        try {
            storage.getReferenceFromUrl(fileUrl)
                    .delete()
                    .addOnFailureListener(e -> { });
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Reads and trims the text contents of a material text field.
     *
     * @param editText the input field whose current text should be normalized
     * @return the trimmed text value, or an empty string when the field has no text
     */
    private String getTrimmedText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

}
