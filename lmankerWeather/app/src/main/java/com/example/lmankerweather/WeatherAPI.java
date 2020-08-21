package com.example.lmankerweather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//WeatherAPI objects which talks to the api and does most of the heavy lifting in this app.
public class WeatherAPI {
    //It's good practice to have getters and setters for all of these fields (and make them private)
    //but I ran out of time and I begrudgingly left them as is.

    //api key provided
    public String API_KEY = "";
    //default location set up just for testing, this always gets changed
    public String LOCATION = "London";
    //string to query openweather
    public String urlString;
    //current temp for the selected city
    public String currentTemp;
    //icon string for the weather conditions
    public String icon;
    //hi for the day
    public String hiTemp;
    //low for the day
    public String lowTemp;
    //latitude of the city.  This is required to know for the 3 day forecast.
    public String lat;
    public String longit;
    //probability of precipitation, this is acquired through the api one-call
    public String chancePrec;
    //all the icons for the next 3 days
    public String tomorrowIcon;
    public String dayAfterIcon;
    public String dayAfterAfterIcon;
    //temps for the next 3 days
    public String tomorrowTemp;
    public String dayAfterTemp;
    public String dayAfterAfterTemp;



    //constructs and api call with the city called into the constructor
    public WeatherAPI(String cityName){
        this.LOCATION = cityName;
        this.urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + LOCATION +
                "&units=imperial&appid=" + API_KEY;
        this.urlString = urlString.replaceAll("\\s+", "%20");
    }

    public void apiCall(){
        //this does the vanilla forecast API call where city names are acceptable
        HttpURLConnection conn = null;
        try{
            StringBuilder result = new StringBuilder();
            URL url = new URL(this.urlString);
            conn = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null){
                result.append(line);
            }

            rd.close();
            conn.disconnect();
            //making a JSON object and pulling the relevant information from it to add to the fields
            JSONObject jsonObject = new JSONObject(result.toString());
            this.icon = jsonObject.getJSONArray("weather")
                    .getJSONObject(0).get("icon").toString();
            this.currentTemp = jsonObject.getJSONObject("main").get("temp").toString();
            this.hiTemp = jsonObject.getJSONObject("main").get("temp_max").toString();
            this.lowTemp = jsonObject.getJSONObject("main").get("temp_min").toString();
            //longitude and latitude are necessary to retrieve in case we need to get the full
            //forecast.
            this.lat = jsonObject.getJSONObject("coord").get("lat").toString();
            this.longit = jsonObject.getJSONObject("coord").get("lon").toString();

        }catch (IOException e){
            Log.e("Bad Http request", "Incorrect API key or city not found.");
        } catch (JSONException e) {
            Log.e("JSON ERROR", "JSON object not identifiable.");
            e.printStackTrace();
        }
    }

    /*
    This function allows us to get the full forecast information for the future.  It is not possible
    with the usual forecast call to get all the needed information.
     */
    public void forecastCall(){
        this.urlString = "https://api.openweathermap.org/data/2.5/onecall?lat=" +
                lat + "&lon=" + longit + "&units=imperial&" +
                "exclude=hourly&appid=" + API_KEY;

        this.urlString = urlString.replaceAll("\\s+", "%20");
        HttpURLConnection conn = null;
        try{
            StringBuilder result = new StringBuilder();
            URL url = new URL(this.urlString);
            conn = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null){
                result.append(line);
            }

            rd.close();
            conn.disconnect();
            //grabbing all relevant future information for the 3 day forecast
            JSONObject jsonObject = new JSONObject(result.toString());
            this.chancePrec = jsonObject.getJSONArray("daily")
                    .getJSONObject(0).get("pop").toString();
            JSONArray tempTomorrow = new JSONArray(jsonObject.getJSONArray("daily")
                    .getJSONObject(1).get("weather").toString());
            this.tomorrowIcon = tempTomorrow.getJSONObject(0).get("icon").toString();
            JSONArray tempAfter = new JSONArray(jsonObject.getJSONArray("daily")
                    .getJSONObject(2).get("weather").toString());
            this.dayAfterIcon = tempAfter.getJSONObject(0).get("icon").toString();
            JSONArray tempAfterAfter = new JSONArray(jsonObject.getJSONArray("daily")
                    .getJSONObject(3).get("weather").toString());
            this.dayAfterAfterIcon = tempAfterAfter.getJSONObject(0).get("icon").toString();
            this.tomorrowTemp = jsonObject.getJSONArray("daily")
                    .getJSONObject(1).getJSONObject("temp").get("day").toString();
            this.dayAfterTemp = jsonObject.getJSONArray("daily")
                    .getJSONObject(2).getJSONObject("temp").get("day").toString();
            this.dayAfterAfterTemp = jsonObject.getJSONArray("daily")
                    .getJSONObject(3).getJSONObject("temp").get("day").toString();
        }catch (IOException e){
            Log.e("Bad Http request", "Incorrect API key or city not found.");
        } catch (JSONException e) {
            Log.e("JSON ERROR", "JSON object not identifiable.");
            e.printStackTrace();
        }
    }

    //this checks to see if the city exists for the add button on the main fragment.  If we get
    //bad http request then it doesn't exist, otherwise return true and tell the main fragment
    //it exists and it can add it to the list of cities.
    public boolean cityExists(String city){
        HttpURLConnection conn = null;
        String cityURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&units=imperial&appid=" + API_KEY;
        Log.i("URL", cityURL);
        try{
            StringBuilder result = new StringBuilder();
            URL url = new URL(cityURL);
            conn = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null){
                result.append(line);
            }

            rd.close();
            conn.disconnect();

            JSONObject jsonObject = new JSONObject(result.toString());


        }catch (IOException e){
            Log.e("Bad Http request", "Incorrect API key or city not found.");
            return false;
        } catch (JSONException e) {
            Log.e("JSON ERROR", "JSON object not identifiable.");
            e.printStackTrace();
        }

        return true;
    }
}
