package hvl.bachelor.omkring;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LydgjenkjenningForegroundService extends Service {
    private static final String TAG = "MyForegroundService";
    private final IBinder binder = new MyBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new Notification());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        LydgjenkjenningForegroundService getService() {
            return LydgjenkjenningForegroundService.this;
        }
    }
}
