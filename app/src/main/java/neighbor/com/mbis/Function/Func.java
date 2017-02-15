package neighbor.com.mbis.Function;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Formatter;

/**
 * Created by user on 2016-08-31.
 */
public class Func {
    private static Func ourInstance = new Func();

    public static Func getInstance() {
        return ourInstance;
    }

    private Func() {
    }

    public static byte[] integerToByte(int in, int size) {
        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.putInt(in);
        buff.order(ByteOrder.BIG_ENDIAN);

        byte[] rtn = new byte[size];
        if (size == Integer.SIZE / 8) {
            return buff.array();
        } else if (size < Integer.SIZE / 8) {
            System.arraycopy(buff.array(), buff.array().length - size, rtn, 0, size);
            return rtn;
        } else {
            System.arraycopy(buff.array(), 0, rtn, size - buff.array().length, buff.array().length);
            return rtn;
        }
    }

    public static byte[] longToByte(long lng, int size) {
        ByteBuffer buff = ByteBuffer.allocate(Long.SIZE / 8);
        buff.putLong(lng);
        buff.order(ByteOrder.BIG_ENDIAN);

        byte[] rtn = new byte[size];
        if (size == Long.SIZE / 8) {
            return buff.array();
        } else if (size < Long.SIZE / 8) {
            System.arraycopy(buff.array(), buff.array().length - size, rtn, 0, size);
            return rtn;
        } else {
            System.arraycopy(buff.array(), 0, rtn, size - buff.array().length, buff.array().length);
            return rtn;
        }
    }

    public static byte[] stringToByte(String s) {
        byte[] b = new byte[s.length()];
        try {
            b = s.getBytes("ksc5601");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return b;
    }


    public static long byteToLong(byte[] bytes) {

        ByteBuffer byte_buf = ByteBuffer.allocate(8);
        final byte[] change = new byte[8];

        for (int i = 0; i < 8; i++) {
            change[i] = (byte) 0x00;
        }
        for (int i = 0; i < bytes.length; i++) {
            change[8 - 1 - i] = bytes[bytes.length - 1 - i];
        }
        byte_buf = ByteBuffer.wrap(change);
        byte_buf.order(ByteOrder.BIG_ENDIAN);

        return byte_buf.getLong();
    }

    public static int byteToInteger(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getInt();
    }

    public static int byteToInteger(byte[] bytes, int len) {
        int value = 0;
        for (int i = 0; i < len; i++) {
            //int shift = (len - 1 - i) * 8;
            int shift = i * 8;
            value += (bytes[bytes.length - 1 - i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static byte[] mergyByte(byte[] b1, byte[] b2) {
        byte[] temp = new byte[b1.length + b2.length];

        System.arraycopy(b1, 0, temp, 0, b1.length);
        System.arraycopy(b2, 0, temp, b1.length, b2.length);

        return temp;
    }

    // 두 지점 구하는 알고리즘 적용
    public static double getDistance(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude) {
        if ((P1_latitude == P2_latitude) && (P1_longitude == P2_longitude)) {
            return 0;
        }
        double e10 = P1_latitude * Math.PI / 180;
        double e11 = P1_longitude * Math.PI / 180;
        double e12 = P2_latitude * Math.PI / 180;
        double e13 = P2_longitude * Math.PI / 180;
  /* 타원체 GRS80 */
        double c16 = 6356752.314140910;
        double c15 = 6378137.000000000;
        double c17 = 0.0033528107;
        double f15 = c17 + c17 * c17;
        double f16 = f15 / 2;
        double f17 = c17 * c17 / 2;
        double f18 = c17 * c17 / 8;
        double f19 = c17 * c17 / 16;
        double c18 = e13 - e11;
        double c20 = (1 - c17) * Math.tan(e10);
        double c21 = Math.atan(c20);
        double c22 = Math.sin(c21);
        double c23 = Math.cos(c21);
        double c24 = (1 - c17) * Math.tan(e12);
        double c25 = Math.atan(c24);
        double c26 = Math.sin(c25);
        double c27 = Math.cos(c25);
        double c29 = c18;
        double c31 = (c27 * Math.sin(c29) * c27 * Math.sin(c29))
                + (c23 * c26 - c22 * c27 * Math.cos(c29))
                * (c23 * c26 - c22 * c27 * Math.cos(c29));
        double c33 = (c22 * c26) + (c23 * c27 * Math.cos(c29));
        double c35 = Math.sqrt(c31) / c33;
        double c36 = Math.atan(c35);
        double c38 = 0;
        if (c31 == 0) {
            c38 = 0;
        } else {
            c38 = c23 * c27 * Math.sin(c29) / Math.sqrt(c31);
        }
        double c40 = 0;
        if ((Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))) == 0) {
            c40 = 0;
        } else {
            c40 = c33 - 2 * c22 * c26
                    / (Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38)));
        }
        double c41 = Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))
                * (c15 * c15 - c16 * c16) / (c16 * c16);
        double c43 = 1 + c41 / 16384
                * (4096 + c41 * (-768 + c41 * (320 - 175 * c41)));
        double c45 = c41 / 1024 * (256 + c41 * (-128 + c41 * (74 - 47 * c41)));
        double c47 = c45
                * Math.sqrt(c31)
                * (c40 + c45
                / 4
                * (c33 * (-1 + 2 * c40 * c40) - c45 / 6 * c40
                * (-3 + 4 * c31) * (-3 + 4 * c40 * c40)));
        double c50 = c17
                / 16
                * Math.cos(Math.asin(c38))
                * Math.cos(Math.asin(c38))
                * (4 + c17
                * (4 - 3 * Math.cos(Math.asin(c38))
                * Math.cos(Math.asin(c38))));
        double c52 = c18
                + (1 - c50)
                * c17
                * c38
                * (Math.acos(c33) + c50 * Math.sin(Math.acos(c33))
                * (c40 + c50 * c33 * (-1 + 2 * c40 * c40)));
        double c54 = c16 * c43 * (Math.atan(c35) - c47);
        // return distance in meter
        return c54;
    }

    public static byte[] getDirection(double bearing) {
        //A -> E 상행(0x01)
        //E -> A 하행(0x02)
        //예외처리(0x00)
        if (85 <= bearing && bearing <= 145) {
            return new byte[]{0x02};
        } else if (265 <= bearing && bearing <= 325) {
            return new byte[]{0x01};
        } else {
            return new byte[]{0x00};
        }
    }

    //방위각 구하는 공식 http://drkein.tistory.com/117 에서 퍼옴
    public static int getBearingAtoB(double latA, double lngA, double latB, double lngB) {
        // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
        double Cur_Lat_radian = latA * (3.141592 / 180);
        double Cur_Lon_radian = lngA * (3.141592 / 180);


        // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
        double Dest_Lat_radian = latB * (3.141592 / 180);
        double Dest_Lon_radian = lngB * (3.141592 / 180);

        // radian distance
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));
        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는 방향을 설정해야 한다. 라디안값이다.
        double radian_bearing = Math.acos((Math.cos(Dest_Lat_radian) - Math.cos(Cur_Lat_radian) * Math.cos(radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.

        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        } else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }

        return (int) true_bearing;
    }

    public static int getSpeed(double latA, double lngA, double latB, double lngB) {
        double distance = getDistance(latA, lngA, latB, lngB);
        return (int) ((distance * 3600) / 1000);

    }

    public static String getStringByMD5(String fileName) throws Exception {
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(new File(fileName));
        BufferedInputStream bis = new BufferedInputStream(fis);
        DigestInputStream dis = new DigestInputStream(bis, algorithm);

        // read the file and update the hash calculation
        while (dis.read() != -1)
            ;

        // get the hash value as byte array
        byte[] hash = algorithm.digest();

        return byteArray2Hex(hash);
    }

    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
