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

public class LoginActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private EditText pseudo;
    private EditText password;


    //_________________________________________methods______________________________________________
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

    ValueEventListener valueEventListener = new ValueEventListener() {
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

    private String getPseudo() {
        return this.pseudo.getText().toString();
    }

    private String getPassord() {
        return this.password.getText().toString();
    }

    public void backToHome(View view) {
        //Redirect to the home page
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
