package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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

public class Frag_QuesParam extends Fragment {

    private RelativeLayout layoutSelect;
    private Button btnSort, btnViewFile, btnSerialUp, btnSerialDown, btnCorrectUp, btnIncorrectUp;
    private TextView txtNotice;
    private RecyclerView reQuesParam;

    private List<QuesParam> answerList;
    private RecyclerQuesParamAdapter paramAdapter;
    private LinearLayoutManager layoutManager;
    private DatabaseReference resultRef;
    private Task currentTask;
    private String idTask, idClass, answerTask, urlFile, nameFile, answerUser;
    private int numQues;
    private View view;
    private QuesParam quesParam;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_quesparam, container, false);
        btnSort = view.findViewById(R.id.btnSort);
        btnViewFile = view.findViewById(R.id.btnViewFile);
        reQuesParam = view.findViewById(R.id.reQuesParam);
        txtNotice = view.findViewById(R.id.txtNotice);
        layoutSelect = view.findViewById(R.id.layoutSelect);
        currentTask = (Task) getArguments().getSerializable("InformationTask");
        idTask = currentTask.getId();
        idClass = currentTask.getIdClass();
        answerTask = currentTask.getAnswer();
        nameFile = currentTask.getTitle();
        urlFile = currentTask.getUrlFile();
        numQues = currentTask.getNumQuestion();
        answerList = new ArrayList<>();
        for (int i = 0; i < numQues; i++) {
            answerList.add(new QuesParam(i + 1, 0, 0));
        }
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        resultRef = FirebaseDatabase.getInstance().getReference("Classroom").child(idClass).child("task").child(idTask).child("result");
        resultRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    txtNotice.setVisibility(View.GONE);
                    layoutSelect.setVisibility(View.VISIBLE);
                    reQuesParam.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        answerUser = snapshot.getValue(ResultTask.class).getAnswerUser();
                        for (int i = 0; i < answerUser.length(); i++) {
                            if (!String.valueOf(answerUser.charAt(i)).equals("?")) {
                                if (answerUser.charAt(i) == answerTask.charAt(i)) {
                                    answerList.get(i).setNumCorrect(answerList.get(i).getNumCorrect() + 1);
                                } else {
                                    answerList.get(i).setNumIncorrect(answerList.get(i).getNumIncorrect() + 1);
                                }
                            }
                        }
                    }
                    reQuesParam.setLayoutManager(layoutManager);
                    paramAdapter = new RecyclerQuesParamAdapter(getContext(), answerList);
                    reQuesParam.setAdapter(paramAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Sắp xếp theo:");
                LayoutInflater inflatDialog = LayoutInflater.from(getContext());
                View viewDialog = inflatDialog.inflate(R.layout.dialog_select_sortype, null);
                builder.setView(viewDialog);
                final AlertDialog alertDialog = builder.show();
                btnSerialUp = viewDialog.findViewById(R.id.btnSerialUp);
                btnSerialDown = viewDialog.findViewById(R.id.btnSerialDown);
                btnCorrectUp = viewDialog.findViewById(R.id.btnCorrectUp);
                btnIncorrectUp = viewDialog.findViewById(R.id.btnIncorrectUp);
                btnSerialUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SortList(">", "serial");
                        alertDialog.dismiss();
                        paramAdapter.notifyDataSetChanged();
                    }
                });
                btnSerialDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SortList("<", "serial");
                        alertDialog.dismiss();
                        paramAdapter.notifyDataSetChanged();
                    }
                });
                btnCorrectUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SortList(">", "numcorrect");
                        alertDialog.dismiss();
                        paramAdapter.notifyDataSetChanged();
                    }
                });
                btnIncorrectUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SortList(">", "numincorrect");
                        alertDialog.dismiss();
                        paramAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        btnViewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFileTask showFileTask = new ShowFileTask(getContext(), nameFile, urlFile);
                showFileTask.showDialog();
            }
        });
        return view;
    }

    private void SortList(String sign, String sortby) {
        if (sign.equals(">")) {
            if (sortby.equals("serial")) {
                for (int i = 0; i < answerList.size() - 1; i++) {
                    for (int j = i + 1; j < answerList.size(); j++) {
                        if (answerList.get(i).getSerial() > answerList.get(j).getSerial()) {
                            convert(i, j);
                        }
                    }
                }
            } else if (sortby.equals("numcorrect")) {
                for (int i = 0; i < answerList.size() - 1; i++) {
                    for (int j = i + 1; j < answerList.size(); j++) {
                        if (answerList.get(i).getNumCorrect() > answerList.get(j).getNumCorrect()) {
                            convert(i, j);
                        }
                    }
                }
            } else if (sortby.equals("numincorrect")) {
                for (int i = 0; i < answerList.size() - 1; i++) {
                    for (int j = i + 1; j < answerList.size(); j++) {
                        if (answerList.get(i).getNumIncorrect() > answerList.get(j).getNumIncorrect()) {
                            convert(i, j);
                        }
                    }
                }
            }
        } else if (sign.equals("<")) {
            if (sortby.equals("serial")) {
                for (int i = 0; i < answerList.size() - 1; i++) {
                    for (int j = i + 1; j < answerList.size(); j++) {
                        if (answerList.get(i).getSerial() < answerList.get(j).getSerial()) {
                            convert(i, j);
                        }
                    }
                }
            }
        }
    }

    private void convert(int i, int j) {
        quesParam = answerList.get(i);
        answerList.set(i, answerList.get(j));
        answerList.set(j, quesParam);
    }
}
