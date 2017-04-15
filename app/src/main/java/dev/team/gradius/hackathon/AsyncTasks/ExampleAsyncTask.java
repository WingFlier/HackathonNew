package dev.team.gradius.hackathon.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.team.gradius.hackathon.Connection.HttpHandler;
import dev.team.gradius.hackathon.MapsActivity;

/**
 * Created by Wingfly on 4/14/2017.
 */
public class ExampleAsyncTask extends AsyncTask<Void, Void, List>
{
    /**https://maps.googleapis.com/maps/api/directions/json?origin=40.179907,44.506064&destination=40.182096,44.509090&key=AIzaSyAGDKxMbERjoHcE_QEcsTeKY7SOfrAvwEk*/
    /**https://mybrowserstartpage.ru/hack/test.php*/

    /**
     * req to Tikos server
     * lat = ...
     * long = ...
     * directionLat
     * directionLong
     */
    private ProgressDialog pd;
    Context context;
    ArrayList<HashMap<String, String>> contactList;

    //https://mybrowserstartpage.ru/hack/test.php?lat=4654654&long=654654

    public ExampleAsyncTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("wait");
        pd.setCancelable(false);
        pd.show();
        contactList = new ArrayList<>();
    }
    // private String url = "http://api.androidhive.info/contacts/";

    private String url = "http://2pcom.esy.es/dir/index.html";
    // private String url = "https://mybrowserstartpage.ru/hack/test.php?lat=4654654&lng=654654";

    @Override
    protected List doInBackground(Void... params)
    {
        HttpHandler sh = new HttpHandler();
        String responseFrom = sh.getResponseFrom(url);

        //  Log.d(MapsActivity.TAG, "Response from url: " + responseFrom);
        if (responseFrom != null)
        {
            try
            {
                JSONObject jsonObj = new JSONObject(responseFrom);
                JSONArray contacts = jsonObj.getJSONArray("marshutka");
                for (int i = 0; i < contacts.length(); i++)
                {
                    JSONObject c = contacts.getJSONObject(i);
                    String id = c.getString("id");
                    String hamar = c.getString("hamar");
                    HashMap<String, String> contact = new HashMap<>();
                    contact.put("id", id);
                    contact.put("hamar", hamar);
                    contactList.add(contact);
                }
              //  Log.d(MapsActivity.TAG, String.valueOf(contactList.get(0)));
            } catch (final JSONException e)
            {
                Log.e(MapsActivity.TAG, "Json parsing error: " + e.getMessage());
            }
        } else
        {
            Log.e(MapsActivity.TAG, "Couldn't get json from server.");
        }
        return contactList;
    }

    @Override
    protected void onPostExecute(List list)
    {
        super.onPostExecute(list);
        if (pd.isShowing())
            pd.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }
}
