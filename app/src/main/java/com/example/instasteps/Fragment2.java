package com.example.instasteps;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tv3, tv4;
    private Button clearButton;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> dateArray;
    private ArrayList<Integer> dateSumSteps;
    private ArrayList<Boolean> dateTotalShown;
//    HashMap<String, ArrayList<String>> map;
    ArrayList<String>str;
    ArrayList<String> tempData;
    private Double overallDailyAverageSteps;

    public Fragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment2 newInstance(String param1, String param2) {
        Fragment2 fragment = new Fragment2();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);

//        map = new HashMap<String, ArrayList<String>>();
        dateArray = new ArrayList<>();
        dateSumSteps = new ArrayList<>();
        dateTotalShown = new ArrayList<>();
        tempData = new ArrayList<>();
        overallDailyAverageSteps = 0.0;


        tv3 = view.findViewById(R.id.tv3_frag);
        tv4 = view.findViewById(R.id.tv4);
        tv3.setText("");
        tv3.setText(readFromFile());

        showData();

//        for (int i = 0; i < dateArray.size(); i++) {
//            tv4.setText(dateArray.get(i) + ": "+dateSumSteps.get(i));
//        }
        if(readFromFile().equals("")){
            tv3.setText("No data found. Pull down to refresh data.");
        }




        clearButton = view.findViewById(R.id.clearButton);
        swipeRefreshLayout = view.findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tv3.setText("");
                tv3.setText(readFromFile());
                showData();
                if(readFromFile().equals("")){
                    tv3.setText("No data found. Pull down to refresh data.");
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity()).setTitle("Set Goal")
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to clear all data?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv3.setText("No data found. Pull down to refresh data.");
                                clearTextData();
                                onResume();

                            }
                        });

                materialAlertDialogBuilder.show();

            }
        });
        return view;
    }

    private void showData() {
        String overallstring = "";
        int todaysTotal = 0;
        String todayDate = getDate();

        for (int i = 0; i < dateArray.size(); i++) {
            String tempString = "";
            if(todayDate.equals(dateArray.get(i))){
                todaysTotal = dateSumSteps.get(i);
            }
            //tempString += dateArray.get(i) +"\n";
            //int temptotal = 0;
            for (int j = 0; j < tempData.size(); j++) {

                String[] splitArray = new String[]{};
                try {
                    splitArray = tempData.get(j).split("\\s+");
                } catch (PatternSyntaxException ex) {
                    ex.printStackTrace();
                }

                if(splitArray[0].equals(dateArray.get(i))){
                    Log.d("~waqqas", "onCreateView: tempdata" +tempData.get(j));
                    tempString += tempData.get(j)+"\n";
                    //temptotal += Integer.parseInt(splitArray[2]);

                }
            }
            tempString += dateArray.get(i) +": Total Steps -> "+ dateSumSteps.get(i)+"\n";
            overallstring +=tempString+"\n";
        }

        int overalltotal = 0;
        for (int i = 0; i < dateSumSteps.size(); i++) {
            overalltotal += dateSumSteps.get(i);
        }
        overallDailyAverageSteps = overalltotal*1.0/dateSumSteps.size();

        tv3.setText(overallstring);
        tv4.setText("Todays total steps: "+todaysTotal+"\nAverage daily steps: "+ String.format("%.2f", overallDailyAverageSteps));

    }


    private String readFromFile() {

        String ret = "";

        try {
            FileInputStream inputStream = getActivity().openFileInput("mysteps.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                dateArray = new ArrayList<>();
                dateSumSteps = new ArrayList<>();
                dateTotalShown = new ArrayList<>();

                tempData = new ArrayList<>();

                while ((receiveString = bufferedReader.readLine()) != null) {
                        Log.d("~waqqas", "readFromFile: "+receiveString);
                        stringBuilder.append(receiveString);

                        tempData.add(receiveString);

                        if(receiveString.length()>0) {
                            String[] splitArray = new String[]{};
                            try {
                                splitArray = receiveString.split("\\s+");
                            } catch (PatternSyntaxException ex) {
                                ex.printStackTrace();
                            }
                            String date = splitArray[0];
                            Log.d("~waqqas", "readFromFile: date"+date);
                            int total = 0;
                            int currentStringTotal;
                            if(dateArray.contains(date)){
                                int ind = dateArray.indexOf(date);
                                Log.d("~waqqas", "readFromFile: index "+ind);
                                currentStringTotal = Integer.parseInt(splitArray[2]);
                                Log.d("~waqqas", "readFromFile: currentStringTotal " +currentStringTotal);
                                int newtotal = dateSumSteps.get(ind) + currentStringTotal;
                                dateSumSteps.set(ind, newtotal);
                                Log.d("~waqqas", "readFromFile: datesumsteps " + newtotal);
                                total = dateSumSteps.get(ind);
                            }
                            else {
                                dateArray.add(date);
                                dateSumSteps.add(Integer.parseInt(splitArray[2]));
                                total = Integer.parseInt(splitArray[2]);

                            }
                            //dateArray.add();
                            Log.d("~waqqas", "readFromFile: total "+total);
                            stringBuilder.append(System.getProperty("line.separator"));
                            stringBuilder.append("total for "+ date + ": "+total);
                        }

                        stringBuilder.append(System.getProperty("line.separator"));
                }

//                str = new ArrayList<>();
//                str = map.get("2020/07/09");
//                for (int i = 0; i < str.size(); i++) {
//                    tv4.setText(str.get(i));
//                }

                inputStream.close();
                ret = stringBuilder.toString();

            }
        } catch (FileNotFoundException e) {
            Log.e("fragment 2", "File not found: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public void onResume() {
        super.onResume();


    }
    private void clearTextData(){
        String currentTotalSteps = "";
        FileOutputStream fos = null;
//        String previousText = readFromFile();
//        String newText = currentTotalSteps;

        try {
            fos = getActivity().openFileOutput("mysteps.txt", Context.MODE_PRIVATE);
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
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}