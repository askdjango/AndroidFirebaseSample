package kr.festi.androidfirebasesample;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    GoogleApiClient mGoogleApiClient;

    String mUsername;

    RecyclerView mChatMessageListRecyclerView;
    View mChatMessageListEmptyView;
    LinearLayoutManager mLinearLayoutManager;
    DatabaseReference mFirebaseDatabaseReference;
    FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> mFirebaseAdapter;

    final String TAG = MainActivity.class.getName();
    final String CHAT_MESSAGES_CHILD = "chat_messages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        /*
         * Firebase Realtime Database
         */
        mChatMessageListRecyclerView = (RecyclerView) findViewById(R.id.messageListRecyclerView);
        mChatMessageListEmptyView = findViewById(R.id.messageListEmptyView);
        mChatMessageListEmptyView.setVisibility(View.GONE);  // FIXME

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mChatMessageListRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(
                ChatMessage.class,
                R.layout.item_chat_message,
                ChatMessageViewHolder.class,
                mFirebaseDatabaseReference.child(CHAT_MESSAGES_CHILD)) {
            @Override
            protected void populateViewHolder(ChatMessageViewHolder viewHolder, ChatMessage chatMessage, int position) {
                viewHolder.messageTextView.setText(chatMessage.message);
                viewHolder.nameTextView.setText(chatMessage.name);
                if ( chatMessage.photoUrl != null ) {
                    viewHolder.photoImageView.setVisibility(View.VISIBLE);
                    Glide.with(MainActivity.this)
                            .load(chatMessage.photoUrl)
                            .into(viewHolder.photoImageView);
                }
                else {
                    viewHolder.photoImageView.setVisibility(View.GONE);
                }
            }
        };
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);

                int chatMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if ( lastVisiblePosition == -1 ||
                        (positionStart >= (chatMessageCount - 1) &&
                                (lastVisiblePosition == (positionStart - 1))) ) {
                    mChatMessageListRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mChatMessageListRecyclerView.setLayoutManager(mLinearLayoutManager);
        mChatMessageListRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if ( itemId == R.id.sign_out_menu ) {
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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView nameTextView;
        ImageView photoImageView;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            photoImageView = (ImageView) itemView.findViewById(R.id.photoImageView);
        }
    }
}
