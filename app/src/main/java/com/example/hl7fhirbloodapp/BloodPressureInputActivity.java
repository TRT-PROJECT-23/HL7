package com.example.hl7fhirbloodapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class BloodPressureInputActivity extends AppCompatActivity {

    private EditText systolicInput;
    private EditText diastolicInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_input);
        Button showDataButton = findViewById(R.id.showDataButton);

        showDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to display the input data here
                displayInputData();
            }
        });


        systolicInput = findViewById(R.id.editTextSystolic);
        diastolicInput = findViewById(R.id.editTextDiastolic);

        Button submitButton = findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's input for systolic and diastolic readings
                String systolic = systolicInput.getText().toString();
                String diastolic = diastolicInput.getText().toString();

                // Create a JSON object for the FHIR observation data
                try {
                    JSONObject observationData = new JSONObject();
                    observationData.put("resourceType", "Observation");
                    observationData.put("status", "final");

                    // Add other fields for category, code, subject, etc.

                    // Add the systolic and diastolic blood pressure components
                    JSONObject systolicComponent = new JSONObject();
                    systolicComponent.put("code", "8480-6");
                    systolicComponent.put("valueQuantity", createQuantityObject(systolic, "mm[Hg]"));

                    JSONObject diastolicComponent = new JSONObject();
                    diastolicComponent.put("code", "8462-4");
                    diastolicComponent.put("valueQuantity", createQuantityObject(diastolic, "mm[Hg]"));

                    JSONObject components = new JSONObject();
                    components.put("systolic", systolicComponent);
                    components.put("diastolic", diastolicComponent);
                    observationData.put("component", components);

                    // Send the observationData to the FHIR API
                    sendObservationToAPI(observationData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        Button saveButton = findViewById(R.id.saveButton);

// Set a click listener for the "Save" button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to save data to a file when the "Save" button is clicked
                //String inputData = observationData.toString(4); // Replace observationData with your JSON data
                //saveDataUsingMediaStore(inputData);

            }
        });


        Button readButton = findViewById(R.id.readButton);

// Set a click listener for the "Read" button
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to read and display data when the "Read" button is clicked
                //readDataUsingMediaStore();
            }
        });

    }

    private JSONObject createQuantityObject(String value, String unit) throws JSONException {
        JSONObject quantity = new JSONObject();
        quantity.put("value", value);
        quantity.put("unit", unit);
        quantity.put("system", "http://unitsofmeasure.org");
        quantity.put("code", "mm[Hg]");
        return quantity;
    }

    private void sendObservationToAPI(String observationData) {
        // Implement code to send the observation data to the FHIR API using Retrofit or HttpClient.
        // You should handle the API request and response here.
    }


    private void displayInputData() {
        try {
            // Create a JSON object for the FHIR observation data
            JSONObject observationData = new JSONObject();
            observationData.put("resourceType", "Observation");
            observationData.put("status", "final");

            // Create the "category" field
            JSONObject category = new JSONObject();
            JSONArray categoryCoding = new JSONArray();
            JSONObject categoryCodingItem = new JSONObject();
            categoryCodingItem.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
            categoryCodingItem.put("code", "vital-signs");
            categoryCoding.put(categoryCodingItem);
            category.put("coding", categoryCoding);
            JSONArray categoryArray = new JSONArray();
            categoryArray.put(category);
            observationData.put("category", categoryArray);

            // Create the "code" field for blood pressure
            JSONObject code = new JSONObject();
            JSONArray codeCoding = new JSONArray();
            JSONObject codeCodingItem = new JSONObject();
            codeCodingItem.put("system", "http://loinc.org");
            codeCodingItem.put("code", "55284-4");
            codeCodingItem.put("display", "Blood pressure systolic & diastolic");
            codeCoding.put(codeCodingItem);
            code.put("coding", codeCoding);
            observationData.put("code", code);

            // Create the "subject" field
            JSONObject subject = new JSONObject();
            JSONObject identifier = new JSONObject();
            identifier.put("system", "urn:oid:1.2.246.21");
            identifier.put("value", "010101-123N");
            subject.put("identifier", identifier);
            observationData.put("subject", subject);

            // Add systolic and diastolic values
            String systolic = systolicInput.getText().toString();
            String diastolic = diastolicInput.getText().toString();

            JSONObject systolicComponent = new JSONObject();
            systolicComponent.put("code", "8480-6");
            systolicComponent.put("valueQuantity", createQuantityObject(systolic, "mm[Hg]"));

            JSONObject diastolicComponent = new JSONObject();
            diastolicComponent.put("code", "8462-4");
            diastolicComponent.put("valueQuantity", createQuantityObject(diastolic, "mm[Hg]"));

            JSONObject components = new JSONObject();
            components.put("systolic", systolicComponent);
            components.put("diastolic", diastolicComponent);
            observationData.put("component", components);

            // Pretty-print the JSON
            String inputData = observationData.toString(4);

            // Display the JSON data on the phone screen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Blood Pressure Data");
            builder.setMessage(inputData);
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();

            // Log the JSON data to Logcat
            Log.d("JSON Data", inputData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveDataUsingMediaStore(String data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "blood_pressure_data.json");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/json");
        Uri contentUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }

        assert contentUri != null;
        Uri itemUri = getContentResolver().insert(contentUri, contentValues);

        try {
            if (itemUri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(itemUri);
                if (outputStream != null) {
                    outputStream.write(data.getBytes());
                    outputStream.close();
                    Toast.makeText(this, "Data saved using MediaStore", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving data using MediaStore", Toast.LENGTH_SHORT).show();
        }
    }

    private String readDataUsingMediaStore() {
        String data = "";
        Uri contentUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            try {
                FileInputStream inputStream = new FileInputStream(filePath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                data = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error reading data using MediaStore", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        }

        return data;
    }


}
