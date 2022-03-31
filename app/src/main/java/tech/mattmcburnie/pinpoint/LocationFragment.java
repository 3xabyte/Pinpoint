package tech.mattmcburnie.pinpoint;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Locale;

public class LocationFragment extends Fragment {

    private final String ACTIVITY_NAME = "LocationFragment";

    private Button locationButton;
    private MainActivity activity;
    private TextView progress;
    private CircularProgressIndicator progressBar;
    private Spinner dropdownMenu;
    private EditText textbox;

    private CoordinatesDatabase db;
    private long current_point;
    private int count = 0;
    private int maxCount;
    private boolean running = false;
    private String pointType = "";
    private String landmark = "N/A";
    private boolean terminate = false;
    private boolean continuous;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.location_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        locationButton = view.findViewById(R.id.location_button);
        progress = view.findViewById(R.id.progress);
        progressBar = view.findViewById(R.id.progress_bar);
        dropdownMenu = view.findViewById(R.id.point_type_menu);
        textbox = view.findViewById(R.id.point_landmark_textbox);
        continuous = activity.getMode();

        this.initializeListeners();

    }

    private void initializeListeners() {

        // Get Location Button
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!running) {
                    db = new CoordinatesDatabase(activity);
                    current_point = db.getCurrentPoint();
                    Log.i(ACTIVITY_NAME, String.format("Current point: %d", current_point));

                    if(!continuous) {
                        count = 0;
                        maxCount = activity.getMaxPoints();

                        progress.setText(String.format(Locale.CANADA, "%d/%d", count, maxCount));


                    }
                    else {
                        progress.setText("Running (0)");
                        locationButton.setText("Stop");
                    }


                    progress.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0, true);

                    dropdownMenu.setEnabled(false);
                    textbox.setEnabled(false);


                    new LocationFragment.Location();
                }

                else if(continuous) {
                    terminate = true;
                }

                else {
                    Toast.makeText(activity, "Please wait for the app to finish gathering coordinates!", Toast.LENGTH_LONG).show();
                }


            }
        });

        // Dropdown Menu
        ArrayAdapter<CharSequence> pointAdapter = ArrayAdapter.createFromResource(activity, R.array.point_types, R.layout.text_resource);
        pointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(pointAdapter);

        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pointType = dropdownMenu.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Text Box
        textbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals(""))
                    landmark = "N/A";
                else
                    landmark = charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }

    private class Location {

        private double latitude;
        private double longitude;

        public Location() {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

            // Checks if the app has the required permissions. If not, ask the user for the permissions.
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            // Get the coordinates using the requestLocationUpdates method
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull android.location.Location location) {
                    running = true;
                    count++;

                    // Get Coordinates
                    latitude = Double.parseDouble(String.valueOf(location.getLatitude()));
                    longitude = Double.parseDouble(String.valueOf(location.getLongitude()));

                    // Formatting and adding to database, depending on mode.
                    if(!continuous) {
                        progressBar.setProgress(count, true);
                        progress.setText(String.format(Locale.CANADA, "%d/%d", count, maxCount));
                        progressBar.setProgress((int) (count / (double) maxCount * 100), true);

                        db.addNewCoordinates(current_point, latitude, longitude, pointType, landmark);
                    }
                    else {
                        progress.setText(String.format(Locale.CANADA, "Running (%d)", count));
                        db.addNewCoordinates(current_point, latitude, longitude, "Point", landmark);
                    }

                    // Terminate location updates
                    if((!continuous && count == maxCount) || (continuous && terminate)) {
                        locationManager.removeUpdates(this);
                        progress.setText("Complete!");
                        running = false;

                        dropdownMenu.setEnabled(true);
                        textbox.setEnabled(true);

                        if(continuous) {
                            locationButton.setText("Get Location");
                            progressBar.setProgress(100, true);
                            terminate = false;
                        }


                    }


                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });

        }


    }




}