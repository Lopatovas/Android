package com.edglop.weatherapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView locationList; //Main ListView for selected cities.
    Button addCityButton; //Button to add cities to main list.
    final int CmaxLocations = 6; //Maximum number of cities allowed at the same time.
    ListView customDialog = null; //Dialog ListView for selecting cities.
    boolean wasEnabled = false; //Checks if a dialog was created.
    AlertDialog dialog; //Dialog
    Adapter myadapter; //Custom adapter for main city list item display.
    ArrayList<String> optionsList = new ArrayList<>(); //Array for adding manipulating option list data.
    final ArrayList<City> cityList = new ArrayList<>(); //Array for manipulating selected city list.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationList = findViewById(R.id.locationList);
        addCityButton = findViewById(R.id.addCity);
        myadapter = new Adapter(this, cityList);
        locationList.setAdapter(myadapter);
        dialogSetupCreation();
        Data startData = new Data();
        Data savedData = new Data();
        startData.setDefault();
        loadSavedData(startData, savedData);
        showSavedData(savedData);
        setOptionsListValues(startData);
        removeOptions(savedData);

    }

    //City adding options
    public void addCityClick(View view){
        if(myadapter.getCount() >= CmaxLocations){
            Toast.makeText(this, "Only 6 cities are allowed at a time.", Toast.LENGTH_SHORT).show();
        }
        else{
            showDialog();
            }
    }

    //If already created, shows the dialog, else creates a new dialog for options.
    public void showDialog(){
        if(!wasEnabled) {
            dialog = createDialog();
            dialog.show();
        }
        else {
            dialog.show();
        }
    }

    //Dialog creation method
    public AlertDialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);
        builder.setView(customDialog);
        AlertDialog dialog = builder.create();
        wasEnabled = true;
        return dialog;
    }

    //Dialog layout and on click effect creator
    public void dialogSetupCreation(){
        customDialog = new ListView(this);
        final ArrayAdapter<String> choiceAdapter = new ArrayAdapter<>(this, R.layout.dialog_custom_list_item, R.id.listItem, optionsList);
        customDialog.setAdapter(choiceAdapter);
        customDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup group = (ViewGroup) view;
                TextView textView = group.findViewById(R.id.listItem);
                String choice = String.valueOf(textView.getText());
                Log.i("chars", choice);
                DownloadData task = new DownloadData();
                if(myadapter.getCount() >= CmaxLocations){
                    Toast.makeText(MainActivity.this, "Only 6 cities are allowed at a time.", Toast.LENGTH_SHORT).show();
                }
                else{
                    caseBreaker(task, choice);
                    setSavedData(choice);
                    choiceAdapter.remove(choiceAdapter.getItem(findChoice(choice)));
                }
            }
        });
    }

    //Adds values to the option list
    public void setOptionsListValues(Data data){
        for(int i = 0; i < data.count; i++){
            optionsList.add(data.getElement(i));
        }
    }

    //finds chosen item from the options list and returns it's place in the array
    public int findChoice(String choice) {
        int temp = 0;
        for (int i = 0; i < optionsList.size(); i++) {
            if (choice.toLowerCase().equals(optionsList.get(i).toLowerCase())) {
                temp = i;
                return temp;

            }
        }
        return temp;
    }

    //For protecting from exceptions due to lithuanian and latvian letters
    public void caseBreaker(DownloadData task, String choice){
        switch(choice){
            case("Klaipeda"):
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=Klaipeda&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
            case("Šiauliai"):
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=%C5%A0iauliai&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
            case("Panevėžys"):
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=Panev%C4%97%C5%BEys&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
            case("Marijampolė"):
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=Marijampol%C4%97&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
            case("Liepāja"):
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=Liep%C4%81ja&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
            case("Jūrmala"):
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=J%C5%ABrmala&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
            case("Rēzekne"):
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=R%C4%93zekne&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
            default:
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + choice + "&appid=1862f18db1e939550cbd14a3c9f6a857");
                break;
        }
    }

    //Class that downloads data in the background from the internet/
    public class DownloadData extends AsyncTask<String, Void, String> {

        //Local variables for usage in creating a city later on.
        String name;
        double temperature;
        String conditions;
        double windSpeed;
        float humidity;
        float pressure;

        @Override
        protected String doInBackground(String... urls) {
            String results = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    results += current;
                    data = reader.read();
                }
                return  results;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);
            String weatherGeneralInfo = extractInfo(results, "weather"); //general info, cloudly sunny ect.
            String weatherWindInfo = extractInfo(results, "wind"); //Wind speed and degrees.
            String weatherMainInfo = extractInfo(results, "main"); //temperature, pressure, humidity.
            String weatherAreaInfo = extractInfo(results, "name"); //Name of area.

            name = weatherAreaInfo;
            temperature = Double.parseDouble(extractInfo(weatherMainInfo, "temp"));
            conditions = extractConditions(weatherGeneralInfo, "description");
            windSpeed = Double.parseDouble(extractInfo(weatherWindInfo, "speed"));
            humidity = Float.parseFloat(extractInfo(weatherMainInfo, "humidity"));
            pressure = Float.parseFloat(extractInfo(weatherMainInfo, "pressure"));
            City temp = new City(name, temperature, conditions, windSpeed, humidity, pressure);
            cityList.add(temp);
            myadapter.notifyDataSetChanged();

        }

        public String extractInfo(String pattern, String searchInfo){
            String tempString = "";
            try {
                JSONObject tempJSON = new JSONObject(pattern);
                tempString = tempJSON.getString(searchInfo);
                return tempString;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tempString;
        }

        public String extractConditions(String searchPattern, String searchInfo){
            String tempString = "";
            try{
                JSONArray asd = new JSONArray(searchPattern);
                for(int i =0; i < asd.length(); i++){
                    JSONObject jsonPart = asd.getJSONObject(i);
                    tempString = jsonPart.getString(searchInfo);

                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return tempString;

        }
    }

    //For saving data in SP
    public void setSavedData(String cityNames){
        SharedPreferences.Editor editor = getSharedPreferences("Saved_Cities", MODE_PRIVATE).edit();
        editor.putString(cityNames, cityNames);
        editor.apply();
    }

    //For getting data out of SP with a key value
    public String getSavedData(String key){
        SharedPreferences prefs = getSharedPreferences("Saved_Cities", MODE_PRIVATE);
        String temp = prefs.getString(key, null);
        return temp;

    }

    //Load up saved data from the last session
    public void loadSavedData(Data startData, Data savedData){
        for(int i = 0; i < startData.count; i++){
            String temp = getSavedData(startData.getElement(i));
            if(temp != null){
                savedData.addElement(temp);
            }
        }
    }

    //clear specific saved values from SP
    public void removeSavedData(String key){
        SharedPreferences.Editor editor = getSharedPreferences("Saved_Cities", MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
    }

    //Shows the cities that the user has selected before.
    public void showSavedData(Data savedData){
        for(int j = 0; j < savedData.count; j++){
            DownloadData task = new DownloadData();
            caseBreaker(task, savedData.getElement(j));
        }
    }

    //Removes already selected elements from the options tab
    public void removeOptions(Data data){
        for(int i = 0; i < data.count; i++){
            optionsList.remove(data.getElement(i));
        }
    }

    ///Deletes an item from the main listview
    public void deleteClick(View view){
        final int position = locationList.getPositionForView((View) view.getParent());
        City temp = cityList.get(position);
        optionsList.add(temp.getName()); //add to options again
        removeSavedData(temp.getName()); //remove from SP
        cityList.remove(temp); //remove from list
        myadapter.notifyDataSetChanged();
    }

}
