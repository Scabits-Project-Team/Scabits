package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity that allow the user to login by checking if he exists in the database.
 * @see SignupActivity
 */
public class LoginActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    /**
     * The instance of the database (Firebase).
     */
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * The reference of the root in the database.
     */
    private DatabaseReference referenceData = database.getReference();
    /**
     * The login of the user.
     */
    private EditText pseudo;
    /**
     * The password of the user.
     */
    private EditText password;
    /**
     * Event that launch the Account Activity if the user exists and the password match.
     */
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;

                //Connect if the login exists, and the passeword is the good one
                if (user.getPassword().equals(getPassord())) {
                    //Lauch Account Acitvity and pass the login
                    Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                    intent.putExtra("pseudo",  getPseudo());
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Le mot de passe ne correspond" +
                            " pas, veuillez réessayer", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Ce pseudo n'existe pas, " +
                        "veuillez réessayer", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Never used
        }
    };

    //_________________________________________methods______________________________________________
    /**
     * Create the fields required. Buttons are setting up and linked to their function.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Recuperation of both EditText
        this.password = findViewById(R.id.password);
        this.pseudo = findViewById(R.id.pseudo);

        //Setting of the button
        Button btn_validate = findViewById(R.id.validate_connect);
        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referenceData.child(getPseudo()).addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }

    /**
     * Get the login of the user.
     * @return the login.
     */
    private String getPseudo() {
        return this.pseudo.getText().toString();
    }

    /**
     * Get the password of the user.
     * @return the password.
     */
    private String getPassord() {
        return this.password.getText().toString();
    }

    /**
     * Get back to the home page.
     */
    public void backToHome(View view) {
        //Redirect to the home page
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
