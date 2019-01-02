package com.taranga.crimetracker.view.crime_history;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taranga.crimetracker.R;
import com.taranga.crimetracker.adapter.CrimeHistoryAdapter;
import com.taranga.crimetracker.model.CrimeModel;
import com.taranga.crimetracker.model.event_bus.CrimeMasterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeHistory extends Fragment implements ValueEventListener, CrimeHistoryAdapter.RequestItemRemoveClickListener {

    private View rootView;

    private static final String TAG = CrimeHistory.class.getSimpleName();
    private RecyclerView recyclerCrimeHistory;
    private TextView textNoHistory;
    private List<CrimeMasterModel> crimeMasterModelList = new ArrayList<>();
    private List<CrimeModel> crimeModelList = new ArrayList<>();
    private CrimeHistoryAdapter crimeHistoryAdapter;
    private ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference childReference = databaseReference.child("crimedetails");

    public CrimeHistory() {
        // Required empty public constructor;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_crime_history, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViews();
    }

    private void setUpViews() {

        textNoHistory = (TextView) rootView.findViewById(R.id.no_history);
        recyclerCrimeHistory = (RecyclerView) rootView.findViewById(R.id.recycler_crime_history);

        textNoHistory.setVisibility(View.GONE);
        recyclerCrimeHistory.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
            }
        }, 2000);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        crimeMasterModelList.clear();

        Log.d("Crime History", "onDataChange: " + dataSnapshot.toString());
        Log.d("Crime History", "onDataChange:  values: " + dataSnapshot.getValue());

        Iterable<DataSnapshot> childList = dataSnapshot.getChildren();

        for (DataSnapshot snapshot : childList) {

            CrimeMasterModel crimeMasterModel = new CrimeMasterModel();
            crimeMasterModel.setKey(snapshot.getKey());
            crimeMasterModel.setCrimeModel(snapshot.getValue(CrimeModel.class));
            crimeMasterModelList.add(crimeMasterModel);
        }

        Log.d(TAG, "onDataChange: masterList: " + crimeMasterModelList.toString());

        if (crimeMasterModelList.size() > 0) {
            textNoHistory.setVisibility(View.GONE);
            recyclerCrimeHistory.setVisibility(View.VISIBLE);

            LinearLayoutManager llm1 = new LinearLayoutManager(getContext());
//            recyclerCrimeHistory.addItemDecoration(new DividerItemDecoration(getContext(), 0));
            recyclerCrimeHistory.setLayoutManager(llm1);
            crimeHistoryAdapter = new CrimeHistoryAdapter(getContext(), crimeMasterModelList, this);

            recyclerCrimeHistory.setAdapter(crimeHistoryAdapter);

            crimeHistoryAdapter.notifyDataSetChanged();
        } else {
            recyclerCrimeHistory.setVisibility(View.GONE);
            textNoHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onStart() {
        super.onStart();
        childReference.addValueEventListener(this);
    }

    @Override
    public void removeItemFromDatabase(int position, List<CrimeMasterModel> crimeMasterModels, String key, CrimeModel crimeModel) {

        Log.d(TAG, "removeItemFromDatabase: position: " + position);
        Log.d(TAG, "removeItemFromDatabase: CrimeModel: " + crimeModel);
        Log.d(TAG, "removeItemFromDatabase: key: " + key);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure to delete permanently ?")
                .setMessage("If Ok select \"YES\" else select \"NO\"")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        childReference.child(key).removeValue();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
