package brymian.bubbles.damian.nonactivity.ServerRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import brymian.bubbles.damian.nonactivity.Connection.HTTPConnection;
import brymian.bubbles.damian.nonactivity.Post;
import brymian.bubbles.damian.nonactivity.ServerRequest.Callback.StringCallback;

import static brymian.bubbles.damian.nonactivity.Miscellaneous.getNullOrValue;

public class UserCommentRequest
{
    private HTTPConnection httpConnection = null;
    private ProgressDialog pd = null;

    public UserCommentRequest(Activity activity) {
        pd = new ProgressDialog(activity);
        pd.setCancelable(false);
        pd.setTitle("Processing");
        pd.setMessage("Please wait...");
        httpConnection = new HTTPConnection();
    }



    public void setObjectComment(Integer uid, String objectTypeLabel, Integer oid,
        String userCommentSetTimestamp, String userComment, Long parentUcid, StringCallback stringCallback)
    {
        pd.show();
        new SetObjectComment(uid, objectTypeLabel, oid, userCommentSetTimestamp,
            userComment, parentUcid, stringCallback).execute();
    }

    public void getObjectComments(String objectTypeLabel, Long oid, StringCallback stringCallback)
    {
        pd.show();
        new GetObjectComments(objectTypeLabel, oid, stringCallback).execute();
    }



    private class SetObjectComment extends AsyncTask<Void, Void, String> {

        Integer uid;
        String  objectTypeLabel;
        Integer oid;
        String userCommentSetTimestamp;
        String userComment;
        Long parentUcid;
        StringCallback stringCallback;

        private SetObjectComment(Integer uid, String objectTypeLabel, Integer oid,
            String userCommentSetTimestamp, String userComment, Long parentUcid, StringCallback stringCallback)
        {
            this.uid = uid;
            this.objectTypeLabel = objectTypeLabel;
            this.oid = oid;
            this.userCommentSetTimestamp = userCommentSetTimestamp;
            this.userComment = userComment;
            this.parentUcid = parentUcid;
            this.stringCallback = stringCallback;
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = httpConnection.getWebServerString() +
                "AndroidIO/UserCommentRequest.php?function=setObjectComment";

            Post request = new Post();

            try
            {
                JSONObject jObject = new JSONObject();
                jObject.put("uid", getNullOrValue(uid));
                jObject.put("objectTypeLabel", getNullOrValue(objectTypeLabel));
                jObject.put("oid", getNullOrValue(oid));
                jObject.put("userCommentSetTimestamp", getNullOrValue(userCommentSetTimestamp));
                jObject.put("userComment", getNullOrValue(userComment));
                jObject.put("parentUcid", getNullOrValue(parentUcid));

                String jString = jObject.toString();
                String response = request.post(url, jString);

                return response;
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
                return "ERROR ENCOUNTERED. SEE ANDROID LOG.";
            }
            catch (JSONException jsone)
            {
                jsone.printStackTrace();
                return "ERROR ENCOUNTERED. SEE ANDROID LOG.";
            }
        }

        @Override
        protected void onPostExecute(String string) {
            pd.dismiss();
            stringCallback.done(string);

            super.onPostExecute(string);
        }

    }

    private class GetObjectComments extends AsyncTask<Void, Void, String> {

        String objectTypeLabel;
        Long oid;
        StringCallback stringCallback;

        private GetObjectComments(String objectTypeLabel, Long oid, StringCallback stringCallback)
        {
            this.objectTypeLabel = objectTypeLabel;
            this.oid = oid;
            this.stringCallback = stringCallback;
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = httpConnection.getWebServerString() +
                "AndroidIO/UserCommentRequest.php?function=getObjectComments";

            Post request = new Post();

            try
            {
                JSONObject jObject = new JSONObject();
                jObject.put("objectTypeLabel", getNullOrValue(objectTypeLabel));
                jObject.put("oid", getNullOrValue(oid));

                String jString = jObject.toString();
                String response = request.post(url, jString);

                return response;
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
                return "ERROR ENCOUNTERED. SEE ANDROID LOG.";
            }
            catch (JSONException jsone)
            {
                jsone.printStackTrace();
                return "ERROR ENCOUNTERED. SEE ANDROID LOG.";
            }
        }

        @Override
        protected void onPostExecute(String string) {
            pd.dismiss();
            stringCallback.done(string);

            super.onPostExecute(string);
        }

    }

}