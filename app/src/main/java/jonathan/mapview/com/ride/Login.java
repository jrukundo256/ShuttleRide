package jonathan.mapview.com.ride;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chalz on 16/06/2018.
 */

public class Login extends AppCompatActivity {

    String enterdname;
    private Mytask task;
    String age;
    private String TAG="Login";
    String answer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
         answer="no";

        Button button=(Button) findViewById(R.id.submit);


    callAsynchronousTask();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mname=(EditText)findViewById(R.id.name);
                EditText myage=(EditText)findViewById(R.id.age);
                //submitinfo(enterdname, myage);
                 //enterdname= mname.getText().toString();
                //age=myage.getText().toString();
               // submitinfo( enterdname,age);
               // new mytash().execute();


                Toast.makeText(getApplication(),"canced asyc task",Toast.LENGTH_SHORT).show();
            }
        });


    }

 private  class  Mytask extends AsyncTask{



     @Override
     protected Object doInBackground(Object[] objects) {
         submitinfo( );
         return null;
     }

     @Override
     protected void onCancelled(Object o) {
         super.onCancelled(o);
     }
 }
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                           // PerformBackgroundTask performBackgroundTask = new PerformBackgroundTask();
                            // PerformBackgroundTask this class is the class that extends AsynchTask

                           // performBackgroundTask.execute();


                              //  timer.cancel();
                               // timer.purge();
  if(answer.equals("yes")){
      task = new Mytask();
      task.execute();

  }else {
      task = new Mytask();
      task.execute();
      timer.cancel();
      timer.purge();


  }







                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 50000); //execute in every 50000 ms
    }



    private void submitinfo() {

        final String tag_string_req = "req_login";
        String url="http://192.168.43.236/name/myname.php";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new  Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                String data=response.toString();
                Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);


                try {
                    //converting the string to json array object


                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        String name=product.getString("name");
                        String age=product.getString("age");
                        Toast.makeText(getApplication(),"returned name is:"+name,Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplication(),"returned age is:"+age,Toast.LENGTH_LONG).show();



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
               // params.put("enteredname", myname);
               // params.put("myage",myage);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
