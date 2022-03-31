package tech.mattmcburnie.pinpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class CoordinateAdapter extends ArrayAdapter<Coordinate> {

    private Context context;
    private int resource;

    public CoordinateAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Coordinate> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int id = getItem(position).getId();
        double latitude = getItem(position).getLatitude();
        double longitude = getItem(position).getLongitude();
        String pointType = getItem(position).getPointType();
        String landmark = getItem(position).getLandmark();

        Coordinate c = new Coordinate(id, latitude, longitude, pointType, landmark);

        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(this.resource, parent, false);

        TextView idLabel = (TextView) convertView.findViewById(R.id.id);
        TextView latLabel = (TextView) convertView.findViewById(R.id.latitude);
        TextView longLabel = (TextView) convertView.findViewById(R.id.longitude);

        if(!landmark.equals("N/A"))
            idLabel.setText(String.format(Locale.CANADA, "%s (%s)", landmark, pointType));
        else
            idLabel.setText(String.format(Locale.CANADA, "%d (%s)", id, pointType));

        latLabel.setText(String.format(Locale.CANADA, "%.8f", latitude));
        longLabel.setText(String.format(Locale.CANADA, "%.8f", longitude));

        return convertView;

    }
}
