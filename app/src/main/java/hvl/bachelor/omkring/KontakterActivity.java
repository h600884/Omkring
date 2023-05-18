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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class KontakterActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnAddFriend;

    private DatabaseReference friendsRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontakter);

        etEmail = findViewById(R.id.etEmail);
        btnAddFriend = findViewById(R.id.btnAddFriend);

        // Get the current user from Firebase Authentication
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the "friends" node reference in the Firebase Realtime Database
        friendsRef = FirebaseDatabase.getInstance().getReference("friends")
                .child(currentUser.getUid());

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
    }

    private void addFriend() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here you can perform additional validations or checks before adding the friend

        // Create a new friend entry using the friend's email as the key
        DatabaseReference newFriendRef = friendsRef.child(email.replace(".", ","));

        // Set a value in the friend entry, such as "true" to indicate a friendship
        newFriendRef.setValue(true);

        Toast.makeText(this, "Friend added successfully", Toast.LENGTH_SHORT).show();

        // Clear the input field
        etEmail.setText("");
    }
}
