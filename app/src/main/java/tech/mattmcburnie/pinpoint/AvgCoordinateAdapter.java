package tech.mattmcburnie.pinpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class AvgCoordinateAdapter extends ArrayAdapter<AvgCoordinates> {

    private Context context;
    private int resource;

    public AvgCoordinateAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AvgCoordinates> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        long point = getItem(position).getPoint();
        String coords = getItem(position).getCoordinates();
        String date = getItem(position).getDate();
        String type = getItem(position).getType();
        String landmark = getItem(position).getLandmark();

        AvgCoordinates coordinates = new AvgCoordinates(point, coords, date, type, landmark);

        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(this.resource, parent, false);

        TextView pt = (TextView) convertView.findViewById(R.id.point);
        TextView latlong = (TextView) convertView.findViewById(R.id.coordinates);
        TextView dayAndTime = (TextView) convertView.findViewById(R.id.date);

        if(!landmark.equals("N/A"))
            pt.setText(String.format(Locale.CANADA, "Point %s (%s)", landmark, type));
        else
            pt.setText(String.format(Locale.CANADA, "Point %s (%s)", point, type));
        latlong.setText(coords);
        dayAndTime.setText(date);

        return convertView;
    }
}
