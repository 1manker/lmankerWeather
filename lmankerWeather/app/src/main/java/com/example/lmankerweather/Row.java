package com.example.lmankerweather;

//simple class construct rows for the listview.  Each row consists of a city name, icon, temp.
public class Row {
    private String imageID;
    private String cityName;
    private String temp;

    public Row(String imageID, String cityName, String temp){
        this.imageID = imageID;
        this.cityName = cityName;
        this.temp = temp;
    }

    public void setImageID(String imageID){
        this.imageID = imageID;
    }

    public String getImageID() {
        return imageID;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }
}
