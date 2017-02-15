package neighbor.com.mbis.MapUtil;

/**
 * Created by user on 2016-09-21.
 */
public class HandlerPosition {

    public final static int BUSTIMER_30SEC = 1000 * 30;
    public final static int SERVER_CONNECT_TIMEOUT = 3000 / 2;
    public final static int SERVER_READ_TIMEOUT = 1000 * 60 * 5;


    public final static int TIME_CHANGE = 1;
    public final static int SEND_BUS_LOCATION_INFO = 2;

    public final static int SOCKET_CONNECT_ERROR = 11;
    public final static int READ_SERVER_DISCONNECT_ERROR = 12;
    public final static int WRITE_SERVER_DISCONNECT_ERROR = 13;
    public final static int READ_TIMEOUT_ERROR = 14;
    public final static int READ_DATA_ERROR = 15;

    public final static int SOCKET_CONNECT_SUCCESS = 100;
    public final static int DATA_READ_SUCESS = 101;

    public final static int SPLASH_LOCATION_RANGE = 30;
}
