package app.com.example.mannas.sunshine_mine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class oneDay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_one_day);

        oneDayFragment fragment = new oneDayFragment();
        fragment.setExtras(getIntent().getExtras());
        setContentView(R.layout.activity_one_day);

        getSupportFragmentManager().beginTransaction().add(R.id.activity_one_day, fragment).commit();
    }

    public static class oneDayFragment extends Fragment{

        Bundle extras;
        public  void setExtras(Bundle extras){
            this.extras = extras;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_one_day , container, false);
            TextView textView = (TextView) view.findViewById(R.id.oneDay_text);
            if(extras!=null){
                textView.setText(extras.getString( getContext().getString(R.string.title_oneDay)));
            }else {
                String out = "The Extras passed to the " + oneDayFragment.class.getName()+" = null !";
                textView.setText(out);
            }

            return view;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.one_day_menu, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == R.id.action_settings ){
                startActivity(new Intent(getActivity(),settings.class));
            }
            return  true;
        }


    }

}

