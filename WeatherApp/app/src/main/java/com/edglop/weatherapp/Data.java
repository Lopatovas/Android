package com.edglop.weatherapp;

import java.util.ArrayList;

//Class for possible options data
public class Data {

    String[] choices;
    final int CmaxChoices = 14;
    int count;

    public Data(){
        choices = new String[CmaxChoices];
        count = 0;
    }

    public void addElement(String element){
        choices[count] = element;
        count++;
    }

    public void setDefault(){
        String[] tempArray = {"Vilnius", "Kaunas", "Klaipeda", "Šiauliai", "Panevėžys", "Alytus", "Marijampolė", "Riga",
        "Daugavpils", "Liepāja", "Jelgava", "Jūrmala", "Ventspils", "Rēzekne"};
        for(int i = 0; i < tempArray.length; i++){
            addElement(tempArray[i]);
        }
    }

    public String[] getArray(){
        return choices;
    }

    public ArrayList<String> getAsArrayList(){
        ArrayList<String> temp = new ArrayList<String>(CmaxChoices);
        for(int i = 0; i < count; i++){
            temp.add(choices[i]);
        }

        return temp;
    }

    public String getElement(int i ){
        return choices[i];
    }


}
