package com.halinhit.eduonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Frag_CreateAnswer extends Fragment {

    private Button btnA, btnB, btnC, btnD, btnDone;
    private ImageButton btnPrevious, btnNext;
    private GridView gridQuestion;
    private TextView txtQuestion;
    private List<String> answerList;
    private GridQuestionAdapter adapterGridQues;
    private ProgressDialog progressUpload;
    private StorageReference tasStokRef;
    private DatabaseReference taskDataRef;
    private String urlFile, answer, answerUser, score;
    private Uri uriFile;
    private int numQues, pos, numCorrect;
    private Task newTask;
    private View view;
    private Boolean editTask;
    private HashMap<String, Object> mapTaskUpdate, mapResultUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_createanswer, container, false);
        newTask = (Task) getArguments().getSerializable("newTask");
        uriFile = Uri.parse(newTask.getUrlFile());
        numQues = newTask.getNumQuestion();
        answer = newTask.getAnswer();
        taskDataRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(newTask.getIdClass()).child("task");
        init();
        SetupProgress();
        answerList = new ArrayList<>(numQues);
        editTask = CreateTaskActivity.editTask;
        if (answer.length() > 0) {
            for (int stt = 0; stt < numQues; stt++) {
                if (answer.length() < numQues & stt >= (answer.length() - 1)) {
                    answerList.add("?");
                    pos = answer.length() - 1;
                } else {
                    pos = 0;
                    answerList.add(String.valueOf(answer.charAt(stt)));
                }
            }
        } else {
            pos = 0;
            for (int stt = 0; stt < numQues; stt++) {
                answerList.add("?");
            }
        }
        adapterGridQues = new GridQuestionAdapter(getContext(), answerList);
        gridQuestion.setAdapter(adapterGridQues);
        SetAnswer(pos);
        gridQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                SetAnswer(pos);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "";
                for (int i = 0; i < numQues; i++) {
                    if (answerList.get(i).equals("?")) {
                        Toast.makeText(getContext(), "Nhập đáp án câu " + (i + 1), Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        answer = answer.concat(answerList.get(i).toUpperCase().trim());
                    }
                    if (i == answerList.size() - 1) {
                        newTask.setAnswer(answer);
                        if (editTask) {
                            UpdateTask();
                        } else {
                            UploadFile();
                        }
                    }
                }
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos -= 1;
                if (pos < 0) {
                    pos = answerList.size() - 1;
                }
                SetAnswer(pos);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos += 1;
                if (pos > answerList.size() - 1) {
                    pos = 0;
                }
                SetAnswer(pos);
            }
        });
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnA.setTextColor(Color.WHITE);
                UpateAnswer(pos, "A");
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnB.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnB.setTextColor(Color.WHITE);
                UpateAnswer(pos, "B");
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnC.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnC.setTextColor(Color.WHITE);
                UpateAnswer(pos, "C");
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnD.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnD.setTextColor(Color.WHITE);
                UpateAnswer(pos, "D");
            }
        });
        return view;
    }

    private void init() {
        txtQuestion = view.findViewById(R.id.txtQuestion);
        btnA = view.findViewById(R.id.btnA);
        btnB = view.findViewById(R.id.btnB);
        btnC = view.findViewById(R.id.btnC);
        btnD = view.findViewById(R.id.btnD);
        btnDone = view.findViewById(R.id.btnDone);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        gridQuestion = view.findViewById(R.id.gridAnswer);
    }

    private void SetupProgress() {
        progressUpload = new ProgressDialog(getContext());
        progressUpload.setCanceledOnTouchOutside(false);
    }

    private void UploadFile() {
        progressUpload.setMessage("Đang tải lên tệp ...");
        progressUpload.show();
        tasStokRef = FirebaseStorage.getInstance().getReference().child(newTask.getIdClass()).child("task").child(newTask.getId() + ".pdf");
        tasStokRef.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressUpload.dismiss();
                                urlFile = uri.toString();
                                newTask.setUrlFile(urlFile);
                                UploadTask();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressUpload.dismiss();
                        Toast.makeText(getActivity(), "Lỗi! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void UploadTask() {
        progressUpload.setMessage("Đang đăng bài tập...");
        progressUpload.show();
        taskDataRef.child(newTask.getId()).setValue(newTask).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    progressUpload.dismiss();
                    Toast.makeText(getActivity(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    progressUpload.dismiss();
                    Toast.makeText(getContext(), R.string.errorFirebase, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CaculateScore() {
        numCorrect = 0;
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == answerUser.charAt(i)) {
                numCorrect += 1;
            }
        }
        score = String.valueOf((double) Math.round((((float) 10) / numQues * numCorrect) * 100) / 100);
        if (score.endsWith(".0")) {
            score = score.substring(0, score.indexOf(".0"));
        }
    }

    private void UpdateTask() {
        mapTaskUpdate = new HashMap<>();
        mapTaskUpdate.put("title", newTask.getTitle());
        mapTaskUpdate.put("date", newTask.getDate());
        mapTaskUpdate.put("dateFinish", newTask.getDateFinish());
        mapTaskUpdate.put("answer", newTask.getAnswer());
        mapTaskUpdate.put("showAnswer", newTask.getShowAnswer());
        progressUpload.setMessage("Đang cập nhật bài tập...");
        progressUpload.show();
        if (!newTask.getAnswer().equals(CreateTaskActivity.answerTask) || !newTask.getTitle().equals(CreateTaskActivity.title)) {
            mapResultUpdate = new HashMap<>();
            mapResultUpdate.put("nameFile", newTask.getTitle());
            mapResultUpdate.put("answerTask", answer);
            taskDataRef.child(newTask.getId()).child("result").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            answerUser = snapshot.child("answerUser").getValue(String.class);
                            CaculateScore();
                            mapResultUpdate.put("score", score);
                            mapResultUpdate.put("numCorrect", numCorrect);
                            snapshot.getRef().updateChildren(mapResultUpdate);
                        }
                    }
                    taskDataRef.child(newTask.getId()).updateChildren(mapTaskUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressUpload.dismiss();
                                Toast.makeText(getContext(), "Bài tập đã được cập nhật!", Toast.LENGTH_SHORT).show();
                                Objects.requireNonNull(getActivity()).onBackPressed();
                            } else {
                                progressUpload.dismiss();
                                Toast.makeText(getContext(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            taskDataRef.child(newTask.getId()).updateChildren(mapTaskUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressUpload.dismiss();
                        Toast.makeText(getContext(), "Bài tập đã được cập nhật!", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        progressUpload.dismiss();
                        Toast.makeText(getContext(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void UpateAnswer(int i1, String s) {
        answerList.set(i1, s);
        adapterGridQues.notifyDataSetChanged();
        gridQuestion.smoothScrollToPosition(i1);
        SetBtnNomal();
        i1 += 1;
        if (i1 > answerList.size() - 1) {
            i1 = 0;
        }
        pos = i1;
        SetAnswer(pos);
    }

    private void SetBtnNomal() {
        btnA.setBackgroundResource(R.drawable.custom_button_answer);
        btnA.setTextColor(Color.BLACK);
        btnB.setBackgroundResource(R.drawable.custom_button_answer);
        btnB.setTextColor(Color.BLACK);
        btnC.setBackgroundResource(R.drawable.custom_button_answer);
        btnC.setTextColor(Color.BLACK);
        btnD.setBackgroundResource(R.drawable.custom_button_answer);
        btnD.setTextColor(Color.BLACK);
    }

    private void SetAnswer(int i) {
        SetBtnNomal();
        txtQuestion.setText(getString(R.string.question, i + 1));
        String answer = answerList.get(i);
        switch (answer) {
            case "A":
                btnA.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnA.setTextColor(Color.WHITE);
                break;
            case "B":
                btnB.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnB.setTextColor(Color.WHITE);
                break;
            case "C":
                btnC.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnC.setTextColor(Color.WHITE);
                break;
            case "D":
                btnD.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
                btnD.setTextColor(Color.WHITE);
                break;
        }
    }
}
