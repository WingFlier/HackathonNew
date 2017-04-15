package dev.team.gradius.hackathon;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import dev.team.gradius.hackathon.Connection.ConnectionCheck;
import dev.team.gradius.hackathon.Path.DirectionFinder;
import dev.team.gradius.hackathon.Path.DirectionFinderListener;
import dev.team.gradius.hackathon.Path.Route;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        DirectionFinderListener
{
    public static final String TAG = "logging_tag";
    private float DEFAULT_ZOOM = 17.0f;

    double lat;
    double lng;


    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    Marker myMarker = null;
    Marker finishMarker = null;
    private View mapView;
    private MarkerOptions options;
    private Map<String, LatLng> latlngs;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 50;

    private TextView btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (!initMap())
        {
            Toast.makeText(this, "smthing Wrong", Toast.LENGTH_SHORT).show();
        }

        btnFindPath = (TextView) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);

        btnFindPath.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    sendRequest();
                } catch (ExecutionException e)
                {
                    Log.e(TAG, "excetion  " + e.getMessage());
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean initMap()
    {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        ConnectionCheck check = new ConnectionCheck();
        if (!check.isOnline(this))
        {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        options = new MarkerOptions();
        latlngs = new HashMap<>();

        /*latlngs.put("0", new LatLng(40.17758634, 44.50382888));
        latlngs.put("1", new LatLng(40.17697974, 44.51083481));

        for (LatLng point : latlngs.values())
        {
            options.position(point)
                    .title("someTitle")
                    .snippet("someDesc")
                    .icon(setIcon(R.mipmap.ic_launcher_round, 25, 25));
            mMap.addMarker(options);
        }*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "work or not?", Toast.LENGTH_SHORT).show();
            return;
        }
        /** ////For displaying location button\\\\
         mMap.setMyLocationEnabled(true);
         ///For displaying location button at bottom right corner\\\
         if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null)
         {
         View locationButton = ((View) mapView.findViewById(Integer.parseInt("1"))
         .getParent()).findViewById(Integer.parseInt("2"));
         RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
         locationButton.getLayoutParams();
         layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
         layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
         layoutParams.setMargins(0, 0, 30, 30);
         }*/
    }

    private void sendRequest() throws ExecutionException, InterruptedException
    {
        Log.d(TAG, "onRequest lat = " + lat);
        Log.d(TAG, "onRequest lng = " + lng);

        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();

        if (origin.isEmpty())
        {
            LatLng position = myMarker.getPosition();
            origin = position.latitude + "," + position.longitude;
            Toast.makeText(this, origin, Toast.LENGTH_SHORT).show();
        }
        if (destination.isEmpty())
        {
            LatLng position = finishMarker.getPosition();
            destination = position.latitude + "," + position.longitude;
            Toast.makeText(this, destination, Toast.LENGTH_SHORT).show();
        }
        try
        {
            new DirectionFinder(this, origin, destination).execute();
        } catch (
                UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (originMarkers != null)
        {
            for (Marker marker : originMarkers)
            {
                marker.remove();
            }
        }

        if (destinationMarkers != null)
        {
            for (Marker marker : destinationMarkers)
            {
                marker.remove();
            }
        }
        if (polylinePaths != null)
        {
            for (Polyline polyline : polylinePaths)
            {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes)
    {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(setIcon(R.drawable.markerstart, 60, 100))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(setIcon(R.drawable.markerfinish, 60, 100))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(false).
                    color(Color.argb(255, 115, 213, 228)).
                    width(10);
            for (int i = 0; i < route.points.size(); i++)
            {
                polylineOptions.add(route.points.get(i));
                Log.d(TAG, String.valueOf(route.points.get(i)));
            }
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result)
    {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
    }

    TextView txtNext;
    ImageView locButton;
    View adressLayout;

    @Override
    public void onLocationChanged(Location location)
    {
        txtNext = (TextView) findViewById(R.id.txtNext);
        locButton = ((ImageView) findViewById(R.id.findLocation));
        adressLayout = ((View) findViewById(R.id.adressLayout));

        final LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLoc, DEFAULT_ZOOM);
        lat = myLoc.latitude;
        lng = myLoc.longitude;
        Log.d(TAG, "onLocationChanged lat = " + lat);
        Log.d(TAG, "onLocationChanged lng = " + lng);
        mMap.animateCamera(cameraUpdate);
        if (myMarker == null)
        {
            myMarker = mMap.addMarker(new MarkerOptions()
                    .icon(setIcon(R.drawable.markerstart, 60, 100))
                    .position(myLoc)
                    .draggable(true)
                    .title("You are here"));


        }
        txtNext.setVisibility(View.VISIBLE);
        locationManager.removeUpdates(this);
        Log.d(TAG, String.valueOf(myLoc.latitude) + "lat");
        Log.d(TAG, String.valueOf(myLoc.longitude) + "long");

        locButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMap.animateCamera(cameraUpdate);
                myMarker.setPosition(myLoc);
            }
        });
        txtNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMap.animateCamera(cameraUpdate);
                myMarker.setPosition(myLoc);
                adressLayout.setVisibility(View.VISIBLE);
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {
            @Override
            public void onMarkerDragStart(Marker marker)
            {
            }

            @Override
            public void onMarkerDrag(Marker marker)
            {
            }

            @Override
            public void onMarkerDragEnd(Marker marker)
            {
                Log.d(TAG, String.valueOf("marker  id " + marker.getId()));

                if (marker.getId().equals("m0"))
                {
                    myMarker.setPosition(marker.getPosition());
                } else if (marker.getId().equals("m1"))
                {
                    //Log.d(TAG, String.valueOf("m1 worked " + marker.getPosition()));
                    finishMarker.setPosition(marker.getPosition());
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    public BitmapDescriptor setIcon(int img, int width, int height)
    {
        // Getting mipmap from recources and set as market with fixed size
        Bitmap bitmapImg = BitmapFactory.decodeResource(getResources(), img);
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapImg, width, height, false);
        BitmapDescriptor icons = BitmapDescriptorFactory.fromBitmap(smallMarker);
        return icons;
    }

    public void btnStartClick(View v) throws ExecutionException, InterruptedException
    {
        LatLng position = myMarker.getPosition();
       /* etOrigin.setText(position.latitude + "," + position.longitude);*/
        //Log.d(TAG, String.valueOf(position));
    }

    public void btnEndClick(View v) throws ExecutionException, InterruptedException
    {
        double lat = myMarker.getPosition().latitude;
        double lng = myMarker.getPosition().longitude;
        //      Log.d(TAG, "lat " + lat);
        //      Log.d(TAG, "lng " + lng);

        if (finishMarker == null)
        {
            finishMarker = mMap.addMarker(new MarkerOptions()
                    .icon(setIcon(R.drawable.markerfinish, 60, 100))
                    .position(new LatLng(lat, lng))
                    .draggable(true)
                    .title("Destination"));
        } else
        {
            LatLng position = finishMarker.getPosition();
            finishMarker.setPosition(myMarker.getPosition());
           /* etDestination.setText(position.latitude + "," + position.longitude);*/
        }
    }
}
