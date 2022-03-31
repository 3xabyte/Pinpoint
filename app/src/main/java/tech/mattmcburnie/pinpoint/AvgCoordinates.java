package tech.mattmcburnie.pinpoint;

public class AvgCoordinates {

    private long point;
    private String coordinates;
    private String date;
    private String type;
    private String landmark;

    public AvgCoordinates(long point, String coordinates, String date, String type, String landmark) {

        this.point = point;
        this.coordinates = coordinates;
        this.date = date;
        this.type = type;
        this.landmark = landmark;

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

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
