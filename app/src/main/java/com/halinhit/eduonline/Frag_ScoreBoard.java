package com.halinhit.eduonline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class Frag_ScoreBoard extends Fragment {

    private TextView txtNotice;
    private RecyclerView reScoreBoard;
    private RecyclerScoreAdapter scoreAdapter;
    private LinearLayoutManager layoutManager;
    private List<ResultTask> resultTaskList;
    private DatabaseReference resultRef;
    private String idClass, idTask;
    private ResultTask resultTask;
    private Task currentTask;
    private Button btnSort;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_scoreboard, container, false);
        reScoreBoard = view.findViewById(R.id.reScoreBoard);
        txtNotice = view.findViewById(R.id.txtNotice);
        btnSort = view.findViewById(R.id.btnSort);
        currentTask = (Task) getArguments().getSerializable("InformationTask");
        idClass = currentTask.getIdClass();
        idTask = currentTask.getId();
        txtNotice.setVisibility(View.VISIBLE);
        resultTaskList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        scoreAdapter = new RecyclerScoreAdapter(getContext(), resultTaskList, this);
        reScoreBoard.setLayoutManager(layoutManager);
        reScoreBoard.setAdapter(scoreAdapter);
        reScoreBoard.setHasFixedSize(true);
        resultRef = FirebaseDatabase.getInstance().getReference("Classroom").child(idClass).child("task").child(idTask).child("result");
        resultRef.orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultTaskList = new ArrayList<>();
                if (dataSnapshot.exists() & getActivity() != null) {
                    btnSort.setVisibility(View.VISIBLE);
                    txtNotice.setVisibility(View.GONE);
                    for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                        resultTask = datasnap.getValue(ResultTask.class);
                        resultTaskList.add(resultTask);
                    }
                }
                if (resultTaskList.size() > 0) {
                    reScoreBoard.setVisibility(View.VISIBLE);
                    btnSort.setVisibility(View.VISIBLE);
                    txtNotice.setVisibility(View.GONE);
                    SortArrayUp();
                } else {
                    reScoreBoard.setVisibility(View.INVISIBLE);
                    btnSort.setVisibility(View.GONE);
                    txtNotice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        btnSort.setText("Sắp xếp: Điểm thấp");
        btnSort.setTag("SortUp");
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSort.getTag().equals("SortUp")) {
                    btnSort.setText("Sắp xếp: Điểm cao");
                    btnSort.setTag("SortDown");
                    reScoreBoard.smoothScrollToPosition(0);
                    layoutManager.setReverseLayout(true);
                } else {
                    layoutManager.setReverseLayout(false);
                    btnSort.setText("Sắp xếp: Điểm thấp");
                    btnSort.setTag("SortUp");
                    reScoreBoard.smoothScrollToPosition(0);
                }
                reScoreBoard.setLayoutManager(layoutManager);
                reScoreBoard.setHasFixedSize(true);

            }
        });
        return view;
    }

    private void SortArrayUp() {
        for (int i = 0; i < resultTaskList.size() - 1; i++) {
            for (int j = i + 1; j < resultTaskList.size(); j++) {
                if (Float.parseFloat(resultTaskList.get(i).getScore()) > Float.parseFloat(resultTaskList.get(j).getScore())) {
                    String cache = resultTaskList.get(i).getScore();
                    resultTaskList.get(i).setScore(resultTaskList.get(j).getScore());
                    resultTaskList.get(j).setScore(cache);
                }
            }
        }
        scoreAdapter = new RecyclerScoreAdapter(getContext(), resultTaskList, this);
        reScoreBoard.setLayoutManager(layoutManager);
        reScoreBoard.setAdapter(scoreAdapter);
    }
}
