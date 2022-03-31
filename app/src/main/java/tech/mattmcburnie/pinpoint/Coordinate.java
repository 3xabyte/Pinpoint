package tech.mattmcburnie.pinpoint;

public class Coordinate {

    private int id;
    private double latitude;
    private double longitude;
    private String pointType;
    private String landmark;

    public Coordinate(int id, double latitude, double longitude, String pointType, String landmark) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointType = pointType;
        this.landmark = landmark;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
