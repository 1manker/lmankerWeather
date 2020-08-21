package com.example.lmankerweather;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/*overview fragment which displays high/low/current temperature for the day, probability of
precipitation, cityname, and a small three day forecast.  The three day forecast simply shows the
icon for weather, the temperature, and a dynamically determined day of the week.
*/
public class OverviewFragment extends Fragment {

    String city;
    TextView cityName;
    ArrayList<String> cityNames = new ArrayList<String>();

    public OverviewFragment(String city, ArrayList<String> cityNames){
        this.city = city;
        this.cityNames = cityNames;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //all the pieces of the overview to be displayed.
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        TextView cityName = rootView.findViewById(R.id.cityName);
        cityName.setText(city);
        TextView temp = rootView.findViewById(R.id.temp);
        TextView lowTemp = rootView.findViewById(R.id.lowTemp);
        TextView hiTemp = rootView.findViewById(R.id.hiTemp);
        TextView precip = rootView.findViewById(R.id.precip);

        TextView dayAfter = rootView.findViewById(R.id.dayAfter);
        TextView dayAfterAfter = rootView.findViewById(R.id.dayAfterAfter);
        TextView tomorrowTemp = rootView.findViewById(R.id.tomorrowTemp);
        TextView dayAfterTemp = rootView.findViewById(R.id.dayAfterTemp);
        TextView dayAfterAfterTemp = rootView.findViewById(R.id.dayAfterAfterTemp);
        //This is names of the two days after tomorrow to be displayed.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date date = calendar.getTime();
        dayAfter.setText(new SimpleDateFormat("EEEE",
                Locale.ENGLISH).format(date.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        date = calendar.getTime();
        dayAfterAfter.setText(new SimpleDateFormat("EEEE",
                Locale.ENGLISH).format(date.getTime()));

        ImageView tomorrowIcon = rootView.findViewById(R.id.tomorrowIcon);
        ImageView dayAfterIcon = rootView.findViewById(R.id.dayAfterIcon);
        ImageView dayAfterAfterIcon = rootView.findViewById(R.id.dayafterAfterIcon);

        ImageView view = rootView.findViewById(R.id.icon);


        //New weather api for the current day
        WeatherAPI weather = new WeatherAPI(city.split(",")[0]);
        weather.apiCall();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        imageLoader.displayImage("https://openweathermap.org/img/wn/"+ weather.icon +".png",
                view);
        temp.setText(weather.currentTemp.split("\\.")[0]+ "\u2109");
        lowTemp.setText(weather.lowTemp.split("\\.")[0]+ "\u2109");
        hiTemp.setText(weather.hiTemp.split("\\.")[0]+ "\u2109");

        //updating the weather API fields to reflect the next 3 days
        weather.forecastCall();
        //icons for the next 3 days
        imageLoader.displayImage("https://openweathermap.org/img/wn/"+ weather.tomorrowIcon
                        +".png",
                tomorrowIcon);
        imageLoader.displayImage("https://openweathermap.org/img/wn/"+ weather.dayAfterIcon
                        +".png",
                dayAfterIcon);
        imageLoader.displayImage("https://openweathermap.org/img/wn/"
                        + weather.dayAfterAfterIcon +".png",
                dayAfterAfterIcon);
        double chancePrecip = Double.parseDouble(weather.chancePrec) * 100;
        //Probability of precipitation
        precip.setText("Chance of precipitation:   " + (int)chancePrecip + "%");

        //temps for the next 3 days (during the day)
        tomorrowTemp.setText(weather.tomorrowTemp.split("\\.")[0]+ "\u2109");
        dayAfterTemp.setText(weather.dayAfterTemp.split("\\.")[0]+ "\u2109");
        dayAfterAfterTemp.setText(weather.dayAfterAfterTemp.split("\\.")[0]+ "\u2109");

        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState) {
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener(){
            //if the back button is pressed, make a new mainfrag with the same list.
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.overviewFrag,new MainFrag(cityNames))
                            .addToBackStack("my_fragment").commit();
                    return true;
                }
                return false;
            }
        });
    }
}
