package hvl.bachelor.omkring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegistrerActivity extends AppCompatActivity {

    EditText inputNavn, inputEpost, inputPassord, inputBekreftPassord;

    Button registrerButton;

    final String epostPattern = "^(.+)@(\\S+)$";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer);

        inputNavn = findViewById(R.id.navn);
        inputEpost = findViewById(R.id.inputEpost);
        inputPassord = findViewById(R.id.inputPassord);
        inputBekreftPassord = findViewById(R.id.inputBekreftPassord);
        registrerButton = findViewById(R.id.registrerButton);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Brukere" );

        registrerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utfoorAuth();
            }
        });
    }

    private void utfoorAuth() {
        String navn = inputNavn.getText().toString();
        String epost = inputEpost.getText().toString();
        String passord = inputPassord.getText().toString();
        String bekreftPassord = inputBekreftPassord.getText().toString();

        if(navn.isEmpty()){
            inputNavn.setError("Skriv inn navn");
        } else if(!epost.matches(epostPattern)){
            inputEpost.setError("Skriv epost rett!");
        } else if (passord.isEmpty() || passord.length() < 6) {
            inputPassord.setError("Skriv passord rett!");
        } else if (!passord.equals(bekreftPassord)) {
            inputBekreftPassord.setError("Skriv samme passord!");
        }else{
            progressDialog.setMessage("Vent mens bruker blir registrert...");
            progressDialog.setTitle("Registrer");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(epost,passord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful()){
                         progressDialog.dismiss();

                         // Lagre bruker i databasen
                         String userId = mAuth.getCurrentUser().getUid();
                         mRef.child(userId).child("email").setValue(epost);
                         mRef.child(userId).child("navn").setValue(navn);

                         FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                             @Override
                             public void onSuccess(String deviceToken) {
                                 // Update the device token in the Realtime Database
                                 mRef.child(userId).child("deviceToken").setValue(deviceToken);
                             }
                         });


                         sendBrukerTilNesteAktivitet();
                         Toast.makeText(RegistrerActivity.this, "Registrering fullført", Toast.LENGTH_SHORT).show();
                     }else {
                         progressDialog.dismiss();
                         Toast.makeText(RegistrerActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                     }
                }
            });
        }
    }

    private void sendBrukerTilNesteAktivitet() {
        Intent intent = new Intent(RegistrerActivity.this, DashbordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}