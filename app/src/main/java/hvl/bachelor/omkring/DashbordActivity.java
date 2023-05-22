package hvl.bachelor.omkring;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.provider.Settings;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DashbordActivity extends AppCompatActivity {

    // Knapper
    protected Button geolokasjonButton;
    protected Button testLydgjenkjenningButton;
    protected Button startRecordingButton;
    protected Button stopRecordingButton;
    protected Button kontakterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashbord);

        // Definere input knapper
        geolokasjonButton = findViewById(R.id.set_geo_lokasjon);
        testLydgjenkjenningButton = findViewById(R.id.test_Lydgjenkjenning);
        startRecordingButton = findViewById(R.id.start_lyd_gjenkjenning);
        stopRecordingButton = findViewById(R.id.stop_lyd_gjenkjenning);
        kontakterButton = findViewById(R.id.kontakter);

        // Starter uten opptak
        stopRecordingButton.setEnabled(false);

        // Hvis foreground service kjører, endre knappene til på
        if(foregroundserviceKjoorer(this, LydgjenkjenningForegroundService.class)){
            startRecordingButton.setEnabled(false);
            stopRecordingButton.setEnabled(true);
        }

        // Mikrofon tilgang
        requestMikrofonTilgang();

        // Notification tilgang
        requestNotificationTilgang();

        // Geolokasjon tilgang
        requestLocationTilgang();
    }

    // Metode som returnerer true om foregroundservicen kjører, false ellers
    public static boolean foregroundserviceKjoorer(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }

            }
        }
        return false;
    }

    // Metode som henter brukerens lokasjon
    private Location hentLokasjon() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        float latitude = sharedPreferences.getFloat("last_latitude", 0);
        float longitude = sharedPreferences.getFloat("last_longitude", 0);
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    // Metode for å lagre nåværende posisjon
    private void lagreNåvaerendePosisjon(Location location) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("last_latitude", (float) location.getLatitude());
        editor.putFloat("last_longitude", (float) location.getLongitude());
        editor.apply();
    }

    // Lagrer nåværende posisjon som brukerens "hjem"
    public void setGeolokasjon(View view){
        lagreNåvaerendePosisjon(hentLokasjon());
    }

    // Sende brukeren videre til test lydgjenkjenning aktiviteten
    public void testLydgjenkjenning(View view){
        Intent intent = new Intent(this, TestLydgjenkjenningActivity.class);
        startActivity(intent);
    }

    // spør om tilgang til mikrofonen
    private void requestMikrofonTilgang(){
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    // spør om tilgang til å sende varsler
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

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    // spør om tilgang til geolokasjon
    private void requestLocationTilgang() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Tilgang til geolokasjon gitt
        } else {
            // Lokasjontilgang ikke gitt
            // Spør om tilgang til lokasjon
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
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
