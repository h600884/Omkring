package hvl.bachelor.omkring;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GlemtPassordActivity extends AppCompatActivity {

    EditText inputEpost;

    Button tilbakestillButton;

    final String epostPattern = "^(.+)@(\\S+)$";

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glemt_passord);

        inputEpost = findViewById(R.id.inputEpostTilbakestill);
        tilbakestillButton = findViewById(R.id.tilbakestillButton);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tilbakestillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tilbakestillPassord();
            }
        });
    }

    // Metode for å tilbakestille passordet
    private void tilbakestillPassord() {
        String email = inputEpost.getText().toString().trim();

        // Validerer eposten med et regulært utrykk
        if (!email.matches(epostPattern)) {
            inputEpost.setError("Ugyldig e-postadresse");
            inputEpost.requestFocus();
            return;
        }

        progressDialog.setMessage("Sender instruksjoner for tilbakestilling av passord...");
        progressDialog.show();

        // Sender en epost med forespørsel om å tilbakestille passord
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(GlemtPassordActivity.this, "Instruksjoner for tilbakestilling av passord er sendt til " + email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GlemtPassordActivity.this, "Feil ved sending av instruksjoner. Vennligst prøv igjen senere.", Toast.LENGTH_LONG).show();
                    }
                });
    }

}