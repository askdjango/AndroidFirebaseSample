package kr.festi.androidfirebasesample;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    GoogleApiClient mGoogleApiClient;

    String mUsername;

    final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Signout ?")
                        .setPositiveButton("signout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mFirebaseAuth.signOut();
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                LoginManager.getInstance().logOut();

                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser == null ) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            mUsername = mFirebaseUser.getDisplayName();
            if ( mFirebaseUser.getPhotoUrl() != null ) {
                String photoUrl = mFirebaseUser.getPhotoUrl().toString();

                ImageView photoImageView = (ImageView) findViewById(R.id.photo_imageview);
                Glide.with(this).load(photoUrl).into(photoImageView);
            }

            TextView usernameTextView = (TextView) findViewById(R.id.username_textview);
            usernameTextView.setText(mUsername);

            Toast.makeText(this, mUsername + "님 환영합니다.", Toast.LENGTH_SHORT).show();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
