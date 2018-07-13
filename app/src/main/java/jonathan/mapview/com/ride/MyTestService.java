package jonathan.mapview.com.ride;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyTestService extends IntentService {
    private String TAG="Login";
    private  String mycount;
    String shuttlename;
    String curentlat;
    String currentlon;
    String val;



    private Timer timer = new Timer();

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    // private static final String ACTION_FOO = "paul.mapview.com.ride.action.FOO";
    public static final String ACTION = "paul.mapview.com.ride.MyTestService";

    public MyTestService() {
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    protected void onHandleIntent(Intent intent) {
        //String pickup=intent.getStringExtra("pickup");
        //shuttlename=intent.getStringExtra("dropoff");
        curentlat=intent.getStringExtra("lat");
        currentlon=intent.getStringExtra("lon");
        shuttlename=intent.getStringExtra("dropoff");
        //val=intent.getStringExtra("lat");







        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                submitinfo(shuttlename,curentlat,currentlon);
                // Put extras into the intent as usual
               // Intent in = new Intent(ACTION);
               // in.putExtra("resultCode", Activity.RESULT_OK);
               // in.putExtra("resultValue",  val);
                // Fire the broadcast with intent packaged
               // LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
            }
        }, 0, 10000);//5 Minutes*/

        // Fetch data passed into the intent on start

    }
    private void submitinfo(final String shuttlename, final String curentlat,final String currentlon) {

        final String tag_string_req = "req_login";
        String url="http://shuttlealert.exoticugandatoursandtravel.com/student/updates.php";

        StringRequest strReq = new StringRequest(com.android.volley.Request.Method.POST,
                url, new  Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                // or sendBroadcast(in) for a normal broadcast;
                String data=response.toString();
              // Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);
                //name="hello world";



                try {
                    //converting the string to json array object


                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        //String shuttlelon=product.getString("longitude");
                        String alert=product.getString("alert");
                        String latitude=product.getString("latitude");
                        String longitude=product.getString("longitude");
//String distanc="5";
                        String val = alert;
                        // Construct an Intent tying it to the ACTION (arbitrary event namespace)
                        Intent in = new Intent(ACTION);
                        // Put extras into the intent as usual
                        in.putExtra("resultCode", Activity.RESULT_OK);
                        in.putExtra("resultValue",  val);
                        in.putExtra("latitude",latitude);
                        in.putExtra("longitude",longitude);
                        // Fire the broadcast with intent packaged
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);


                        // String age=product.getString("age");
                        // Toast.makeText(getApplication(),"returned name is:"+mycount,Toast.LENGTH_LONG).show();
                        // Toast.makeText(getApplication(),"returned age is:"+age,Toast.LENGTH_LONG).show();



                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                   // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "server error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(),
                       // error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to request url.
                Map<String, String> params = new HashMap<String, String>();
                params.put("shuttlename", shuttlename);
                params.put("currentlat",curentlat);
                params.put("currentlon",currentlon);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}