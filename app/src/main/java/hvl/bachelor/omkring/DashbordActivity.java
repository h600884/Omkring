package hvl.bachelor.omkring;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashbordActivity extends AppCompatActivity {

    // Input & output
    protected Button startRecordingButton;
    protected Button stopRecordingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set dashbord view
        setContentView(R.layout.activity_dashbord);

        // Definere input knapper
        startRecordingButton = findViewById(R.id.start_lyd_gjenkjenning);
        stopRecordingButton = findViewById(R.id.stop_lyd_gjenkjenning);

        // Starter uten opptak
        stopRecordingButton.setEnabled(false);

        // Mikrofon tilgang
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }

        // Notification tilgang

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
