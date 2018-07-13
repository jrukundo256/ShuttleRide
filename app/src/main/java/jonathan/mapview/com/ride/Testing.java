package jonathan.mapview.com.ride;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by PAUL on 6/24/2018.
 */

public class Testing extends AppCompatActivity {
    private String desc[] = {
            "pickup", "dropoff"};
    private Double item []={ 0.2222,3.444};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spin);

       // String mine=desc[1];
        double doub=item[1];

        String converted =String.valueOf(doub);
        Toast.makeText(getApplication(),"item is:"+converted,Toast.LENGTH_LONG).show();


    }
}
