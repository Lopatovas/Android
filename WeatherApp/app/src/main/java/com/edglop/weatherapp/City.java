package com.edglop.weatherapp;

public class City {

    private String name;
    private double temperature;
    private String conditions;
    private double windSpeed;
    private float humidity;
    private float pressure;

    public City(String name, double temperature, String conditions, double windSpeed, float humidity, float pressure) {
        this.name = name;
        this.temperature = temperature - 273;
        this.conditions = conditions;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public String getName() {
        return name;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getConditions() {
        return conditions;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

}
