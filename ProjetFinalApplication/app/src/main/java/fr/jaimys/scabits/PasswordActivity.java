package fr.jaimys.scabits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity that allow us to connect to the admin settings.
 * @see SettingsActivity
 */
public class PasswordActivity extends AppCompatActivity {

    //_________________________________________field_______________________________________________
    /**
     * The password, mandatory for using settings as admin.
     */
    private static final String PASSWORD = "Linstead";

    //_________________________________________methods______________________________________________
    /**
     * Compare the string value written by the user with the actual password. If those strings are
     * equal then the user can access to the settings activity.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final EditText editTextPassword = findViewById(R.id.edit_password);

        Button buttonBack = findViewById(R.id.backTo_account);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonValidate = findViewById(R.id.validate_password_admin);
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attemptPassword = editTextPassword.getText().toString();
                if(attemptPassword.compareTo(PASSWORD) == 0) {
                    Intent intent = new Intent(PasswordActivity.this,
                            SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(PasswordActivity.this, "Mot de passe incorrect",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}