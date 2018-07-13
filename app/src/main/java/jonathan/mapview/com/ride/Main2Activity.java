package jonathan.mapview.com.ride;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,OnMapReadyCallback,LocationListener ,View.OnClickListener
{
    private SessionManager session;

    private SQLiteHandler db;
    private GoogleMap mMap;
    LocationManager locationManager;
    String currentlat;
    String currentlon;
    double targetlat;
    double targetlon;
    String studentname;
    LatLng paul;
    String dropoff;
    String mylat;
    String newlat;
    String mylng;
    private String TAG="user request";
    String pickup;
    String newlon;
    Marker m = null;
    SharedPreferences prefs = null;
    final Context c = this;
    //String next="Cascading Style Sheets";

    private ListView listView;
    private String names[] = {
            "PickUp",
            "DropOff"
            // "Java Script",
            //"Wordpress"
    };

    /*private String desc[] = {
            pickup,
            dropoff
            //"Code with Java Script",
            //"Manage your content with Wordpress"
    };*/
    private String desc[];

    private Integer imageid[] = {
            R.drawable.marker,
            R.drawable.markerto
            // R.drawable.cinemaicon,
            //R.drawable.clubicon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      /* */

        Button makerequesat=findViewById(R.id.makereq);
      Button submitreq=findViewById(R.id.subreq);
      makerequesat.setOnClickListener(this);
      submitreq.setOnClickListener(this);
      Intent intent=getIntent();
     dropoff=intent.getStringExtra("selectedshuttle");
     // Toast.makeText(getApplication(),"drop off is:"+dropoff,Toast.LENGTH_LONG).show();
      pickup=intent.getStringExtra("mypickup");
        //Toast.makeText(getApplication(),"drop off is:"+pickup,Toast.LENGTH_LONG).show();
        //dropoff="hello";

       // stopservice();




desc=new String[20];
desc[0]=pickup;
desc[1]=dropoff;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocation();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        CustomList customList = new CustomList(this, names, desc, imageid);

        listView = (ListView) findViewById(R.id.listView);
        if (dropoff!=null){
            makerequesat.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            submitreq.setVisibility(View.VISIBLE);



        }else
        {
            listView.setVisibility(View.GONE);


        }
        listView.setAdapter(customList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),"You Clicked "+names[i],Toast.LENGTH_SHORT).show();
            }
        });

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        studentname = user.get("name");

//Toast.makeText(getApplication(),"student :"+studentname,Toast.LENGTH_LONG).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
    }
    // Launching the service
    public void BroadcastReceiver() {


        Intent i = new Intent(this, MyTestService.class);
        i.putExtra("lat", mylat);
        i.putExtra("lon",mylng);
       // i.putExtra("pickup",pickup);
        i.putExtra("dropoff",dropoff);
        startService(i);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
    public void register(){

        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(MyTestService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast


    }

    public void unregister(){

        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);



    }

    private void start(){

        Intent intent = new Intent(Main2Activity.this, MyForeGroundService.class);
        intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);


    }// Define the callback for what to do when data is received

    private void stop(){
        Intent intent = new Intent(Main2Activity.this, MyForeGroundService.class);
        intent.setAction(MyForeGroundService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(intent);


    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
           // String value=intent.getStringExtra("resultvalue");


            if (resultCode == RESULT_OK) {
                String resultValue = intent.getStringExtra("resultValue");
                newlat=intent.getStringExtra("latitude");
                 newlon=intent.getStringExtra("longitude");
                if (resultValue.equals("send alert")){
                    start();
                    unregister();
                    Toast.makeText(getApplication(),resultValue,Toast.LENGTH_LONG).show();

                }



               //


            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        prefs = this.getSharedPreferences("LatLng",MODE_PRIVATE);
        if((prefs.contains("Lat")) && (prefs.contains("Lng")))
        {
             mylat = prefs.getString("Lat","");
             mylng = prefs.getString("Lng","");
            LatLng l =new LatLng(Double.parseDouble(mylat),Double.parseDouble(mylng));
            mMap.addMarker(new MarkerOptions().position(l).title("last known location").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mapp",80,80))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l,15f));
            mMap.addCircle(new CircleOptions().center(l).fillColor(0x110000FF).strokeWidth(1).strokeColor(0x110000FF));

        }

        // Add a marker in Sydney and move the camera
        //locationManager  = new Location(locationManager.getLastKnownLocation());
        // mMap.addMarker(new MarkerOptions().position(current).title("current location"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,12f));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(current));

    }



    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    public Bitmap resizeMapIcons(String iconName,int width,int height){


        Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName,"drawable" ,getPackageName()));
        Bitmap resizedBitmap=Bitmap.createScaledBitmap(imageBitmap,width,height,false);
        return  resizedBitmap;


    }

    @Override
    public void onLocationChanged(Location location) {

        targetlat = location.getLatitude();
        targetlon = location.getLongitude();
        currentlat = String.valueOf(targetlat);
        currentlon = String.valueOf(targetlon);
        Location current = new Location(location);
       // Toast.makeText(getApplication(),"lat is:"+currentlat,Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplication(),"lon is id:"+currentlon,Toast.LENGTH_LONG).show();
        // mMap.addMarker(new MarkerOptions().position(current).title("current location"));
        LatLng curren=new LatLng(targetlat,targetlon);
        prefs.edit().putString("Lat",currentlat).commit();
        prefs.edit().putString("Lng",currentlon).commit();

        // CameraPosition camPosition = new CameraPosition.Builder()
        // .target(new LatLng(targetlat, targetlon)).zoom(15f).build();
        if (mMap != null){
            mMap.clear();


            mMap.addMarker(new MarkerOptions().position(curren).title("current location").
                    icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mapp",80,80))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curren,15f));
            mMap.addCircle(new CircleOptions().center(curren).radius(300).fillColor(0x110000FF).strokeWidth(1).strokeColor(0x110000FF));

            // mMap.animateCamera(CameraUpdateFactory
            //   .newCameraPosition(camPosition));




        }

        else
        {


        }





    }
    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(Page.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
        new android.app.AlertDialog.Builder(this).setMessage("For Better Results, Please Enable your Location").setCancelable(false).
                setPositiveButton("Okey!!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })

                .show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

        }else{
            new android.app.AlertDialog.Builder(this).setMessage("Location has been turned off, Please re-enable your Location").setCancelable(false).
                    setPositiveButton("Okey!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })

                    .show();

        }

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
            new android.app.AlertDialog.Builder(this).setMessage("ARE YOU SURE YOU WANT TO EXIT").setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Main2Activity.this.finish();

                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
                    .show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           // return true;
            new android.app.AlertDialog.Builder(this).setMessage("ARE YOU SURE YOU WANT TO EXIT").setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Main2Activity.this.finish();

                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
                    .show();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shuttle) {
            if (newlat!=null){
                Double lat=Double.parseDouble(newlat);
                Double lon=Double.parseDouble(newlon);
                //Intent intent=new Intent(this,MapsActivity2.class);
                //intent.putExtra("mylat",lat);
                //intent.putExtra("mylon",lon);
                // startActivity(intent);
                // finish();
                // Handle the camera action
         /*   Intent intent=new Intent(Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon));
            startActivity(intent);*/
       /*  String Label="thhh";
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:-1.080600,29.661400("+Label+")"));
            startActivity(intent);*/
                Double myLatitude = lat;
                Double myLongitude = lon;
                String labelLocation = "Shuttle Location";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + myLatitude  + ">,<" + myLongitude + ">?q=<" + myLatitude  + ">,<" + myLongitude + ">(" + labelLocation + ")"));
                startActivity(intent);
               // finish();



            }
            else {
                Toast.makeText(getApplication(),"waiting for cordinates",Toast.LENGTH_LONG).show();

            }


        } else if (id == R.id.nav_profile) {
            // Fetching user details from SQLite
            HashMap<String, String> user = db.getUserDetails();

            studentname = user.get("name");
            String email=user.get("email");
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
            View mView = layoutInflaterAndroid.inflate(R.layout.student, null);
            android.support.v7.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.support.v7.app.AlertDialog.Builder(c);
            alertDialogBuilderUserInput.setView(mView);

           // final TextView showdistance = (TextView) mView.findViewById(R.id.myduration);
            final TextView showduration = (TextView) mView.findViewById(R.id.myduration);
            //showdistance.setText(studentname);
            showduration.setText(email);
            // userInputDialogEditText.setText(message);
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            // ToDo get user input here
                            dialogBox.cancel();
                        }
                    });



            android.support.v7.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();


        } else if (id == R.id.nav_logout) {

            logoutUser();

        }
        else if (id==R.id.newrequest){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            Button makerequesat=findViewById(R.id.makereq);
            if (makerequesat.getVisibility()==View.VISIBLE){
                drawer.closeDrawer(GravityCompat.START);



            }else {

            check(studentname);



            }





        }

        else if (id==R.id.duration){
            Double lat=Double.parseDouble(newlat);
            Double lon=Double.parseDouble(newlon);
            String url = "https://maps.googleapis.com/maps/api/directions/json?"+"origin="+targetlat+","+targetlon+"&destination="+lat+","+lon+"&key=AIzaSyAuh6Hu5pg0CANtpjv74aPja2cydRVUrFE";
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Calculating Duration...");
            progressDialog.show();
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(Main2Activity.this);

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    com.android.volley.Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            // Do something with response
                            //mTextView.setText(response.toString());
                            String data = response.toString();
                            Toast.makeText(getApplication(), "success", Toast.LENGTH_SHORT).show();
                            // Log.d(TAG, "returned data" + data);


                            // Process the JSON
                            try{
                                // Get the JSON array
                                //JSONArray array = response.getJSONArray("students");
                                //JSONArray array = response.getJSONArray("routes");

                                // Log.d(TAG, "returned data" + array);
                               String distance = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                               String duration = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

                                Log.d(TAG, "returned data" + duration);
                       if (!duration.isEmpty()){
                           //Toast.makeText(getApplication(),"duration is:"+duration,Toast.LENGTH_LONG).show();
                           LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                           View mView = layoutInflaterAndroid.inflate(R.layout.distance, null);
                           android.support.v7.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.support.v7.app.AlertDialog.Builder(c);
                           alertDialogBuilderUserInput.setView(mView);

                           final TextView showdistance = (TextView) mView.findViewById(R.id.mydistance);
                           final TextView showduration = (TextView) mView.findViewById(R.id.myduration);
                           showdistance.setText(distance);
                           showduration.setText(duration);
                          // userInputDialogEditText.setText(message);
                           alertDialogBuilderUserInput
                                   .setCancelable(false)
                                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialogBox, int id) {
                                           // ToDo get user input here
                                           dialogBox.cancel();
                                       }
                                   });



                           android.support.v7.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                           alertDialogAndroid.show();





                       }else {

                           Toast.makeText(getApplication(),"geting Duration,please wait...",Toast.LENGTH_LONG).show();



                       }
                                // }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Log.e(TAG, "server error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );

            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.makereq:{


                //unregister();
            Intent intent=new Intent(this,Request.class);
            startActivity(intent);
              finish();
              //  stopservice();
            }
            break;
            case R.id.subreq:{

               submitinfo( studentname,pickup,dropoff);
               BroadcastReceiver();
                register();

                //stopservice();
               // stopservice();






            }
            break;


        }
    }
    private void submitinfo(final String studentname,final String pickup,final String dropoff) {

        final String tag_string_req = "req_login";
      //  String url="http://shuttlealert.exoticugandatoursandtravel.com/student/request.php";
        String url="http://shuttlealert.exoticugandatoursandtravel.com/student/request.php";

        StringRequest strReq = new StringRequest(com.android.volley.Request.Method.POST,
                url, new  Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                // or sendBroadcast(in) for a normal broadcast;
                String data=response.toString();
                Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);
                Button submitreq=findViewById(R.id.subreq);
                submitreq.setVisibility(View.GONE);


                //name="hello world";



                try {
                    //converting the string to json array object


                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        String message=product.getString("inbox");
                        if (!message.isEmpty()){

                     /*       new android.app.AlertDialog.Builder(Main2Activity.this).setMessage(message)
                                    .setCancelable(false).setTitle("New message").setIcon(R.drawable.pin).
                                    setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })

                                    .show();*/
                            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                            View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                            android.support.v7.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.support.v7.app.AlertDialog.Builder(c);
                            alertDialogBuilderUserInput.setView(mView);

                            final TextView userInputDialogEditText = (TextView) mView.findViewById(R.id.userInputDialog);
                            userInputDialogEditText.setText(message);
                            alertDialogBuilderUserInput
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            // ToDo get user input here
                                            dialogBox.cancel();
                                        }
                                    });



                            android.support.v7.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                            alertDialogAndroid.show();





                        }
                        else{










                        }

                        // Construct an Intent tying it to the ACTION (arbitrary event namespace)




                        // String age=product.getString("age");
                        // Toast.makeText(getApplication(),"returned name is:"+mycount,Toast.LENGTH_LONG).show();
                        // Toast.makeText(getApplication(),"returned age is:"+age,Toast.LENGTH_LONG).show();



                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "server error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to request url.
                Map<String, String> params = new HashMap<String, String>();
                params.put("studentname", studentname);
                params.put("pickup",pickup);
                params.put("dropoff",dropoff);
                // params.put("myage",myage);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void check(final String studentname) {

        final String tag_string_req = "req_login";
        //String url="http://shuttlealert.exoticugandatoursandtravel.com/student/request.php";
        String url="http://shuttlealert.exoticugandatoursandtravel.com/student/check.php";
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(com.android.volley.Request.Method.POST,
                url, new  Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                // or sendBroadcast(in) for a normal broadcast;
                String data=response.toString();
                Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);
                //name="hello world";



                try {
                    //converting the string to json array object


                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        String message=product.getString("signal");
                        if (message.equals("doesntexist")){
                            Button submitreq=findViewById(R.id.subreq);
                            Button makerequesat=findViewById(R.id.makereq);
                            listView = (ListView) findViewById(R.id.listView);
                            makerequesat.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                           // submitreq.setVisibility(View.GONE);



                     /*       new android.app.AlertDialog.Builder(Main2Activity.this).setMessage(message)
                                    .setCancelable(false).setTitle("New message").setIcon(R.drawable.pin).
                                    setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })

                                    .show();*/


                        }
                        else{
                            new android.app.AlertDialog.Builder(Main2Activity.this).setMessage("SHOULD WE CANCEL YOUR LAST PENDING REQUEST")
                                    .setCancelable(false).
                                    setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            cancel( studentname,pickup, dropoff);


                                        }
                                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            })

                                    .show();









                        }

                        // Construct an Intent tying it to the ACTION (arbitrary event namespace)




                        // String age=product.getString("age");
                        // Toast.makeText(getApplication(),"returned name is:"+mycount,Toast.LENGTH_LONG).show();
                        // Toast.makeText(getApplication(),"returned age is:"+age,Toast.LENGTH_LONG).show();



                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "server error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to request url.
                Map<String, String> params = new HashMap<String, String>();
                params.put("studentname", studentname);

                // params.put("myage",myage);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void cancel(final String studentname,final String pickup,final String dropoff) {

        final String tag_string_req = "req_login";
        //  String url="http://shuttlealert.exoticugandatoursandtravel.com/student/request.php";
        String url="http://shuttlealert.exoticugandatoursandtravel.com/student/cancel.php";

        StringRequest strReq = new StringRequest(com.android.volley.Request.Method.POST,
                url, new  Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                // or sendBroadcast(in) for a normal broadcast;
                String data=response.toString();
                Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);
                Button makerequesat=findViewById(R.id.makereq);
                listView = (ListView) findViewById(R.id.listView);
                listView.setVisibility(View.GONE);
                makerequesat.setVisibility(View.VISIBLE);


                try {
                    //converting the string to json array object


                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                       // String message=product.getString("inbox");


                        // Construct an Intent tying it to the ACTION (arbitrary event namespace)




                        // String age=product.getString("age");
                        // Toast.makeText(getApplication(),"returned name is:"+mycount,Toast.LENGTH_LONG).show();
                        // Toast.makeText(getApplication(),"returned age is:"+age,Toast.LENGTH_LONG).show();



                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "server error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to request url.
                Map<String, String> params = new HashMap<String, String>();
                params.put("studentname", studentname);
                params.put("pickup",pickup);
                params.put("dropoff",dropoff);
                // params.put("myage",myage);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
