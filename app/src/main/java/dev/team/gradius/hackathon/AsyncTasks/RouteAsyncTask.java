package dev.team.gradius.hackathon.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dev.team.gradius.hackathon.Connection.HttpHandler;
import dev.team.gradius.hackathon.MapsActivity;
import dev.team.gradius.hackathon.Path.DirectionFinderListener;
import dev.team.gradius.hackathon.Path.Distance;
import dev.team.gradius.hackathon.Path.Duration;
import dev.team.gradius.hackathon.Path.Route;

/**
 * Created by Wingfly on 4/14/2017.
 */

public class RouteAsyncTask extends AsyncTask<String, Void, String>
{
    DirectionFinderListener listener;

    public RouteAsyncTask(DirectionFinderListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params)
    {
        HttpHandler sh = new HttpHandler();
        String responseFrom = sh.getResponseFrom(params[0]);
        //  Log.d(MapsActivity.TAG, "Response from url: " + responseFrom);
        return responseFrom;
    }

    @Override
    protected void onPostExecute(String s)
    {
        try
        {
            parseJson(s);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }

    private void parseJson(String s) throws JSONException
    {
        if (s != null)
        {
            try
            {
                List<Route> routes = new ArrayList<>();
                JSONObject jsonData = new JSONObject(s);
                JSONArray jsonRoutes = jsonData.getJSONArray("routes");
                for (int i = 0; i < jsonRoutes.length(); i++)
                {
                    JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                    Route route = new Route();

                    JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                    JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                    JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                    JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                    JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                    JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                    JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                    route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
                    route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
                    route.endAddress = jsonLeg.getString("end_address");
                    route.startAddress = jsonLeg.getString("start_address");
                    route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                    route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                    route.points = decodePolyLine(overview_polylineJson.getString("points"));

                    routes.add(route);
                }

                listener.onDirectionFinderSuccess(routes);
            } catch (JSONException e)
            {
                Log.e(MapsActivity.TAG, "Json parsing error: " + e.getMessage());
            }
        } else
        {
            Log.e(MapsActivity.TAG, "Couldn't get json from server.");
        }
    }


    private List<LatLng> decodePolyLine(final String poly)
    {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<>();
        int lat = 0;
        int lng = 0;

        while (index < len)
        {
            int b;
            int shift = 0;
            int result = 0;
            do
            {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do
            {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }
        return decoded;
    }


}
