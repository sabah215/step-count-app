package com.example.datacollectionlib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.function.*;

import com.example.datacollectionlib.permissionUtil.PermissionManager;
import com.example.datacollectionlib.permissionUtil.Permissions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnSuccessListener;

import android.util.Log;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity:";
    private FitnessOptions fitnessOptions;
    private float steps;
    private TextView stepsTextView;

    private OnDataPointListener mListener;
//    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



         fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = getGoogleAccount();

        if(!GoogleSignIn.hasPermissions(account, fitnessOptions)){
            Log.i(TAG, "Has no Permission");
            GoogleSignIn.requestPermissions(this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
        } else {

//            accessGoogleFit();
            Log.i(TAG,String.valueOf(DataType.TYPE_STEP_COUNT_DELTA));
            getDataUsingSensor(DataType.TYPE_STEP_COUNT_DELTA);




//            getActiveSubscriptions();
        }
    }

    /*
     * Register Sensor Client to get Real Time Data
     */
    private void getDataUsingSensor(DataType dataType) {
        Log.i(TAG, "From getDataUsingSensor()");
        // [START register_data_listener]
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.e(TAG, "Detected DataPoint field: " + field.getName());
                    Log.e(TAG, "Detected DataPoint value: " + val);

                }
            }
        };
        Fitness.getSensorsClient(this, getGoogleAccount())
                .add(new SensorRequest.Builder()
                                .setDataType(dataType)
                                .setSamplingRate(1, TimeUnit.SECONDS)  // sample once per minute
                                .build(), mListener
//                                    dataPoint -> {
//                                    float value = Float.parseFloat(dataPoint.getValue(Field.FIELD_STEPS).toString());
//                                   steps = Float.parseFloat(new DecimalFormat("#.##").format(value + steps)
                                   );
//                                }
//                );
        Log.i(TAG, "No. of Steps: "+ mListener.toString());
    }


    private void accessGoogleFit(){
        Fitness.getRecordingClient(this, getGoogleAccount())
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(unused ->
                        Log.i(TAG, "Successfully subscribed!"))
                .addOnFailureListener(e ->
                        Log.w(TAG,"There was a problem in subscribing.", e));


    }



    private void getActiveSubscriptions(){

        Fitness.getRecordingClient(this, getGoogleAccount())
                .listSubscriptions()
                .addOnSuccessListener(subscriptions -> {
                    for(Subscription sc : subscriptions){
                        DataType dt = sc.getDataType();
                        Log.i(TAG,"Active Subscription for data type: ${dt.name}");
                    }
                });
    }

    private GoogleSignInAccount getGoogleAccount(){
        return GoogleSignIn.getAccountForExtension(this, fitnessOptions);
    }

}