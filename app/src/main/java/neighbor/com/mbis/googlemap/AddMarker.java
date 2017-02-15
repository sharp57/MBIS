package neighbor.com.mbis.googlemap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import neighbor.com.mbis.R;

public class AddMarker {
    private GoogleMap Markermap;
    Marker busMark;

    public AddMarker(GoogleMap gmap) {
        Markermap = gmap;
    }

    //주소 가져오는 메소드
    public String getAddress(double lat, double lng , Context mContext) {
        String str = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);

        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    str = address.get(0).getAddressLine(0).toString();
                }
            }
        } catch (IOException e) {
            Log.e("MainActivity", "주소를 찾지 못하였습니다.");
            e.printStackTrace();
        }

        return str;

    }


// 마크 찍는 메소드
    public Marker getMark(double lat, double lon, final Context mContext) {

        try {

            busMark = Markermap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title("버스정류장")
                    .snippet("주소 : " + getAddress(lat,lon,mContext) + "\n")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Markermap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        return busMark;
    }

}