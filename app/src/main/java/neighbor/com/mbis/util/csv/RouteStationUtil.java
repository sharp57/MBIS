package neighbor.com.mbis.util.csv;

/**
 * Created by user on 2016-08-29.
 */
public class RouteStationUtil {
    private String route_id;
    private int route_form;
    private String station_id;
    private int station_order;
//    private int station_distance;
    private float station_distance;
    private int station_time;


    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public int getRoute_form() {
        return route_form;
    }

    public void setRoute_form(int route_form) {
        this.route_form = route_form;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public int getStation_order() {
        return station_order;
    }

    public void setStation_order(int station_order) {
        this.station_order = station_order;
    }

    public float getStation_distance() {
        return station_distance;
    }

    public void setStation_distance(float station_distance) {
        this.station_distance = station_distance;
    }

    public int getStation_time() {
        return station_time;
    }

    public void setStation_time(int station_time) {
        this.station_time = station_time;
    }
}
