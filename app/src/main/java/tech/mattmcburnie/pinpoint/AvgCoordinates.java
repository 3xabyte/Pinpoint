package tech.mattmcburnie.pinpoint;

public class AvgCoordinates {

    private long point;
    private String coordinates;
    private String date;

    public AvgCoordinates(long point, String coordinates, String date) {

        this.point = point;
        this.coordinates = coordinates;
        this.date = date;

    }


    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public long getPoint() {
        return point;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
