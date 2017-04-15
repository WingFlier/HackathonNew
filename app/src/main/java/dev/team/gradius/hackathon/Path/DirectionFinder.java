package dev.team.gradius.hackathon.Path;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dev.team.gradius.hackathon.AsyncTasks.RouteAsyncTask;
import dev.team.gradius.hackathon.Connection.HttpHandler;
import dev.team.gradius.hackathon.MapsActivity;

/**
 * Created by Wingfly on 4/13/2017.
 */

public class DirectionFinder
{

    private static final String DIRECTION_URL_API =
            "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDnwLF2-WfK8cVZt9OoDYJ9Y8kspXhEHfI";

    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination)
    {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }


    public void execute() throws UnsupportedEncodingException, ExecutionException, InterruptedException
    {
        listener.onDirectionFinderStart();
        new RouteAsyncTask(listener).execute(url()).get();

    }

    private String url() throws UnsupportedEncodingException
    {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        Log.d(MapsActivity.TAG,"url for send " +  DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY);
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }


}
