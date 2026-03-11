package com.example.wecookproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrganizerEditEventActivity extends AppCompatActivity {
    private Date registrationStartDate;
    private Date registrationEndDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_edit_event);

        TextInputEditText etRegistrationStartDate = findViewById(R.id.et_registration_start_date);
        TextInputEditText etRegistrationEndDate = findViewById(R.id.et_registration_end_date);

        // Set up date picker for start date
        etRegistrationStartDate.setOnClickListener(v -> showStartDatePicker(etRegistrationStartDate));
        
        // Set up date picker for end date
        etRegistrationEndDate.setOnClickListener(v -> showEndDatePicker(etRegistrationEndDate));

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

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.btn_update_event).setOnClickListener(v -> {
            // TODO: validate fields and update event
        });
    }

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
}
