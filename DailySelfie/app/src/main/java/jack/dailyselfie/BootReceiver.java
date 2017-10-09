package jack.dailyselfie;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class BootReceiver extends BroadcastReceiver {

    public final String TRIGGER_ALARM = "jack.TRIGGER_ALARM";
    final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    final int NOTIFICATION_ID = 1;
    PendingIntent alarmIntent = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(ACTION_BOOT_COMPLETED)) {
            setupRepeatingAlarm(context);
        }
        else if(action.equals(TRIGGER_ALARM)) {
            PendingIntent notifyIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder mBuilder = new Notification.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .setContentTitle("Selfie Time")
                    .setContentText("It's time to take another selfie")
                    .setContentIntent(notifyIntent)
                    .setAutoCancel(true);

            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    protected void setupRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(alarmIntent == null) {
            alarmIntent = PendingIntent.getBroadcast(context, 2, new Intent(TRIGGER_ALARM), 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 10 * 1000, 2 * 60 * 1000, alarmIntent);
        }
    }
}
