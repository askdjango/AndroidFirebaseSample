package kr.festi.androidfirebasesample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    String mUsername;
    String mPhotoUrl;

    final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser == null ) {
            // FIXME: 로그인이 필요합니다.
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
        }
        else {
            mUsername = mFirebaseUser.getDisplayName();
            if ( mFirebaseUser.getPhotoUrl() != null ) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }
}
