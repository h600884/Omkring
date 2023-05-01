package hvl.bachelor.omkring;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.IBinder;

import androidx.annotation.Nullable;

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

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            audioClassifier = AudioClassifier.createFromFile(this, model);
        } catch (IOException e){
            e.printStackTrace();
        }
        tensorAudio = audioClassifier.createInputTensorAudio();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
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

        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Classifying audio data
                // val numberOfSamples = tensor.load(record)
                // val output = classifier.classify(tensor)
                int numberOfSamples = tensorAudio.load(audioRecord);
                List<Classifications> output = audioClassifier.classify(tensorAudio);

                for (Category category : output.get(1).getCategories()) {
                    if (category.getLabel().equals("SmokeDetector")  && category.getScore() > probabilityThreshold) {
                        alarm();
                    }
                }
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, 1, 500);

        return super.onStartCommand(intent, flags, startId);
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