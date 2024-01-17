package com.example.petproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ListOfCountriesActivity extends AppCompatActivity {
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] countries = {"Usa", "Ukraine", "Turkey", "Austria", "Czech Republic"};


        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_countries);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item, countries);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);


        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String countries = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(ListOfCountriesActivity.this, "country" + countries, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
