package fr.jaimys.scabits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    //_________________________________________field_______________________________________________
    private static final String PASSWORD = "Linstead";

    //_________________________________________methods______________________________________________
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
                    Intent intent = new Intent(PasswordActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(PasswordActivity.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}