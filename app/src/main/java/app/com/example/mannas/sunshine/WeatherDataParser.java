package app.com.example.mannas.sunshine;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mannas on 7/25/2016.
 */
public class WeatherDataParser {
/*
*
*       Experimental
* */


    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
    {
        double max = 0.0;
        try {
            JSONObject jo = new JSONObject(weatherJsonStr);

            JSONArray list  = jo.getJSONArray("list");

            if(dayIndex <= list.length()-1 && dayIndex>=0){
                JSONObject day = (JSONObject) list.get(dayIndex);
                JSONObject temp = (JSONObject) day.get("temp");
                max = temp.getDouble("max");
                return max;
            }

            return max;
        }
        catch (JSONException jex){

        }

        return 0.0;
    }
}
