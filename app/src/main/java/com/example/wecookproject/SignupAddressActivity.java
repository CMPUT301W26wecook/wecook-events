package com.example.wecookproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupAddressActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_address);

        ImageView backButton = findViewById(R.id.iv_back);
        Button continueButton = findViewById(R.id.btn_continue);
        EditText etAddressLine1 = findViewById(R.id.et_address_line_1);
        EditText etCity = findViewById(R.id.et_city);
        EditText etPostalCode = findViewById(R.id.et_postal_code);

        backButton.setOnClickListener(v -> finish());

        continueButton.setOnClickListener(v -> {
            String addressLine1 = etAddressLine1.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String postalCode = etPostalCode.getText().toString().trim();
            if (addressLine1.isEmpty() || city.isEmpty() || postalCode.isEmpty()) {
                Toast.makeText(this, "Address line 1, City, and Postal code cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            // Navigate immediately — Firestore write happens in the background
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Map<String, Object> userData = new HashMap<>();
            userData.put("addressLine1", addressLine1);
            userData.put("city", city);
            userData.put("postalCode", postalCode);
            userData.put("profileCompleted", true);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(androidId).set(userData)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
                });

            // Go to MainActivity right away without waiting for Firestore
            Intent intent = new Intent(SignupAddressActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
