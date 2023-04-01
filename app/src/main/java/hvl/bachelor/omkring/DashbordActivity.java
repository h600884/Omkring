package hvl.bachelor.omkring;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DashbordActivity extends AppCompatActivity {

    protected TextView outputTextView;
    protected TextView specsTextView;
    protected Button startRecordingButton;
    protected Button stopRecordingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        outputTextView = findViewById(R.id.audio_output_textview);
        specsTextView = findViewById(R.id.audio_specs_textview);
        startRecordingButton = findViewById(R.id.start_lyd_gjenkjenning);
        stopRecordingButton = findViewById(R.id.stop_lyd_gjenkjenning);

        stopRecordingButton.setEnabled(false);

        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    public void startLydgjenkjenning(View view){
        startRecordingButton.setEnabled(false);
        stopRecordingButton.setEnabled(true);
    }

    public void stopLydgjenkjenning(View view){
        startRecordingButton.setEnabled(true);
        stopRecordingButton.setEnabled(false);
    }
}
