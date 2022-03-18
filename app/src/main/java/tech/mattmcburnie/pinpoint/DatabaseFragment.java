package tech.mattmcburnie.pinpoint;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseFragment extends Fragment {

    ListView lv;
    MainActivity activity;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.database_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();

        lv = view.findViewById(R.id.database_list);
        this.view = view;

        this.initializeListeners();

    }

    private void initializeListeners() {

        CoordinatesDatabase db = new CoordinatesDatabase(activity);

        ArrayList<AvgCoordinates> list = new ArrayList<>();

        long maxPoint = db.getCurrentPoint();
        double[] averageCoordinates;

        if(maxPoint == 1) {
            Toast.makeText(activity, "Looks like there are no points, try recording some!", Toast.LENGTH_LONG).show();
        }
        else {
            ArrayList<Long> points = db.getPoints();
            for(long i : points) {
                String date = db.getDateAtPoint(i);
                averageCoordinates = db.getAvgCoordsAtPoint(i);
                list.add(new AvgCoordinates(i, String.format(Locale.CANADA, "%.8f, %.8f", averageCoordinates[0], averageCoordinates[1]), date));
            }

        }


        AvgCoordinateAdapter adapter = new AvgCoordinateAdapter(activity, R.layout.average_adapter_view, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                activity.setPoint(i);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IndividualPointFragment()).commit();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(activity, "Long press", Toast.LENGTH_SHORT).show();

                return true;
            }
        });


    }
}
