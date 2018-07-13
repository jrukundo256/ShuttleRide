package jonathan.mapview.com.ride;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by PAUL on 7/10/2018.
 */

public class Videoplayer extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoview);
        VideoView myvidview=findViewById(R.id.vid);
        String address="http://192.168.43.236/myvideo.mp4";
        Uri myurl=Uri.parse(address);

        myvidview.setVideoURI(myurl);
        MediaController mconroller=new MediaController(this);
        mconroller.setAnchorView(myvidview);
        myvidview.setMediaController(mconroller);
        myvidview.start();
    }
}
