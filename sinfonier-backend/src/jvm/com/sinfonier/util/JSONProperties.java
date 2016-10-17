package com.sinfonier.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONProperties {

    private JSONObject jsonConfig;

    public JSONProperties(String jsonFileInsideJAR) {

        this.jsonConfig = this.getJsonConfig(jsonFileInsideJAR);
    }

    public JSONObject getJsonConfig(String jsonFile){

        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line = "{}";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(line);
    }

    public JSONObject getProperties(){
        return this.jsonConfig.getJSONObject("properties");
    }

    public Object getProperty(String property){
        if(this.getProperties().has(property)){
            return this.getProperties().get(property);
        } else {
            return null;
        }
    }

    public JSONArray getSpouts(){
        return this.jsonConfig.getJSONObject("builderConfig").getJSONArray("spouts");
    }

    public JSONArray getBolts(){
        return this.jsonConfig.getJSONObject("builderConfig").getJSONArray("bolts");
    }

    public JSONArray getDrains(){
        return this.jsonConfig.getJSONObject("builderConfig").getJSONArray("drains");
    }

}
