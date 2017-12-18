package com.example.onair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kleanthis on 14-Dec-17.
 */

public class Detail_class {
    private ArrayList<HashMap<String, String>> arrayListToGo;
    private String storeCurrency, labelGo, labelDestination;
    private int NUMBER_OF_ADULTS;

    public Detail_class() {
        arrayListToGo = new ArrayList<HashMap<String, String>>();
    }

    public ArrayList<HashMap<String, String>> getArrayListToGo() {
        return arrayListToGo;
    }

    public void addArrayListToGo (HashMap<String, String> insertValue){
        this.arrayListToGo.add(insertValue);
    }


    //setters
    public void setStoreCurrency(String storeCurrency) {
        this.storeCurrency = storeCurrency;
    }

    public void setLabelGo(String labelGo) {
        this.labelGo = labelGo;
    }

    public void setLabelDestination(String labelDestination) {
        this.labelDestination = labelDestination;
    }

    public void setNUMBER_OF_ADULTS(int NUMBER_OF_ADULTS) {
        this.NUMBER_OF_ADULTS = NUMBER_OF_ADULTS;
    }
}
