package neighbor.com.mbis.util.csv;

/**
 * Created by user on 2016-08-29.
 */
public class RouteUtil {

    private String route_id;
    private String route_name;
    private int route_form;
    private int route_type;
    private int route_first_start_time;

    private int route_last_start_time;
    private int route_average_interval;
    private int route_average_time;
//    private int route_length;
    private float route_length;
    private int route_station_num;
    private String route_start_station;
    private String route_important_station1;
    private String route_important_station2;
    private String route_last_station;

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public int getRoute_form() {
        return route_form;
    }

    public void setRoute_form(int route_form) {
        this.route_form = route_form;
    }

    public int getRoute_type() {
        return route_type;
    }

    public void setRoute_type(int route_type) {
        this.route_type = route_type;
    }

    public int getRoute_first_start_time() {
        return route_first_start_time;
    }

    public void setRoute_first_start_time(int route_first_start_time) {
        this.route_first_start_time = route_first_start_time;
    }

    public int getRoute_last_start_time() {
        return route_last_start_time;
    }

    public void setRoute_last_start_time(int route_last_start_time) {
        this.route_last_start_time = route_last_start_time;
    }

    public int getRoute_average_interval() {
        return route_average_interval;
    }

    public void setRoute_average_interval(int route_average_interval) {
        this.route_average_interval = route_average_interval;
    }

    public int getRoute_average_time() {
        return route_average_time;
    }

    public void setRoute_average_time(int route_average_time) {
        this.route_average_time = route_average_time;
    }

    public float getRoute_length() {
        return route_length;
    }

    public void setRoute_length(float route_length) {
        this.route_length = route_length;
    }

    public int getRoute_station_num() {
        return route_station_num;
    }

    public void setRoute_station_num(int route_station_num) {
        this.route_station_num = route_station_num;
    }

    public String getRoute_start_station() {
        return route_start_station;
    }

    public void setRoute_start_station(String route_start_station) {
        this.route_start_station = route_start_station;
    }

    public String getRoute_important_station1() {
        return route_important_station1;
    }

    public void setRoute_important_station1(String route_important_station1) {
        this.route_important_station1 = route_important_station1;
    }

    public String getRoute_important_station2() {
        return route_important_station2;
    }

    public void setRoute_important_station2(String route_important_station2) {
        this.route_important_station2 = route_important_station2;
    }

    public String getRoute_last_station() {
        return route_last_station;
    }

    public void setRoute_last_station(String route_last_station) {
        this.route_last_station = route_last_station;
    }
}
