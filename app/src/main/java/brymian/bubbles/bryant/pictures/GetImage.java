package brymian.bubbles.bryant.pictures;

import android.app.Activity;
import android.widget.Toast;

import java.util.List;

import brymian.bubbles.objects.Image;
import brymian.bubbles.damian.nonactivity.ServerRequest.Callback.ImageListCallback;
import brymian.bubbles.damian.nonactivity.ServerRequestMethods;


public class GetImage {

    public static void image(final Activity activity, int uid, String purpose){
        new ServerRequestMethods(activity).getImagesByUid(uid, purpose, new ImageListCallback() {
            @Override
            public void done(List<Image> imageList) {
                System.out.println("imageList.size(): " + imageList.size());
                Toast.makeText(activity, "imageList.size(): " + imageList.size(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}