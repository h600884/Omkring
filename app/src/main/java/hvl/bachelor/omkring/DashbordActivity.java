package hvl.bachelor.omkring;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;;
import androidx.core.content.ContextCompat;

public class DashbordActivity extends AppCompatActivity {

    // Knapper
    protected Button geolokasjonButton;
    protected Button testLydgjenkjenningButton;
    protected Button startRecordingButton;
    protected Button stopRecordingButton;
    protected Button kontakterButton;
    protected Button innstillingerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set dashbord view
        setContentView(R.layout.activity_dashbord);

        // Definere input knapper
        geolokasjonButton = findViewById(R.id.set_geo_lokasjon);
        testLydgjenkjenningButton = findViewById(R.id.test_Lydgjenkjenning);
        startRecordingButton = findViewById(R.id.start_lyd_gjenkjenning);
        stopRecordingButton = findViewById(R.id.stop_lyd_gjenkjenning);
        kontakterButton = findViewById(R.id.kontakter);
        innstillingerButton = findViewById(R.id.innstillinger);

        // Starter uten opptak
        stopRecordingButton.setEnabled(false);

        // Mikrofon tilgang
        requestMikrofonTilgang();

        // Notification tilgang
        requestNotificationTilgang();
    }

    public void setGeolokasjon(View view){

    }

    public void testLydgjenkjenning(View view){
        Intent intent = new Intent(this, TestLydgjenkjenningActivity.class);
        startActivity(intent);
    }

    private void requestMikrofonTilgang(){
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    private void requestNotificationTilgang(){

    }

    public void startLydgjenkjenning(View view){
        startRecordingButton.setEnabled(false);
        stopRecordingButton.setEnabled(true);

        Intent serviceIntent = new Intent(this, LydgjenkjenningForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopLydgjenkjenning(View view){
        startRecordingButton.setEnabled(true);
        stopRecordingButton.setEnabled(false);

        Intent serviceIntent = new Intent(this, LydgjenkjenningForegroundService.class);
        stopService(serviceIntent);
    }

    public void kontakter(View view){
        Intent intent = new Intent(this, KontakterActivity.class);
        startActivity(intent);
    }

    public void innstillinger(View view){
        Intent intent = new Intent(this, InnstillingerActivity.class);
        startActivity(intent);
    }
}
