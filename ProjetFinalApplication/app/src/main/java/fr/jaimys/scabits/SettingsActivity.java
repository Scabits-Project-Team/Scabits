package fr.jaimys.scabits;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Activity that allow the user to change the values of TIME_PARENT and TIME_CHILD in AccountActivity.
 * @see AccountActivity
 */
public class SettingsActivity extends AppCompatActivity {

    //_________________________________________methods______________________________________________
    /**
     * Fill the spinners with a few numbers.
     * The items that are selected by default are the items that have the same values as the current
     * TIME_PARENT and TIME_CHILD.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Button settings
        Button buttonExit = findViewById(R.id.exit_settings);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Spinner settings
        AdapterView.OnItemSelectedListener onItemSelectedListenerParent =
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeString = parent.getItemAtPosition(position).toString();
                int time = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
                if(time != AccountActivity.TIME_PARENT){
                    AccountActivity.setTimeParent(time);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        AdapterView.OnItemSelectedListener onItemSelectedListenerChild =
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeString = parent.getItemAtPosition(position).toString();
                int time = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
                if(time != AccountActivity.TIME_CHILD){
                    AccountActivity.setTimeChild(time);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        Spinner spinnerParent = findViewById(R.id.spinner_parent);
        ArrayAdapter<CharSequence> arrayAdapterParent = ArrayAdapter.createFromResource(this,
                R.array.timeParent, android.R.layout.simple_spinner_item);
        arrayAdapterParent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParent.setAdapter(arrayAdapterParent);
        switch (AccountActivity.TIME_PARENT){
            case 1: spinnerParent.setSelection(0); break;
            case 3: spinnerParent.setSelection(1); break;
            case 5: spinnerParent.setSelection(2); break;
            case 7: spinnerParent.setSelection(3); break;
            case 10: spinnerParent.setSelection(4); break;
            default: break;
        }
        spinnerParent.setOnItemSelectedListener(onItemSelectedListenerParent);

        Spinner spinnerChild = findViewById(R.id.spinner_child);
        ArrayAdapter<CharSequence> arrayAdapterChild = ArrayAdapter.createFromResource(this,
                R.array.timeChild, android.R.layout.simple_spinner_item);
        arrayAdapterChild.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChild.setAdapter(arrayAdapterChild);
        switch (AccountActivity.TIME_CHILD){
            case 10: spinnerChild.setSelection(0); break;
            case 30: spinnerChild.setSelection(1); break;
            case 50: spinnerChild.setSelection(2); break;
            case 60: spinnerChild.setSelection(3); break;
            default: break;
        }
        spinnerChild.setOnItemSelectedListener(onItemSelectedListenerChild);
    }
}