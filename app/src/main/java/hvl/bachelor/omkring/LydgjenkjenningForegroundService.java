package hvl.bachelor.omkring;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioRecord;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LydgjenkjenningForegroundService extends Service {

    private final String model = "model.tflite";

    // Lyd opptak, timer, klassifiserings
    private AudioRecord audioRecord;
    private TimerTask timerTask;
    private AudioClassifier audioClassifier;
    private TensorAudio tensorAudio;

    // Sannsynlighet for at lyden er røykvarsler
    private final float probabilityThreshold = 0.6f;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mUsersRef;
    DatabaseReference mFriendsRef;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            audioClassifier = AudioClassifier.createFromFile(this, model);
        } catch (IOException e){
            e.printStackTrace();
        }
        tensorAudio = audioClassifier.createInputTensorAudio();

        // Get the current user from Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUsersRef = FirebaseDatabase.getInstance().getReference("Users");
        mFriendsRef = FirebaseDatabase.getInstance().getReference("Friends");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_HIGH
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText(".")
                .setContentTitle("Lydgjenkjenning kjører")
                .setSmallIcon(R.drawable.ic_notification);

        startForeground(1001, notification.build());

        // Creating and start recording
        audioRecord = audioClassifier.createAudioRecord();
        audioRecord.startRecording();

        // Check if the user is at home before executing the timer task
        float maxDistanceFromHome = 50; // In meters
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Location currentLocation = getLastKnownLocation();
                if (currentLocation != null && brukerErHjemme(currentLocation, maxDistanceFromHome)) {
                    // Classify audio data if the user is at home
                    // val numberOfSamples = tensor.load(record)
                    // val output = classifier.classify(tensor)
                    int numberOfSamples = tensorAudio.load(audioRecord);
                    List<Classifications> output = audioClassifier.classify(tensorAudio);

                    for (Category category : output.get(1).getCategories()) {
                        if (category.getLabel().equals("SmokeDetector") && category.getScore() > probabilityThreshold) {
                            alarm();
                        }
                    }
                }
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, 1, 500);

        return super.onStartCommand(intent, flags, startId);
    }

    private Location getLastKnownLocation() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        float latitude = sharedPreferences.getFloat("last_latitude", 0);
        float longitude = sharedPreferences.getFloat("last_longitude", 0);
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    private boolean brukerErHjemme(Location currentLocation, float maxDistanceFromHome) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the last saved location
        double lastLatitude = sharedPreferences.getFloat("last_latitude", 0);
        double lastLongitude = sharedPreferences.getFloat("last_longitude", 0);
        Location homeLocation = new Location("");
        homeLocation.setLatitude(lastLatitude);
        homeLocation.setLongitude(lastLongitude);

        // Check if the current location is within the maximum distance from the home location
        float distance = currentLocation.distanceTo(homeLocation);
        return distance <= maxDistanceFromHome;
    }


    public void alarm() {
        // Varsle brukeren
        varsleBruker();

        // Hvis brukeren har kontakter
        // Varsler kontakter
        varsleKontakter();
    }

    private void varsleBruker(){
        // Sender en push notification til brukeren
        NotificationHelper.sendNotification(
                this,
                "Røykvarsler Oppdaget",
                "Lydgjenkjenningen oppdaget lyden av en røykvarsler");
    }

    private void varsleKontakter(){
        // For hver kontakt brukeren har
        // Send varsel til kontakten om at røykvarsleren har gått av hos brukeren
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}