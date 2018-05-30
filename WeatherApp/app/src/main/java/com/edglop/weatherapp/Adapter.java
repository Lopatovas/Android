package com.edglop.weatherapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

//Custom adapter for main list display settings
public class Adapter extends ArrayAdapter<City> {

    public Adapter(@NonNull Context context, ArrayList<City> cities) {
        super(context,R.layout.customrow, cities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.customrow, parent, false);

        City singleCity = getItem(position);
        TextView tempName = customView.findViewById(R.id.nameLayout);
        TextView tempTemperature = customView.findViewById(R.id.temperatureLayout);
        TextView tempDescription = customView.findViewById(R.id.descriptionLayout2);
        TextView tempHumidity = customView.findViewById(R.id.humidityLayout);
        TextView tempWind = customView.findViewById(R.id.windLayout);
        TextView tempPressure = customView.findViewById(R.id.pressureLayout);
        Button delButton = customView.findViewById(R.id.deleteButton);

        tempName.setText(singleCity.getName());
        tempTemperature.setText("Degrees, C: " + String.valueOf(Math.round(singleCity.getTemperature())));
        tempDescription.setText(singleCity.getConditions());
        tempHumidity.setText("Humidity: " + String.valueOf(Math.round(singleCity.getHumidity())) + " %");
        tempWind.setText("Wind: " + String.valueOf(singleCity.getWindSpeed()) + " m/s");
        tempPressure.setText("Pressure: " + String.valueOf(singleCity.getPressure()) + " hPa");

        return customView;
    }

    @Override
    public void add(@Nullable City object) {
        super.add(object);
        notifyDataSetChanged();
    }
}

