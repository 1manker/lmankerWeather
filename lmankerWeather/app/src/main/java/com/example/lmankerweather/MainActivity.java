package com.example.lmankerweather;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        MainFrag.OnFragmentInteractionListener{

    //instantiate the fragment manager since the main activity should only be a container
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            //tell the main activity to act as a container for a new MainFrag class.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFrag())
                    .commit();
        }
    }

    /*
    This is how the fragments talk to each other and pass along data.  Instead of a persistent data
    file I opted to just pass along an ArrayList of cities to throw back on the listview after
    visiting the overview page.  It takes in two variables, the cityname for the overview, and the
    persistently updated list for the listview.  
     */
    @Override
    public void onFragmentInteraction(String name, ArrayList<String> cityNames) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FragmentManager fm = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction();
        if(name != "MAIN_FRAG"){
            transaction.replace(R.id.container, new OverviewFragment(name, cityNames));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
