package tech.mattmcburnie.pinpoint;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class IndividualPointFragment extends Fragment {

    MainActivity activity;
    ListView points;
    TextView avgLatitude;
    TextView avgLongitude;
    long pt;

    ArrayList<Coordinate> coordinates;

    CoordinatesDatabase db;
    double[] avgCoordinates;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_database_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();

        points = view.findViewById(R.id.database_coordinate_list);
        avgLatitude = view.findViewById(R.id.latitude);
        avgLongitude = view.findViewById(R.id.longitude);


        db = new CoordinatesDatabase(activity);

        ArrayList<Long> pts = db.getPoints();
        pt = pts.get(activity.getPoint());

        avgCoordinates = db.getAvgCoordsAtPoint(pt);

        this.setInformation();
        this.initializeListeners();
        db.close();

    }

    private void setInformation() {
        avgLatitude.setText(String.format(Locale.CANADA, "Latitude: %.8f", avgCoordinates[0]));
        avgLongitude.setText(String.format(Locale.CANADA, "Longitude: %.8f", avgCoordinates[1]));

        coordinates = db.getCoordinatesAtPoint(pt);

        CoordinateAdapter adapter = new CoordinateAdapter(activity, R.layout.coordinate_adapter_view, coordinates);
        points.setAdapter(adapter);

    }

    private void initializeListeners() {

        points.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        points.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("You are able to delete this point, are you sure you want to continue?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int o) {

                        // Get ID of the point, then delete it
                        int id = coordinates.get(i).getId();
                        CoordinatesDatabase db = new CoordinatesDatabase(activity);
                        db.deletePointID(id);
                        coordinates.remove(i);

                        // Update averages
                        avgCoordinates = db.getAvgCoordsAtPoint(activity.getPoint());
                        avgLatitude.setText(String.format(Locale.CANADA, "Latitude: %.8f", avgCoordinates[0]));
                        avgLongitude.setText(String.format(Locale.CANADA, "Longitude: %.8f", avgCoordinates[1]));

                        db.close();

                        // Update the ListView
                        CoordinateAdapter adapter = new CoordinateAdapter(activity, R.layout.coordinate_adapter_view, coordinates);
                        points.setAdapter(adapter);


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });

                builder.show();



                return true;
            }
        });

    }


}
