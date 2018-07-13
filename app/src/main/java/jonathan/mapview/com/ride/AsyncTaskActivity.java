package jonathan.mapview.com.ride;

/**
 * Created by PAUL on 6/20/2018.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AsyncTaskActivity extends AppCompatActivity {

    private final String ASYNC_TASK_TAG = "ASYNC_TASK";

    private Button executeAsyncTaskButton;
    private Button cancelAsyncTaskButton;
    private ProgressBar asyncTaskProgressBar;
    private TextView asyncTaskLogTextView;

    private MyAsyncTask myAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);

        setTitle("dev2qa.com - AsyncTask Example");

        this.executeAsyncTaskButton = (Button)findViewById(R.id.executeAsyncTaskButton);
        this.executeAsyncTaskButton.setEnabled(true);

        this.cancelAsyncTaskButton = (Button)findViewById(R.id.cancelAsyncTaskButton);
        this.cancelAsyncTaskButton.setEnabled(false);

        this.asyncTaskProgressBar = (ProgressBar)findViewById(R.id.asyncTaskProgressBar);
        this.asyncTaskLogTextView = (TextView)findViewById(R.id.asyncTaskLogTextView);

        executeAsyncTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Need to create a new MyAsyncTask instance for each call,
                // otherwise there will through an exception.
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(Integer.parseInt("10"));

                executeAsyncTaskButton.setEnabled(false);
                cancelAsyncTaskButton.setEnabled(true);
            }
        });

        cancelAsyncTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel a running task, then MyAsyncTask's onCancelled(String result) method will be invoked.
                myAsyncTask.cancel(true);
            }
        });
    }

    // MyAsyncTask is used to demonstrate async task process.
    private class MyAsyncTask extends AsyncTask<Integer, Integer, String>{

        // onPreExecute() is used to do some UI operation before performing background tasks.
        @Override
        protected void onPreExecute() {
            asyncTaskLogTextView.setText("Loading");
            Log.i(ASYNC_TASK_TAG, "onPreExecute() is executed.");
        }

        // doInBackground(String... strings) is used to execute background task, can not modify UI component in this method.
        // It return a String object which can be used in onPostExecute() method.
        @Override
        protected String doInBackground(Integer... inputParams) {

            StringBuffer retBuf = new StringBuffer();
            boolean loadComplete = false;

            try
            {
                Log.i(ASYNC_TASK_TAG, "doInBackground(" + inputParams[0] + ") is invoked.");

                int paramsLength = inputParams.length;
                if(paramsLength > 0) {
                    Integer totalNumber = inputParams[0];
                    int totalNumberInt = totalNumber.intValue();

                    for(int i=0;i < totalNumberInt; i++)
                    {
                        // First calculate progress value.
                        int progressValue = (i * 100 ) / totalNumberInt;

                        //Call publishProgress method to invoke onProgressUpdate() method.
                        publishProgress(progressValue);

                        // Sleep 0.2 seconds to demo progress clearly.
                        Thread.sleep(200);
                    }

                    loadComplete = true;
                }
            }catch(Exception ex)
            {
                Log.i(ASYNC_TASK_TAG, ex.getMessage());
            }finally {
                if(loadComplete) {
                    // Load complete display message.
                    retBuf.append("Load complete.");
                }else
                {
                    // Load cancel display message.
                    retBuf.append("Load canceled.");
                }
                return retBuf.toString();
            }
        }

        // onPostExecute() is used to update UI component and show the result after async task execute.
        @Override
        protected void onPostExecute(String result) {
            Log.i(ASYNC_TASK_TAG, "onPostExecute(" + result + ") is invoked.");
            // Show the result in log TextView object.
            asyncTaskLogTextView.setText(result);

            asyncTaskProgressBar.setProgress(100);

            executeAsyncTaskButton.setEnabled(true);
            cancelAsyncTaskButton.setEnabled(false);
        }

        // onProgressUpdate is used to update async task progress info.
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(ASYNC_TASK_TAG, "onProgressUpdate(" + values + ") is called");
            asyncTaskProgressBar.setProgress(values[0]);
            asyncTaskLogTextView.setText("loading..." + values[0] + "%");
        }

        // onCancelled() is called when the async task is cancelled.
        @Override
        protected void onCancelled(String result) {
            Log.i(ASYNC_TASK_TAG, "onCancelled(" + result + ") is invoked.");
            // Show the result in log TextView object.
            asyncTaskLogTextView.setText(result);
            asyncTaskProgressBar.setProgress(0);

            executeAsyncTaskButton.setEnabled(true);
            cancelAsyncTaskButton.setEnabled(false);
        }
    }
}
