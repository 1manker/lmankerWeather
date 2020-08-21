package com.example.lmankerweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.List;




public class MainFrag extends Fragment{

    /*
    This fragment handles the listview.  It has a few attributes that keep track of cities and add
    listeners.  cityNames store the names of cities to send to the API to grab data, the images
    array is there for dummy testing of weather icons.  The boolean remove turns the listview into
    remove mode when activated.  The mListener variable is here to conduct communication between
    fragments and the main activity.  The listview is here to display the data collected, and the
    rowItems list is the collection of objects constructed from the API into rows.  These rows
    include the icon, the name of the city, and the temperature.
     */
    ArrayList<String> cityNames = new ArrayList<String>();
    //example images used for testing, these will be overwritten.
    public String[] images = {"04d", "04d", "04d"};

    boolean remove = false;

    ListView listView;
    List<Row> rowItems;

    private OnFragmentInteractionListener mListener;

    public MainFrag() {
        //If the program just starts up it'll fill in these hardcoded values as explained in the
        //directions.
        cityNames.add("San Francisco, CA");
        cityNames.add("New York, NY");
        cityNames.add("Salt Lake City, UT");
    }

    public MainFrag(ArrayList<String> cityNames){
        //If this fragment is being activated after the overview, it will be constructed with the
        //previous values found on the listview.
        this.cityNames = cityNames;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //This allows for the API to be called on a separate thread.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //takes the citynames and makes Row objects for the listview.
        rowItems = new ArrayList<Row>();
        for (int i = 0; i < cityNames.size(); i++) {
            //takes the cityname and queries the api
            WeatherAPI weather = new WeatherAPI(cityNames.get(i).split(",")[0]);
            weather.apiCall();
            //The Row item is comprised of the icon from the api, the name of the city, and the temp
            Row item = new Row(weather.icon, cityNames.get(i),
                    weather.currentTemp.split("\\.")[0] + "\u2109");
            //add everything to the listview for display
            rowItems.add(item);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(final View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        //setting up an adapter for the listview so the overview screen can be accessed.
        listView = (ListView) rootView.findViewById(R.id.list);
        final CityAdapter adapter = new CityAdapter(rootView.getContext(), rowItems);
        //connect the custom adapter to display the three fields in one row (icon, name, temp).
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(remove){
                    //if the remove button has been enabled then don't go to overview screen,
                    //delete the selected entry.
                    remove = false;
                    cityNames.remove(i);
                    rowItems.remove(i);
                    adapter.notifyDataSetChanged();

                }
                else {
                    //if remove button not enabled then go to overview screen, send the new fragment
                    //the city list for it to hold when the back button is pressed.
                    String temp = adapter.getItem(i).getCityName();
                    mListener.onFragmentInteraction(temp, cityNames);
                }
            }
        });
        //simple remove button, clicked once it will enable to remove a single entry.
        Button removeButton = rootView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                remove = true;
            }
        });
        //add button, after pressed it will bring up a dialogue to enter a city.
        Button addButton = rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showTextDialog();
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity = getActivity();
        //making sure the fragment listener is enabled, Java requires this block here
        try{
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + "must have frag listenener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

    //used to communicate between fragments and maintain the same list
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String name, ArrayList<String> cityNames);
    }


    //all this is doing is simply asking for a city, checking the api for it's existence, and
    //adding it to the list.

    private void showTextDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.AlertDialogStyle);
        builder.setTitle("Enter City Name");

        final EditText input = new EditText(getContext());
        input.setTextColor(Color.parseColor("#FFFFFF"));

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredText = input.getText().toString();
                WeatherAPI cityCheck = new WeatherAPI(enteredText.split(",")[0]);
                //if the city exists then add it!
                if(cityCheck.cityExists(enteredText.split(",")[0])){
                    cityNames.add(enteredText);
                    cityCheck.apiCall();
                    Row item = new Row(cityCheck.icon, enteredText,
                            cityCheck.currentTemp.split("\\.")[0] + "\u2109");
                    rowItems.add(item);
                }
                //if the api call doesn't return a JSON object then it doesn't exist, so notify user
                else{
                    Toast toast = Toast.makeText(getContext(),
                            "City Not Found", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
