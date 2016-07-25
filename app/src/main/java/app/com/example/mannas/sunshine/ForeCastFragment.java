package app.com.example.mannas.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForeCastFragment extends Fragment {

    public ArrayAdapter<String> itemsAdapter;
    String[] DATA;
    public static List<String> DATA_List;

    private final String LOG_TAG = ForeCastFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


            DATA = GetTrevialItems();
            DATA_List = new ArrayList<String>();
            DATA_List =  Arrays.asList(DATA);
            //new dataFetcher().execute();
        itemsAdapter.clear();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        itemsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_TextView,
                DATA_List);

        ListView listview = (ListView) rootView.findViewById(R.id.listView_forecast);
        listview.setAdapter(itemsAdapter);

        listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Item ", "has clicked !");
            }
        });

        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i("menue sellected item",Integer.toString(id)+"From For");
            return true;
        }else if(id == R.id.action_refresh){
            Log.i("menue sellected item",Integer.toString(id)+"From For");
            //new dataFetcher().execute();
            itemsAdapter.clear();
            Log.i(DATA[0],"hjkh");

            return true;
        }

        return super.onOptionsItemSelected(item);
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
        inflater.inflate(R.menu.main,menu);
    }




}

