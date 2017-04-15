package dev.team.gradius.hackathon.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Isahak on 4/15/2017.
 */

public class ServerDataReadWriter extends AsyncTask<Void, Void, Void>
{
    private Context context;
    private ProgressDialog progress;

    public ServerDataReadWriter(Context context) { this.context = context; }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progress = new ProgressDialog(context);
        progress.setMessage("wait");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        if (progress.isShowing())
            progress.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values) { super.onProgressUpdate(values); }
}