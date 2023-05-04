package hvl.bachelor.omkring;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.provider.Settings;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        // Check if the notification permission is granted
        if (notificationManager != null && notificationManager.areNotificationsEnabled()) {
            // The notification permission is granted
            // You can perform your desired actions here
        } else {
            // The notification permission is not granted
            // Request the user to grant the notification permission
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());

            // Check if there are activities that can handle this intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Show a message that the notification settings cannot be opened
                Toast.makeText(this, "Unable to open notification settings", Toast.LENGTH_SHORT).show();
            }
        }
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
