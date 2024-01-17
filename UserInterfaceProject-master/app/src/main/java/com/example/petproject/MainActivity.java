package com.example.petproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private SearchView searchView;

    // Map to store notes for each country
    private Map<String, String> countryNotesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        setupSearchView();

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
        // Load existing notes from file when the app starts
        loadNotesFromFile();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomAppBar);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;

                case R.id.map:
                    return true;
                case R.id.list:
                    startActivity(new Intent(getApplicationContext(), ListOfCountriesActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

                    finish();
                    return true;
            }
            return false;
        });
    }

    private void setupSearchView() {
        searchView = findViewById(R.id.searchView);

        // Set up search action
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query submission
                // Move the map to the location of the searched country
                moveMapToSearchedCountry(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle changes in the search query text
                // Update suggestions or perform real-time filtering
                return true;
            }
        });
    }



    private void moveMapToSearchedCountry(String countryName) {
        Geocoder geocoder = new Geocoder(this);

        try {
            // Get the list of addresses for the given country name
            List<Address> addresses = geocoder.getFromLocationName(countryName, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                LatLng location = new LatLng(latitude, longitude);

                // Move the map to the location of the searched country
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                addMarker(location, countryTextView.getText().toString());
            } else {
                // Handle case when no address is found for the given country name
                // You can show a toast or log a message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);
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
            saveNotesToFile(); // Save notes to file after adding
        }
    }
    private void updateNotesTextView() {
        StringBuilder notesBuilder = new StringBuilder("Notes:\n");
        for (Map.Entry<String, String> entry : countryNotesMap.entrySet()) {
            notesBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        TextView notesTextView = findViewById(R.id.notesTextView);
        notesTextView.setText(notesBuilder.toString());
        saveNotesToFile();
    }
    private void saveNotesToFile() {
        try {
            File file = new File(getExternalFilesDir(null), "notes.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (Map.Entry<String, String> entry : countryNotesMap.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNotesFromFile() {
        try {
            File file = new File(getExternalFilesDir(null), "notes.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    countryNotesMap.put(parts[0], parts[1]);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onGalleryButtonClick(View view) {

    }
}
