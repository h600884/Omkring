package hvl.bachelor.omkring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hvis innlogget, goto dashboard

        setContentView(R.layout.activity_main);
    }

    public void loggInn(View view){
        Intent intent = new Intent(this, DashbordActivity.class);
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