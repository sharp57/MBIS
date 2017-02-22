package neighbor.com.mbis.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import neighbor.com.mbis.R;
import neighbor.com.mbis.data.RouteInfo;
/**
 * Created by 권오철 on 2017-02-22.
 */

public class RouteAdapter extends ArrayAdapter<RouteInfo>{

    private ArrayList<RouteInfo> item;

    public RouteAdapter(Context context, int resource, ArrayList<RouteInfo> item) {
        super(context, resource);
        this.item = item;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
//        if (v == null) {
//            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = vi.inflate(R.layout.row, null);
//        }
//        Person p = items.get(position);
//        if (p != null) {
//            TextView tt = (TextView) v.findViewById(R.id.toptext);
//            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
//            if (tt != null){
//                tt.setText(p.getName());
//            }
//            if(bt != null){
//                bt.setText("전화번호: "+ p.getNumber());
//            }
//        }
        return v;
    }

}
