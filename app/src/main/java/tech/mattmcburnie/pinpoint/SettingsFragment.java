package tech.mattmcburnie.pinpoint;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    SeekBar seekbar;
    TextView pointsLabel;
    MainActivity activity;
    Button exportButton;
    Button exportAllButton;
    Button deleteButton;
    SharedPreferences prefs;
    SwitchCompat darkMode;
    SwitchCompat recordingMode;
    TextView recordingLabel;

    boolean darkModeEnabled = false;
    boolean rMode = false;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize page elements
        activity = (MainActivity) getActivity();

        seekbar = view.findViewById(R.id.recorded_points_seek_bar);
        pointsLabel = view.findViewById(R.id.recorded_points_amount);
        exportButton = view.findViewById(R.id.export_button);
        exportAllButton = view.findViewById(R.id.export_all_button);
        deleteButton = view.findViewById(R.id.delete_button);
        darkMode = view.findViewById(R.id.dark_mode_switch);
        recordingMode = view.findViewById(R.id.mode_switch);
        recordingLabel = view.findViewById(R.id.recording_mode_label);

        // Get the number of points to record
        prefs = activity.getPreferences(Context.MODE_PRIVATE);
        int progress = prefs.getInt("PointsRecorded", 0);
        seekbar.setProgress(progress);

        pointsLabel.setText(String.format(Locale.CANADA, "%d", activity.convertMaxPoints(progress)));

        // Get dark mode preferences
        darkModeEnabled = prefs.getBoolean("DarkMode", true);
        darkMode.setChecked(darkModeEnabled);

        rMode = activity.getMode();
        recordingMode.setChecked(rMode);
        if(rMode)
            recordingLabel.setText("Continuous");
        else
            recordingLabel.setText("Set Amount");

        this.initializeListeners();

    }

    private void initializeListeners() {

        // Seekbar
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("PointsRecorded", i);
                editor.apply();

                int progress = activity.convertMaxPoints(i);

                pointsLabel.setText(String.format(Locale.CANADA, "%d", progress));
                activity.setMaxPoints(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Export Button
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Checks if the app has the required permissions. If not, ask the user for the permissions.
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }

                CoordinatesDatabase db = new CoordinatesDatabase(activity);
                ArrayList<Long> points = db.getPoints();

                StringBuilder output = new StringBuilder();
                output.append("Point,Average Latitude,Average Longitude,Point Type,Landmark\n");
                double[] coordinates;


                for(long i : points) {
                    coordinates = db.getAvgCoordsAtPoint(i);
                    output.append(String.format(Locale.CANADA, "%s,%.8f,%.8f\n", i, coordinates[0], coordinates[1]));
                }

                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

                try {
                    File txt = new File(dir, "pinpoint_averages.csv");
                    FileOutputStream fos = new FileOutputStream(txt);
                    fos.write(output.toString().getBytes());
                    fos.close();
                    Toast.makeText(activity, "Average points exported to pinpoint_averages.csv", Toast.LENGTH_SHORT).show();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                db.close();


            }
        });

        // Delete Button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("You are able to delete all of the points, are you sure you want to continue?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CoordinatesDatabase db = new CoordinatesDatabase(activity);
                        db.deleteTable();
                        db.close();
                        Toast.makeText(activity, "All points have been deleted!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();

            }
        });

        // Export All Button
        exportAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checks if the app has the required permissions. If not, ask the user for the permissions.
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }

                CoordinatesDatabase db = new CoordinatesDatabase(activity);
                StringBuilder output = new StringBuilder();
                output.append("ID,Latitude,Longitude,Point Type,Landmark\n");

                ArrayList<Coordinate> allPoints = db.getAllPoints();

                for(Coordinate x : allPoints) {
                    output.append(String.format(Locale.CANADA, "%s,%.8f,%.8f,%s,%s\n", x.getId(), x.getLatitude(), x.getLongitude(), x.getPointType(), x.getLandmark()));
                }

                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

                try {
                    File txt = new File(dir, "pinpoint_all_points.csv");
                    FileOutputStream fos = new FileOutputStream(txt);
                    fos.write(output.toString().getBytes());
                    fos.close();
                    Toast.makeText(activity, "All points exported to pinpoint_all_points.csv", Toast.LENGTH_SHORT).show();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                db.close();


            }
        });

        // Dark Mode
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(darkMode.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("DarkMode", b);
                editor.apply();

                // Refreshes the current fragment
                activity.getSupportFragmentManager().beginTransaction().detach(SettingsFragment.this);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment());

            }
        });



        // Recording Mode
        recordingMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    recordingLabel.setText("Continuous");
                else
                    recordingLabel.setText("Set Amount");

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("RecordingMode", b);
                editor.apply();

                activity.setMode(b);
            }
        });


    }

}
