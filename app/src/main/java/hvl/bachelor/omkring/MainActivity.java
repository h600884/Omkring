package hvl.bachelor.omkring;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText inputEpost, inputPassord;

    Button loggInnButton;

    final String epostPattern = "^(.+)@(\\S+)$";

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hvis innlogget, goto dashboard
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(MainActivity.this, DashbordActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        setContentView(R.layout.activity_main);


        inputEpost = findViewById(R.id.inputEpostLoggInn);
        inputPassord = findViewById(R.id.inputPassordLoggInn);
        loggInnButton = findViewById(R.id.loggInnButton);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loggInnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utfoorAuth();
            }
        });
    }

    private void utfoorAuth() {
        String epost = inputEpost.getText().toString();
        String passord = inputPassord.getText().toString();

        if(!epost.matches(epostPattern)){
            inputEpost.setError("Skriv epost rett!");
        } else if (passord.isEmpty() || passord.length() < 6) {
            inputPassord.setError("Skriv passord rett!");
        }else {
            progressDialog.setMessage("Vent mens bruker blir logget inn...");
            progressDialog.setTitle("Logger Inn");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(epost, passord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        sendBrukerTilNesteAktivitet();
                        Toast.makeText(MainActivity.this, "Innlogging Fullført", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendBrukerTilNesteAktivitet() {
        Intent intent = new Intent(MainActivity.this, DashbordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void glemtPassord(View view){
        Intent intent = new Intent(this, GlemtPassordActivity.class);
        startActivity(intent);
    }

    public void registrer(View view){
        Intent intent = new Intent(this, RegistrerActivity.class);
        startActivity(intent);
    }
}