package neighbor.com.mbis.views.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import neighbor.com.mbis.R;
import neighbor.com.mbis.models.RouteInfo;

/**
 * Created by 권오철 on 2017-02-22.
 */

public class RouteAdapter extends ArrayAdapter<RouteInfo>{

    private LayoutInflater mInflate;
    private ArrayList<RouteInfo> items;
    private Context context;
    private ViewHolder mViewHolder;

    class ViewHolder {
        public TextView busNumber;
        public TextView direction;
        public TextView desc;
    }
    public RouteAdapter(Context context, int resource, ArrayList<RouteInfo> items) {
        super(context, resource);
        this.items = items;
        this.context = context;
        mInflate = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(convertView == null){
            mViewHolder = new ViewHolder();
            convertView = mInflate.inflate(R.layout.row_route_info, null);

            mViewHolder.busNumber = (TextView) v.findViewById(R.id.busNumber);
            mViewHolder.direction = (TextView) v.findViewById(R.id.direction);
            mViewHolder.desc = (TextView) v.findViewById(R.id.desc);

            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder)convertView.getTag();
            mViewHolder.busNumber.setText(items.get(position).getBusNum());
            mViewHolder.direction.setText(items.get(position).getDirection());
            mViewHolder.desc.setText(items.get(position).getStart_station() + " - " + items.get(position).getLast_station());
        }
        return convertView;
    }

}
