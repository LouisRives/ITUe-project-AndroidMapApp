package com.example.petproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.location.Address;
import android.location.Geocoder;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap googleMap;
    private CardView infoCardView;
    private TextView countryTextView;
    private EditText emptyTextBox;
    private Button addButton;

    // Map to store notes for each country
    private Map<String, String> countryNotesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        infoCardView = findViewById(R.id.infoCardView);
        countryTextView = findViewById(R.id.countryTextView);
        emptyTextBox = findViewById(R.id.emptyTextBox);
        addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNoteForCountry();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);

        // Example marker for demonstration
        LatLng markerPosition = new LatLng(0, 0);
        googleMap.addMarker(new MarkerOptions().position(markerPosition).title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Implement logic to determine the country based on the clicked coordinates
        String countryName = getCountryName(latLng.latitude, latLng.longitude);

        if (!countryName.equals("Unknown")) {
            showInfoWindow(countryName);
            addMarker(latLng, countryName);
        } else {
            hideInfoWindow();
        }
    }

    private void addMarker(LatLng latLng, String title) {
        if (googleMap != null) {
            googleMap.clear(); // Clear existing markers
            googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
    }

    private String getCountryName(double latitude, double longitude) {
        // Use Geocoding API to get the country name based on coordinates
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String countryName = address.getCountryName();
                if (countryName != null && !countryName.isEmpty()) {
                    return countryName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Handle case when country name is not found
        return "Unknown";
    }

    private void showInfoWindow(String countryName) {
        countryTextView.setText(countryName);
        emptyTextBox.setText(""); // Clear the text box
        infoCardView.setVisibility(View.VISIBLE);
    }

    private void hideInfoWindow() {
        infoCardView.setVisibility(View.GONE);
    }

    private void addNoteForCountry() {
        String countryName = countryTextView.getText().toString();
        String note = emptyTextBox.getText().toString();

        // Check if the note is not empty before adding
        if (!note.isEmpty()) {
            countryNotesMap.put(countryName, note);
            updateNotesTextView();
        }
    }

    private void updateNotesTextView() {
        StringBuilder notesBuilder = new StringBuilder("Notes:\n");
        for (Map.Entry<String, String> entry : countryNotesMap.entrySet()) {
            notesBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        TextView notesTextView = findViewById(R.id.notesTextView);
        notesTextView.setText(notesBuilder.toString());
    }
    
}
