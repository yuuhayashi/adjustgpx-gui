package osm.jp.gpx;

public class Expecter {
    String value;
    boolean expect;
    String timeStr;
    double latD;
    double lonD;
    boolean magvar;

    public Expecter(String value, boolean expect, String timeStr, double latD, double lonD, boolean magvar) {
        this.value = value;
        this.expect = expect;
        this.timeStr = timeStr;
        this.latD = latD;
        this.lonD = lonD;
        this.magvar = magvar;
    }

}
