package com.atum.tunetube;

/**
 * Created by madtu on 26/05/2018.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import static com.atum.tunetube.NotificationService.NOTIFICATION_ID.FOREGROUND_SERVICE;

public class NotificationService extends Service {

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(ACTION.PAUSE_ACTION)) {
            Toast.makeText(this, "Clicked PAUSE_ACTION", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked PAUSE_ACTION");
            sendPlayerAction(MainActivity.PLAYER_ACTION_PAUSE);
        } else if (intent.getAction().equals(ACTION.PLAY_ACTION)) {
            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Play");
            sendPlayerAction(MainActivity.PLAYER_ACTION_PLAY);
        } else if (intent.getAction().equals(ACTION.NEXT_ACTION)) {
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Next");

            sendPlayerAction(MainActivity.PLAYER_ACTION_NEXT_TRACK);
        }/* else if (intent.getAction().equals(
                ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }*/
        return START_STICKY;
    }

    Notification status;
    Notification.Builder builder;
    private final String LOG_TAG = "NotificationService";
    private RemoteViews notificationExpandedView;
    private LocalBroadcastManager bManager;
    private NotificationManager mNotificationManager;

    public void sendPlayerAction(String action) {
        Intent RTReturn = new Intent(MainActivity.PLAYER_ACTION);
        RTReturn.putExtra("action", action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn);
    }

    public interface ACTION {
        String MAIN_ACTION = "com.atum.tunetube.notification.action.main";
        String PAUSE_ACTION = "com.atum.tunetube.notification.action.pause";
        String PLAY_ACTION = "com.atum.tunetube.notification.action.play";
        String NEXT_ACTION = "com.atum.tunetube.notification.action.next";
        String STARTFOREGROUND_ACTION = "START";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    /*public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_album_art, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }*/
    private void showNotification() {
// Using RemoteViews to bind custom layouts into Notification
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationExpandedView = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent pauseIntent = new Intent(this, NotificationService.class);
        pauseIntent.setAction(ACTION.PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0,
                pauseIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        /*Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);*/

        notificationExpandedView.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        notificationExpandedView.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        notificationExpandedView.setOnClickPendingIntent(R.id.status_bar_pause, ppauseIntent);

        /*notificationSmallView.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        notificationExpandedView.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);*/

        notificationExpandedView.setTextViewText(R.id.status_bar_track_name, "Song Title");
        String channelId = "TestTubeNotification";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channelId);
        } else {
            builder = new Notification.Builder(this);
        }

        status = builder.build();
        status.contentView = notificationExpandedView;
        status.bigContentView = notificationExpandedView;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_launcher;
        status.contentIntent = pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Test Tube notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            mNotificationManager.createNotificationChannel(channel);
        }

        startForeground(FOREGROUND_SERVICE, status);

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.PLAYER_ACTION);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MainActivity.PLAYER_ACTION)) {
                String playerAction = intent.getStringExtra("action");
                if(playerAction == null){
                    return;
                }
                switch(playerAction){
                    case MainActivity.UPDATE_TEXT_ACTION:
                        String text = intent.getStringExtra("text");
                        Log.i(LOG_TAG,"attempting to update notification to: "+text);
                        notificationExpandedView.setTextViewText(R.id.status_bar_track_name, text);
                        mNotificationManager.notify(FOREGROUND_SERVICE, builder.build());
                        break;
                }
            }
        }
    };

}
