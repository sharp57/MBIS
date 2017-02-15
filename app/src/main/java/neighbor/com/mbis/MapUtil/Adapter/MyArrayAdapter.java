package neighbor.com.mbis.MapUtil.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import neighbor.com.mbis.R;

/**
 * Created by user on 2016-09-22.
 */
public class MyArrayAdapter extends ArrayAdapter {

    private ArrayList<String> items;
    Context c;

    public MyArrayAdapter(Context context, int resource, ArrayList<String> stationName) {
        super(context, resource, stationName);


        c = context;
        items = stationName;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(c).inflate(R.layout.map_item, null);
        }
        if (items.get(position) != null) {
            TextView tt = (TextView) v.findViewById(R.id.item_textView);
            if (tt != null){
                tt.setText(items.get(position).toString());
                tt.setSelected(true);
            }

//            Toast.makeText(c, "" + v.findViewById(R.id.item_imageView), Toast.LENGTH_SHORT).show();
//            Log.e("Adapter", v.findViewById(R.id.item_imageView) + "");
        }
        return v;
    }
}
