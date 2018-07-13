package jonathan.mapview.com.ride;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by PAUL on 6/18/2018.
 */

public class spinnerclass extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ArrayList<String> fish;
    private String TAG="Login";
    ArrayAdapter adapter;
    String empty="";
    protected  CharSequence [] accomodatio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spin);
        Spinner myspinner=findViewById(R.id.spinner);
        myspinner.setOnItemSelectedListener(this);
        fish=new ArrayList<String>();
        Button submit=findViewById(R.id.req);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(spinnerclass.this);
                builder
                        .setTitle("Select ")
                        .setCancelable(true)
                        .setSingleChoiceItems(accomodatio ,-1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String no="NO";

                                ListView lw=((android.support.v7.app.AlertDialog)dialog).getListView();
                                Object checkeditem=lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                if (checkeditem!= null){

                                  String  item= checkeditem.toString();
                                    Toast.makeText(getApplication(),"selected"+item,Toast.LENGTH_LONG).show();
/*
                            if (item!=no){

                                Toast.makeText(getApplication(),"he said yes",Toast.LENGTH_LONG).show();
                            }

                            else {
                                Toast.makeText(getApplication(),"he said no",Toast.LENGTH_LONG).show();
                            }
                            */

                                }

                                dialog.dismiss();




                            }
                        });

                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

       // getdata();


    }
    private void getdata(){
        final String tag_string_req = "req_login";

        String url="http://192.168.43.236/name/myname.php";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String data=response.toString();
                Toast.makeText(getApplication(),"success",Toast.LENGTH_SHORT).show();
                Log.d(tag_string_req,"returned data"+data);
                try {


                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);
                        String item=product.getString("name");
                        fish.add(item);



                    }
                    Spinner myspinner=findViewById(R.id.spinner);
                    accomodatio=fish.toArray(new CharSequence[fish.size()]);

                    // adapter=new ArrayAdapter<String>(spinnerclass.this,R.layout.row,R.id.mytext,fish);
                   // myspinner.setAdapter(adapter);
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    String hello=parent.getItemAtPosition(position).toString();
    Toast.makeText(getApplication(),"selected:"+hello,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }
}
