package hvl.bachelor.omkring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InnstillingerActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_innstillinger);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Button signOutButton = findViewById(R.id.loggUtButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loggUt();
            }
        });
    }

    private void loggUt() {
        // Sign out the user from Firebase Authentication
        mAuth.signOut();

        // Start the MainActivity and clear the back stack
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}