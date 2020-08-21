package com.example.lmankerweather;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;


//custom adapter to display 3 different fields on the same row (icon, city name, temp)
public class CityAdapter extends BaseAdapter {

    Context context;
    List<Row> items;

    public CityAdapter(Context context, List<Row> items){
        this.context = context;
        this.items = items;
    }

    private class ViewHold{
        ImageView imageView;
        TextView city;
        TextView temp;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        //the important part of this is loading the images from openweather, after the weather API
        //has given us the icon string it throws it in a url to host the image in an imageholder.
        ViewHold holder = null;
        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.activity_listview, null);
            holder = new ViewHold();
            holder.city = convertView.findViewById(R.id.city);
            holder.imageView = convertView.findViewById(R.id.icon);
            holder.temp = convertView.findViewById(R.id.temp);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHold) convertView.getTag();
        }

        Row item = (Row) getItem(position);
        holder.city.setText(item.getCityName());
        holder.temp.setText(item.getTemp());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        imageLoader.displayImage("https://openweathermap.org/img/wn/"+ item.getImageID() +".png",
                holder.imageView);

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Row getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
       return items.indexOf(getItem(i));
    }

    //removal function for when items on the listview get deleted.
    public void remove(int position){
        Log.i("POSITION", Integer.toString(position));
        items.remove(position);
        notifyDataSetChanged();
    }

}
