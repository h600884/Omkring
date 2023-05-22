package hvl.bachelor.omkring;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class KontakterActivity extends AppCompatActivity {

    private EditText inputEpost;
    private Button btnAddFriend;

    final String epostPattern = "^(.+)@(\\S+)$";

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mUsersRef;
    DatabaseReference mFriendsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontakter);

        inputEpost = findViewById(R.id.inputEpostLeggTil);
        btnAddFriend = findViewById(R.id.btnLeggTill);

        // Hent nåværende bruker fra firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUsersRef = FirebaseDatabase.getInstance().getReference("Brukere" );
        mFriendsRef = FirebaseDatabase.getInstance().getReference("Kontakter" );

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leggTilKontakt();
            }
        });
    }

    private void leggTilKontakt() {
        String email = inputEpost.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !email.matches(epostPattern)) {
            Toast.makeText(this, "Bruk en gyldig epostadresse", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        mUsersRef.child(userId);

        getUserIdFromEmail(email, new UserIdCallback() {
            @Override
            public void onUserIdReceived(String friendId) {
                if (friendId == null) {
                    // Handle the case when the friend does not exist
                    return;
                }

                mFriendsRef.child(userId).child(friendId).setValue(true);
                mFriendsRef.child(friendId).child(userId).setValue(true);
            }
        });
    }

    private void getUserIdFromEmail(String email, final UserIdCallback callback) {
        Query query = mUsersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userId = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userId = snapshot.getKey(); // Retrieve the user ID from the snapshot
                    break; // Assuming there is only one user with the given email, exit the loop after finding the first match
                }
                callback.onUserIdReceived(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
                callback.onUserIdReceived(null); // Notify the callback with null value
            }
        });
    }

    interface UserIdCallback {
        void onUserIdReceived(String userId);
    }
}
