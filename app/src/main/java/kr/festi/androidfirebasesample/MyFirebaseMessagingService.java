package kr.festi.androidfirebasesample;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    static final String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 앱이 실행 중일 때 (Foreground 상황) 에서 푸쉬를 받으면 호출됩니다.
        // 백그라운드 상황에서는 호출되지 않고 그냥 알림목록에 알림이 추가됩니다.

        if ( remoteMessage.getData().size() > 0 ) {
            Log.d(TAG, "FCM Data Message : " + remoteMessage.getData());
            String messageBody = remoteMessage.getData().get("message_body");
            if ( messageBody != null ) {
                toast(messageBody);
            }
        }

        if ( remoteMessage.getNotification() != null ) {
            final String messageBody = remoteMessage.getNotification().getBody();
            Log.d(TAG, "FCM Notification Message Body : " + messageBody);
            toast(messageBody);
        }
    }

    void toast(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

