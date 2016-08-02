package app.com.example.mannas.sunshine_mine;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompatBase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add( R.id.Activity_main , new mainForeCastFragment()).commit();
    }


    public static class mainForeCastFragment extends Fragment {

        public ArrayAdapter<String> itemsAdapter;

        public static List<String> DATA_List;
        private final String LOG_TAG = mainForeCastFragment.class.getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            DATA_List = new ArrayList<String>();
            DATA_List.addAll(Arrays.asList(GetTrevialItems()));

            //// TODO: 7/30/2016  execute() to be updated when start
            //new dataFetcher().execute();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

            itemsAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_TextView,
                    DATA_List);

            ListView listview = (ListView) rootView.findViewById(R.id.listView_forecast);
            listview.setAdapter(itemsAdapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = getContext();
                    CharSequence text = "item clicked num : " + position + "  " + ((TextView) view).getText();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    Intent intent = new Intent(getActivity(), oneDay.class);
                    intent.putExtra( getContext().getString(R.string.title_oneDay) , ((TextView) view).getText());
                    startActivity(intent);
                }
            });

            return rootView;
        }

        private  String[] GetTrevialItems(){
            String[] items = {"Tomorrow - Sunny - 88/63",
                    "Wed - Sunny - 88/63",
                    "Thu - Normal - 88/63",
                    "Fri - Sunny - 88/63"};
            return  items;
        }
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.main_menu,menu);
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                Log.i("main menu item ",  "settings");
                startActivity(new Intent(getActivity(), settings.class));
                return true;
            }else if(id == R.id.action_refresh){
                Log.i("main menu item ", "Refresh");
                final String CityKey = getString(R.string.pref_location_key);
                final String DefaultCity = getString(R.string.pref_location_default);
                 String param =   PreferenceManager.getDefaultSharedPreferences(getContext()).getString(CityKey,DefaultCity);
                new dataFetcher().execute(param);
                return true;
            }else if(id == R.id.action_on_map){
                Log.i("main menu item ",  "map");

                String city = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                                                                                                    getString(R.string.pref_location_key),
                                                                                                    getString(R.string.pref_location_default) );
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + city);

                Intent intent = new Intent(Intent.ACTION_VIEW , gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity( getContext().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

            return super.onOptionsItemSelected(item);
        }

        public class dataFetcher extends AsyncTask< String,Void,String[]> {
            private final String LOG_TAG = dataFetcher.class.getSimpleName();

            final String APP_site = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            String q_CityName = "q=";
            final String MODE = "&mode="+"json";
            final String UNITS =  "&units="+"metric";
            final String CNT = "&cnt="+"16";
            final String APPID = "&APPID="+"593aac0ba077ce2fb8c5dd30f90a4651";

            @Override
            protected String[] doInBackground(String... params) {
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.

                if(params.length >= 1) {
                    q_CityName += params[0];
                }else{
                    q_CityName+= getString(R.string.pref_location_default);
                }

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;

                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                    //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                    String baseUrl = APP_site+q_CityName+MODE+UNITS+CNT+APPID;

                    URL url = new URL(baseUrl);

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    forecastJsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    return getWeatherDataFromJson(forecastJsonStr,16);
                }
                catch (JSONException jex){
                    Log.e(LOG_TAG," Can Not Get the Data Array !");
                }
                return  null;
            }
            @Override
            protected void onPostExecute(String[] strings) {
                //super.onPostExecute(strings);
                if(strings!=null){
               /*
               //both ways are working ( update the List + notifyDataSetChanged ) OR ( clear() + add() )
                DATA_List.clear();
                DATA_List.addAll(Arrays.asList(strings));
                itemsAdapter.notifyDataSetChanged();
                */

                    itemsAdapter.clear();
                    for(String item: strings){
                        itemsAdapter.add(item);
                    }
                }

            }
            private String getReadableDateString(long time){
                // Because the API returns a unix timestamp (measured in seconds),
                // it must be converted to milliseconds in order to be converted to valid date.
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(time);
            }
            /**
             * Prepare the weather high/lows for presentation.
             */
            private String formatHighLows(double high, double low) {
                // For presentation, assume the user doesn't care about tenths of a degree.
                long roundedHigh = Math.round(high);
                long roundedLow = Math.round(low);

                return roundedHigh + "/" + roundedLow;
            }

            /**
             * Take the String representing the complete forecast in JSON Format and
             * pull out the data we need to construct the Strings needed for the wireframes.
             *
             * Fortunately parsing is easy:  constructor takes the JSON string and converts it
             * into an Object hierarchy for us.
             */
            private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                    throws JSONException {

                // These are the names of the JSON objects that need to be extracted.
                final String OWM_LIST = "list";
                final String OWM_WEATHER = "weather";
                final String OWM_TEMPERATURE = "temp";
                final String OWM_MAX = "max";
                final String OWM_MIN = "min";
                final String OWM_DESCRIPTION = "main";

                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                // OWM returns daily forecasts based upon the local time of the city that is being
                // asked for, which means that we need to know the GMT offset to translate this data
                // properly.

                // Since this data is also sent in-order and the first day is always the
                // current day, we're going to take advantage of that to get a nice
                // normalized UTC date for all of our weather.

                Time dayTime = new Time();
                dayTime.setToNow();

                // we start at the day returned by local time. Otherwise this is a mess.
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                // now we work exclusively in UTC
                dayTime = new Time();

                String[] resultStrs = new String[numDays];
                for(int i = 0; i < weatherArray.length(); i++) {
                    // For now, using the format "Day, description, hi/low"
                    String day;
                    String description;
                    String highAndLow;

                    // Get the JSON object representing the day
                    JSONObject dayForecast = weatherArray.getJSONObject(i);

                    // The date/time is returned as a long.  We need to convert that
                    // into something human-readable, since most people won't read "1400356800" as
                    // "this saturday".
                    long dateTime;
                    // Cheating to convert this to UTC time, which is what we want anyhow
                    dateTime = dayTime.setJulianDay(julianStartDay+i);
                    day = getReadableDateString(dateTime);

                    // description is in a child array called "weather", which is 1 element long.
                    JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    description = weatherObject.getString(OWM_DESCRIPTION);

                    // Temperatures are in a child object called "temp".  Try not to name variables
                    // "temp" when working with temperature.  It confuses everybody.
                    JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                    double high = temperatureObject.getDouble(OWM_MAX);
                    double low = temperatureObject.getDouble(OWM_MIN);

                    highAndLow = formatHighLows(high, low);
                    resultStrs[i] = day + " - " + description + " - " + highAndLow;
                }

                for (String s : resultStrs) {
                    Log.v( LOG_TAG, "Forecast entry: " + s);
                }
                return resultStrs;

            }


        }

    }

}
