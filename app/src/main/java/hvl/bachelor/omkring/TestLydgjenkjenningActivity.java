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

    public class TestLydgjenkjenningActivity extends AppCompatActivity {

        protected TextView outputTextView;
        protected TextView specsTextView;
        protected Button startRecordingButton;
        protected Button stopRecordingButton;
        protected TextView oppdaget;

        private String model = "model.tflite";

        private AudioRecord audioRecord;
        private TimerTask timerTask;
        private AudioClassifier audioClassifier;
        private TensorAudio tensorAudio;

        float sannsynlighet = 0.8f;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_test_lydgjenkjenning);

            outputTextView = findViewById(R.id.audio_output_textview);
            specsTextView = findViewById(R.id.audio_specs_textview);
            startRecordingButton = findViewById(R.id.start_lyd_gjenkjenning);
            stopRecordingButton = findViewById(R.id.stop_lyd_gjenkjenning);
            oppdaget = findViewById(R.id.oppdaget);

            stopRecordingButton.setEnabled(false);

            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            }

            try {
                audioClassifier = AudioClassifier.createFromFile(this, model);
            } catch (IOException e){
                e.printStackTrace();
            }

            tensorAudio = audioClassifier.createInputTensorAudio();
        }

        public void startLydgjenkjenning(View view){
            startRecordingButton.setEnabled(false);
            stopRecordingButton.setEnabled(true);

            TensorAudio.TensorAudioFormat format = audioClassifier.getRequiredTensorAudioFormat();
            String specs = "Number of channels: " + format.getChannels() + "\n"
                    + "Sample Rate: " + format.getSampleRate();
            specsTextView.setText(specs);

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

                    // Filtering out classifications with low probability
                    List<Category> finalOutput = new ArrayList<>();
                    for (Classifications classifications : output) {
                        for (Category category : classifications.getCategories()) {
                            if (category.getScore() > sannsynlighet) {
                                finalOutput.add(category);
                            }
                        }
                    }

                    // Sorting the results
                    Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));

                    // Creating a multiline string with the filtered results
                    StringBuilder outputStr = new StringBuilder();
                    for (Category category : finalOutput) {
                        outputStr.append(category.getLabel())
                                .append(": ").append(category.getScore()).append("\n");
                    }

                    // Updating the UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalOutput.isEmpty()) {
                                outputTextView.setText("Could not classify");
                            } else {
                                outputTextView.setText(outputStr.toString());
                                for (Category category : output.get(1).getCategories()) {
                                    if (category.getLabel().equals("SmokeDetector")  && category.getScore() > sannsynlighet) {
                                        oppdaget.setText("Røykvarsler Oppdaget!");
                                    }
                                }
                            }
                        }
                    });
                }
            };

            new Timer().scheduleAtFixedRate(timerTask, 1, 500);
        }

        public void stopLydgjenkjenning(View view){
            startRecordingButton.setEnabled(true);
            stopRecordingButton.setEnabled(false);

            timerTask.cancel();
            audioRecord.stop();
        }
    }