package com.halinhit.eduonline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Frag_OverView extends Fragment {

    private TextView txtMediumScore, txtStatusDone, txtPercentOne, txtPercentTwo, txtPercentThree, txtPercentFour, txtPercentFive;
    private RecyclerView reHighScore, reLowScore;

    private DatabaseReference classRef, resultRef;
    private String idTask, idClass, mediumScore;
    private int numMember, numComplete, numOne, numTwo, numThree, numFour;
    private float totalScore, score;
    private Task currentTask;
    private List<ResultTask> resultTaskList, higheScoreList, lowScoreList;
    private RecyclerScoreAdapter highscoreAdapter, lowscoreAdapter;
    private LinearLayoutManager layoutManager;
    private View view;
    private Classroom currentClass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_overview, container, false);
        currentTask = (Task) getArguments().getSerializable("InformationTask");
        idTask = currentTask.getId();
        idClass = currentTask.getIdClass();
        init();
        resultTaskList = new ArrayList<>();
        lowScoreList = new ArrayList<>();
        higheScoreList = new ArrayList<>();

        highscoreAdapter = new RecyclerScoreAdapter(getContext(), higheScoreList, this);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        reHighScore.setLayoutManager(layoutManager);
        reHighScore.setAdapter(highscoreAdapter);
        reHighScore.setHasFixedSize(true);

        lowscoreAdapter = new RecyclerScoreAdapter(getContext(), lowScoreList, this);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        reLowScore.setLayoutManager(layoutManager);
        reLowScore.setAdapter(lowscoreAdapter);
        reLowScore.setHasFixedSize(true);
        classRef = FirebaseDatabase.getInstance().getReference("Classroom").child(idClass);
        resultRef = classRef.child("task").child(idTask).child("result");
        resultRef.orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultTaskList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        resultTaskList.add(snapshot.getValue(ResultTask.class));
                    }
                    numComplete = resultTaskList.size();
                    classRef.child("numMember").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                numMember = dataSnapshot.getValue(int.class);
                                SortArray();
                                if (getActivity() != null) {
                                    txtStatusDone.setText(getString(R.string.doneall, numComplete, numMember));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void init() {
        txtMediumScore = view.findViewById(R.id.txtMediumScore);
        txtStatusDone = view.findViewById(R.id.txtStatusDone);
        txtPercentOne = view.findViewById(R.id.txtPercentOne);
        txtPercentTwo = view.findViewById(R.id.txtPercentTwo);
        txtPercentThree = view.findViewById(R.id.txtPercentThree);
        txtPercentFour = view.findViewById(R.id.txtPercentFour);
        txtPercentFive = view.findViewById(R.id.txtPercentFive);
        reHighScore = view.findViewById(R.id.reHighScore);
        reLowScore = view.findViewById(R.id.reLowScore);
    }

    private void SetListDataRecycler() {
        lowScoreList.clear();
        higheScoreList.clear();
        for (int i = 0; i < resultTaskList.size(); i++) {
            if (Float.parseFloat(resultTaskList.get(i).getScore()) <= 3) {
                lowScoreList.add(resultTaskList.get(i));
                lowscoreAdapter.notifyDataSetChanged();
            }
        }
        for (int i = resultTaskList.size() - 1; i >= 0; i--) {
            if (Float.parseFloat(resultTaskList.get(i).getScore()) >= 8) {
                higheScoreList.add(resultTaskList.get(i));
                highscoreAdapter.notifyDataSetChanged();
            }
        }
    }

    private void UpdateScoreMedium() {
        numOne = 0;
        numTwo = 0;
        numThree = 0;
        numFour = 0;
        totalScore = 0;
        if (numComplete == 0) {
            txtMediumScore.setText("0");
        } else {
            for (int i = 0; i < resultTaskList.size(); i++) {
                score = Float.parseFloat(resultTaskList.get(i).getScore());
                if (score > 9) numOne += 1;
                else if (score >= 8) numTwo += 1;
                else if (score >= 6.5) numThree += 1;
                else numFour += 1;
                totalScore += score;
            }
            mediumScore = String.valueOf((double) Math.round((totalScore / numComplete) * 100) / 100);
            if (mediumScore.endsWith(".0")) {
                mediumScore = mediumScore.substring(0, mediumScore.indexOf(".0"));
            }
            txtMediumScore.setText(mediumScore);
            float percent;
            float percentlast = 1;
            NumberFormat numEN = NumberFormat.getPercentInstance();
            percent = Math.abs((float) numOne / numMember);
            String percentOne = numEN.format(percent);
            percentlast = percentlast - percent;

            percent = Math.abs((float) numTwo / numMember);
            String percentTwo = numEN.format(percent);
            percentlast = percentlast - percent;

            percent = Math.abs((float) numThree / numMember);
            String percentThree = numEN.format(percent);
            percentlast = percentlast - percent;

            percent = Math.abs((float) numFour / numMember);
            String percentFour = numEN.format(percent);
            percentlast = percentlast - percent;

            String percentFive = numEN.format(Math.abs(percentlast));
            txtPercentOne.setText(percentOne);
            txtPercentTwo.setText(percentTwo);
            txtPercentThree.setText(percentThree);
            txtPercentFour.setText(percentFour);
            txtPercentFive.setText(percentFive);
        }
    }

    private void SortArray() {
        /*for (int i = 0; i < resultTaskList.size() - 1; i++) {
            for (int j = i + 1; j < resultTaskList.size(); j++) {
                if (Float.parseFloat(resultTaskList.get(i).getScore()) > Float.parseFloat(resultTaskList.get(j).getScore())) {
                    String cache = resultTaskList.get(i).getScore();
                    resultTaskList.get(i).setScore(resultTaskList.get(j).getScore());
                    resultTaskList.get(j).setScore(cache);
                }
            }
        }*/
        SetListDataRecycler();
        UpdateScoreMedium();
    }
}
