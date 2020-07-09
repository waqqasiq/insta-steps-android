package com.example.instasteps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment implements SensorEventListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SensorManager sensorManager = null;
    private Boolean running = false;
    private float totalsteps = 0f;
    private float previousTotalSteps = 0f;
    private TextView tv1;
    private TextView tv2;
    private CircularProgressBar circularProgressBar;
    private float previousEventValue = 0f;
    private Boolean firstTime = true;
    private Boolean completed = false;
    private Button resetButton;
    private String [] stepGoals;
    float [] goalFloat;
    private int checked = 0;
    private MediaPlayer mp;
    SharedPreferences preferences;
    private final String FILE_NAME ="mysteps.txt";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //Toast.makeText(getActivity(), "This is fragment 1", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("~waqqas", "onSensorChanged: onResume");
        running = true;
        previousTotalSteps = totalsteps;

        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null){
            Toast.makeText(getActivity(), "No sensor detected on this device", Toast.LENGTH_SHORT).show();
        }
        else{
            sensorManager.registerListener((SensorEventListener) this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        resetButton = view.findViewById(R.id.resetButton);
//        stepGoals.add("1000 steps");
        goalFloat = new float[]{1000f, 2500f, 5000f, 10000f};
        stepGoals = new String[]{"1000 steps", "2500 steps", "5000 steps", "10000 steps"};
        mp = MediaPlayer.create(getActivity(), R.raw.tapsound);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Long Press RESET button",Toast.LENGTH_SHORT).show();
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity()).setTitle("Set Goal")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                circularProgressBar.setProgressMax(goalFloat[checked]);
                                tv2.setText("/"+(int)goalFloat[checked]);
                                //
                                save();
                                totalsteps = 0f;
                                tv1.setText(""+(int)totalsteps);
                                circularProgressBar.setProgress(totalsteps);
                                //
                                completed = false;
                            }
                        })
                        .setSingleChoiceItems(stepGoals, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                checked = which;


                            }
                        });
                materialAlertDialogBuilder.show();

            }
        });


        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("~waqqas", "onSensorChanged: "+totalsteps);
        if(running) {


                //totalsteps = event.values[0];
                Log.d("~waqqas", "onSensorChanged: "+event.values[0]);
                if (event.values[0] > 0) {
                    if(firstTime){
                        firstTime = false;
                    }else{
                        totalsteps += 1.0;
                        previousEventValue = event.values[0];
                    }
                }
                //circularProgressBar.setProgressMax(20f); //for debugging purpose
                if((int)circularProgressBar.getProgressMax()>=(int)totalsteps){
                    int currentSteps = (int)totalsteps - (int)previousTotalSteps;
                    tv1.setText(""+currentSteps);
                    //tv1.setText(currentSteps);
                    Log.d("~waqqas", "onSensorChanged: currentsteps"+currentSteps);

                    circularProgressBar.setProgressWithAnimation((float)currentSteps);
                }
                else {
                    if(!completed) {
                        completed = true;
                        mp.start();
                        String msg = "";
                        if(circularProgressBar.getProgressMax()==10000f){
                            msg = "Research says as few as 6000 steps per day correlate with a lower death rate.";
                        }
                        else {
                            msg = "How about setting a higher goal next time?";
                        }
                        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity()).setTitle("Set Goal")
                                .setTitle("Goal Accomplished")
                                .setMessage(msg + " Please reset your goal and keep going." )
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        materialAlertDialogBuilder.show();

                    }
                }



        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void save(){
        if(tv1.getText().toString().equals("0")){

        }
        else {
            String currentTotalSteps = "\n" + getDateTime() + ": " + tv1.getText().toString() +" steps \n";
            FileOutputStream fos = null;
//        String previousText = readFromFile();
//        String newText = currentTotalSteps;

            try {
                fos = getActivity().openFileOutput(FILE_NAME, Context.MODE_APPEND);
                fos.write(currentTotalSteps.getBytes());
                //Toast.makeText(getActivity(), "Saved to "+getActivity().getFilesDir()+"/"+FILE_NAME, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}