package brymian.bubbles.bryant.cropImage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import brymian.bubbles.R;
import brymian.bubbles.bryant.camera.CameraActivity;
import brymian.bubbles.bryant.nonactivity.SaveSharedPreference;
import brymian.bubbles.damian.nonactivity.CustomException.SetOrNotException;
import brymian.bubbles.damian.nonactivity.ServerRequest.Callback.StringCallback;
import brymian.bubbles.damian.nonactivity.ServerRequest.UserImageRequest;
import brymian.bubbles.damian.nonactivity.ServerRequestMethods;

public class CropImageActivity extends AppCompatActivity {

    private ProgressDialog pd = null;
    Toolbar toolbar;
    CropImageView cropImageView;
    FloatingActionButton fabDone;
    private final int GALLERY_CODE = 1;
    private final int CAMERA_CODE = 2;
    private String from, encodedImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String from = getIntent().getStringExtra("from");
        setFrom(from);
        if (from.equals("profileGallery")){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE);
        }
        else if(from.equals("profileCamera")){
            startActivityForResult(new Intent(this, CameraActivity.class), CAMERA_CODE);
        }

        setContentView(R.layout.crop_image_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.Crop_Image);

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        fabDone = (FloatingActionButton) findViewById(R.id.fabDone);
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabDone.setClickable(false);
                Bitmap croppedImage = cropImageView.getCroppedImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                setEncodedImage(encodedImage);
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                if (getFrom().equals("profileGallery")){
                    checkProfileImageExists();
                    pd = new ProgressDialog(CropImageActivity.this);
                    pd.setCancelable(false);
                    pd.setTitle("Processing");
                    pd.setMessage("Uploading image...");
                    pd.show();
                }
            }
        });
    }

    /* Step 1: check if any profile images exist */
    private void checkProfileImageExists(){
        new UserImageRequest(this).getImagesByUid(SaveSharedPreference.getUserUID(CropImageActivity.this), true, new StringCallback() {
            @Override
            public void done(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String images = jsonObject.getString("images");
                    JSONArray imagesArr = new JSONArray(images);
                    if (imagesArr.length() > 0){ //user has profile images
                        JSONObject imagesObj = imagesArr.getJSONObject(0);
                        String userImageProfileSequence = imagesObj.getString("userImageProfileSequence");
                        //checking if any images have sequence of 0
                        if (userImageProfileSequence.equals("0")){
                            String uiid = imagesObj.getString("uiid");
                            String userImageSequence = imagesObj.getString("userImageSequence");
                            setProfileSequenceNull(Integer.valueOf(uiid), userImageSequence);
                        }
                        else {
                            uploadProfileImage();
                        }
                    }
                    else { //user has no profile images, proceed to upload image
                        uploadProfileImage();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setProfileSequenceNull(final int uiid, final String userImageSequence){
        try {
            new UserImageRequest(this).setImage(
                    uiid, /* uiid */
                    null, /* userImageProfileSequence */
                    null, /* userImageName */
                    null, /* userImagePurposeLabel */
                    null, /* userImagePrivacyLabel */
                    null, /* userImageGpsLatitude */
                    null, /* userImageGpsLongitude */
                    new Boolean[]{null, true, false, false, false, false, false},
                    new StringCallback() {
                        @Override
                        public void done(String string) {
                            Log.e("SetProfSeqNull", string);
                            deleteProfileImage(Integer.valueOf(userImageSequence));
                            uploadProfileImage();
                        }
                    }
            );
        }
        catch (SetOrNotException e) {
            e.printStackTrace();
        }
    }

    private void deleteProfileImage(int uiid){
        new ServerRequestMethods(this).deleteImage(SaveSharedPreference.getUserUID(this), uiid, new StringCallback() {
            @Override
            public void done(String string) {
                Log.e("delete", string);
            }
        });
    }
    private void uploadProfileImage(){
        new UserImageRequest(this).uploadImage(SaveSharedPreference.getUserUID(CropImageActivity.this), 0, imageName(), "Profile", "Public", null, null, getEncodedImage(), new StringCallback() {
            @Override
            public void done(String string) {
                Log.e("uploadImage", string);
                pd.dismiss();
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE){
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        cropImageView.setImageBitmap(bitmap);
                        cropImageView.setFixedAspectRatio(true);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cropImageView.setAspectRatio(10, 10);
                            }
                        }, 100);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == RESULT_CANCELED){
                finish();
            }
        }
        else if (requestCode == CAMERA_CODE){
            if (resultCode == RESULT_OK){
                Log.e("camera", "works");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.episode_create_crop_image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.rotate:
                cropImageView.rotateImage(90);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String imageName(){
        String charSequenceName = (String) android.text.format.DateFormat.format("yyyy_MM_dd_hh_mm_ss", new java.util.Date());
        return SaveSharedPreference.getUserUID(this) + "_" + charSequenceName;
    }

    private void setFrom(String from){
        this.from = from;
    }

    private String getFrom(){
        return this.from;
    }

    private void setEncodedImage(String encodedImage){
        this.encodedImage = encodedImage;
    }

    private String getEncodedImage(){
        return encodedImage;
    }
}