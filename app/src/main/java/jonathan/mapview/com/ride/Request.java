package jonathan.mapview.com.ride;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PAUL on 6/18/2018.
 */

public class Request extends AppCompatActivity implements View.OnClickListener {
    TextView selectedshuttle;
    TextView selectedpickup;
    private String TAG="user request";
    ArrayList<String> mypickup;
    ArrayList<String> myshuttle;
    protected  CharSequence [] pickup;
    protected CharSequence [] shuttle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request);
        Button selectshuttle=findViewById(R.id.shuttle);
        Button selectpickup=findViewById(R.id.button3);
        Button confirm=findViewById(R.id.button4);
        mypickup=new ArrayList<>();
        myshuttle=new ArrayList<>();
        confirm.setOnClickListener(this);
        selectpickup.setOnClickListener(this);
        selectshuttle.setOnClickListener(this);
        selectedshuttle=findViewById(R.id.textView);
        selectedpickup=findViewById(R.id.textView2);
        getpickup();
        getshuutles();
        android.support.v7.widget.Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Make New Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,Main2Activity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){

    case R.id.shuttle:{
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Request.this);
        builder
                .setTitle("Select shuttle")
                .setCancelable(true)
                .setSingleChoiceItems(shuttle ,-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String no="NO";

                        ListView lw=((android.support.v7.app.AlertDialog)dialog).getListView();
                        Object checkeditem=lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        if (checkeditem!= null){

                            String  item= checkeditem.toString();
                           // Toast.makeText(getApplication(),"selected"+item,Toast.LENGTH_LONG).show();
                            selectedshuttle.setText(item);


                        }

                        dialog.dismiss();




                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();







    }
    break;
    case R.id.button3:
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Request.this);
        builder
                .setTitle("Select Pickup point ")
                .setCancelable(true)
                .setSingleChoiceItems(pickup ,-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String no="NO";

                        ListView lw=((android.support.v7.app.AlertDialog)dialog).getListView();
                        Object checkeditem=lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        if (checkeditem!= null){

                            String  item= checkeditem.toString();
                            //Toast.makeText(getApplication(),"selected"+item,Toast.LENGTH_LONG).show();
                            selectedpickup.setText(item);

                        }

                        dialog.dismiss();




                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

    {



    }
    break;
    case R.id.button4:{
        Intent intent=new Intent(this,Main2Activity.class);
        final String myshutl=selectedshuttle.getText().toString();
       final String mypickup=selectedpickup.getText().toString();
        intent.putExtra("selectedshuttle",myshutl);
        intent.putExtra("mypickup",mypickup);
        startActivity(intent);
        finish();




    }







}
    }
    private void getpickup(){
        final String tag_string_req = "req_login";

       // String url="http://shuttlealert.exoticugandatoursandtravel.com/student/pickup.php";
        String url="http://shuttlealert.exoticugandatoursandtravel.com/student/pickup.php";
        StringRequest stringRequest=new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String data=response.toString();
                Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);
                try {


                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);
                        String item=product.getString("pickup");
                        mypickup.add(item);



                    }

                    pickup=mypickup.toArray(new CharSequence[mypickup.size()]);

                }

                catch (JSONException e) {
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
        });
        AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);


    }
    private void getshuutles(){
        final String tag_string_req = "req_login";

        //String url="http://shuttlealert.exoticugandatoursandtravel.com/student/shuttles.php";
        String url="http://shuttlealert.exoticugandatoursandtravel.com/student/shuttles.php";
        StringRequest stringRequest=new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String data=response.toString();
                Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);
                try {


                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);
                        String item=product.getString("shuttlename");
                        myshuttle.add(item);



                    }

                    shuttle=myshuttle.toArray(new CharSequence[myshuttle.size()]);

                }

                catch (JSONException e) {
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
        });
        AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);


    }
}
