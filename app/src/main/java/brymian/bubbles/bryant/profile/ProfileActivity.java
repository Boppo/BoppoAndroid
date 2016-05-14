package brymian.bubbles.bryant.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import brymian.bubbles.R;
import brymian.bubbles.bryant.map.MapsActivity;
import brymian.bubbles.bryant.nonactivity.SaveSharedPreference;
import brymian.bubbles.bryant.friends.FriendsList;
import brymian.bubbles.damian.nonactivity.ServerRequest.Callback.ImageListCallback;
import brymian.bubbles.damian.nonactivity.ServerRequest.Callback.StringCallback;
import brymian.bubbles.damian.nonactivity.ServerRequest.UserImageRequest;
import brymian.bubbles.damian.nonactivity.ServerRequestMethods;
import brymian.bubbles.objects.Image;
import brymian.bubbles.objects.User;
import brymian.bubbles.damian.nonactivity.ServerRequest.Callback.UserCallback;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    public static ImageView ivProfilePictures;
    TextView tvProfileUsername, tvProfileFirstLastName;
    FloatingActionButton fabMessage, fabStatusAction, fabMap, fabEpisodes;
    FloatingActionMenu fabMenu;
    int userUID;
    String profile;
    String firstName, lastName, privacy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        /*--------------------------------Checking for putExtras()--------------------------------*/
        String profile;
        int uid;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                profile = null;
                uid = 0;
            }
            else {
                profile = extras.getString("profile");
                uid = extras.getInt("uid");
            }
        }
        else {
            profile= savedInstanceState.getString("profile");
            uid = savedInstanceState.getInt("uid");
        }
        /*----------------------------------------------------------------------------------------*/

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("");
        mToolbar.bringToFront();
        ivProfilePictures = (ImageView) findViewById(R.id.ivProfilePictures);
        tvProfileUsername = (TextView) findViewById(R.id.tvProfileUsername);
        tvProfileFirstLastName = (TextView) findViewById(R.id.tvProfileFirstLastName);

        getProfilePictures(uid);
        fabMessage = (FloatingActionButton) findViewById(R.id.fabMessage);
        fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Under construction: Message", Toast.LENGTH_SHORT).show();
            }
        });

        fabStatusAction = (FloatingActionButton) findViewById(R.id.fabStatusAction);
        fabStatusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        fabEpisodes = (FloatingActionButton) findViewById(R.id.fabEpisodes);
        fabEpisodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fabMap = (FloatingActionButton) findViewById(R.id.fabMap);
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        if (profile != null) {
            if (profile.equals("logged in user")) {
                setUID(SaveSharedPreference.getUserUID(this));
                tvProfileUsername.setText(SaveSharedPreference.getUsername(this));
                tvProfileFirstLastName.setText(SaveSharedPreference.getUserFirstName(this) + " " + SaveSharedPreference.getUserLastName(this));
                setButtons(profile);
                setProfile(profile);
            }
            else {
                setUID(uid);
                setButtons(profile);
                setProfile(profile);
                Toast.makeText(ProfileActivity.this, profile, Toast.LENGTH_SHORT).show();
                new ServerRequestMethods(this).getUserData(uid, new UserCallback() {
                    @Override
                    public void done(User user) {
                        setFirstName(user.getFirstName());
                        setLastName(user.getLastName());
                        tvProfileFirstLastName.setText(user.getFirstName() + " " + user.getLastName());
                        tvProfileUsername.setText(user.getUsername());
                        setPrivacy(user.getUserAccountPrivacy());
                    }
                });
            }
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabMessage:
                if(getProfile().equals("logged in user")){
                    startActivity(new Intent(this, MapsActivity.class).putExtra("profile", "logged in user"));
                }
                else if(getPrivacy().equals("Private")){
                    Toast.makeText(ProfileActivity.this, "User account is private", Toast.LENGTH_SHORT).show();
                }
                else if(getPrivacy().equals("Public")){
                    startActivity(new Intent(this, MapsActivity.class)
                            .putExtra("uid", getUID())
                            .putExtra("profile", getFirstName() + " " + getLastName()));
                }
                break;
            case R.id.fabStatusAction:
                if(getProfile().equals("logged in user")){
                    startActivity(new Intent(this, FriendsList.class)
                            .putExtra("uid", getUID())
                            .putExtra("profile", "logged in user"));
                }
                else if(getProfile().equals("Already friends with user.")){
                    startActivity(new Intent(this, FriendsList.class)
                            .putExtra("uid", getUID())
                            .putExtra("profile", getFirstName() + " " + getLastName()));
                }
                else if(getProfile().equals("Not friends.")){
                    new ServerRequestMethods(this).setFriendStatus(SaveSharedPreference.getUserUID(this), getUID(), new StringCallback() {
                    @Override
                    public void done(String string) {
                        Toast.makeText(ProfileActivity.this, string, Toast.LENGTH_SHORT).show();
                        System.out.println("string from setFriendStatus: " + string);
                        }
                    });
                }
                else if(getProfile().equals("Already sent friend request to user.")){
                    Toast.makeText(ProfileActivity.this, "Already sent friend request to user.", Toast.LENGTH_SHORT).show();
                }
                else if (getProfile().equals("User is awaiting confirmation for friend request.")){
                    new ServerRequestMethods(this).setFriendStatus(SaveSharedPreference.getUserUID(this), getUID(), new StringCallback() {
                        @Override
                        public void done(String string) {
                            Toast.makeText(ProfileActivity.this, string, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;


            case R.id.fabMenu:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setButtons(String friendStatus) {
        fabMessage.setImageResource(R.mipmap.ic_chat_bubble_outline_black_24dp);
        fabMap.setImageResource(R.mipmap.ic_public_black_24dp);
        fabEpisodes.setImageResource(R.mipmap.ic_my_location_black_24dp);
        switch (friendStatus){
            case "logged in user":
                fabStatusAction.setImageResource(R.mipmap.ic_people_outline_black_24dp);
                break;
            case "Already friends with user.":
                fabStatusAction.setImageResource(R.mipmap.ic_people_outline_black_24dp);
                break;
            case "Already sent friend request to user.":
                fabStatusAction.setImageResource(R.mipmap.ic_more_horiz_black_24dp);
                break;
            case "User is awaiting confirmation for friend request.":
                fabStatusAction.setImageResource(R.mipmap.ic_more_horiz_black_24dp);
                break;
            case "Not friends.":
                fabStatusAction.setImageResource(R.mipmap.ic_person_add_black_24dp);
                break;
            case "User is currently being blocked.":

                break;
            case "Currently being blocked by user.":

                break;
            default:
        }
    }

    private void getProfilePictures(int uid){
        new UserImageRequest(this).getImagesByUidAndPurpose(uid, "Profile", new ImageListCallback() {
            @Override
            public void done(List<Image> imageList) {
                if (imageList.size() > 0) {
                    for (int i = 0; i < imageList.size(); i++) {
                        new DownloadImage(imageList.get(i).userImagePath).execute();
                    }
                }
            }
        });
    }

    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String path;
        int location;
        public DownloadImage(String path){
            this.path = path;
            //this.location = location;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URLConnection connection = new URL(path).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);
                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap!=null){
                ivProfilePictures.setImageBitmap(bitmap);
            }
        }
    }

    private void setUID(int uid){
        this.userUID = uid;
    }

    private int getUID(){
        return userUID;
    }

    private void setProfile(String profile){
        this.profile = profile;
    }

    private String getProfile(){
        return profile;
    }

    private void setFirstName(String firstName){
        this.firstName = firstName;
    }

    private String getFirstName(){
        return firstName;
    }

    private void setLastName(String lastName){
        this.lastName = lastName;
    }

    private String getLastName(){
        return lastName;
    }

    private void setPrivacy(String privacy){
        this.privacy = privacy;
    }

    private String getPrivacy(){
        return privacy;
    }
}
