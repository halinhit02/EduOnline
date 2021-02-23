package com.halinhit.eduonline;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Frag_Task extends Fragment implements RecyclerViewClickListener {

    private RecyclerView recyclerTask;
    private TextView txtNotice, txtNumTask;
    private List<Task> taskList;
    private RecyclerTaskAdapter taskAdapter;
    private LinearLayoutManager layoutManager;
    private DatabaseReference taskRef, scoreUserRef;
    private Task task;
    private ResultTask resultTask;
    private Classroom inforClass;
    private FirebaseUser userCurrent;
    private View view;
    private int REQUEST_STORAGE_CODE = 123;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_task, container, false);
        recyclerTask = view.findViewById(R.id.recyclerTask);
        txtNotice = view.findViewById(R.id.txtNotice);
        txtNumTask = view.findViewById(R.id.txtNumTask);
        userCurrent = FirebaseAuth.getInstance().getCurrentUser();
        assert getArguments() != null;
        inforClass = (Classroom) getArguments().getSerializable("informationClass");
        taskList = new ArrayList<>();
        taskAdapter = new RecyclerTaskAdapter(getContext(), taskList);
        taskAdapter.setClassCurrent(inforClass);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setReverseLayout(true);
        recyclerTask.setAdapter(taskAdapter);
        recyclerTask.setLayoutManager(layoutManager);
        recyclerTask.setHasFixedSize(true);
        taskAdapter.RecyclerViewClickListener(this);
        taskRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(inforClass.getId()).child("task");
        taskRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() & getActivity() != null) {
                    task = dataSnapshot.getValue(Task.class);
                    taskList.add(task);
                    taskAdapter.notifyDataSetChanged();
                    recyclerTask.scrollToPosition(taskList.size() - 1);
                    txtNotice.setVisibility(View.GONE);
                    txtNumTask.setVisibility(View.VISIBLE);
                    txtNumTask.setText(getString(R.string.numTask, taskList.size()));
                } else {
                    if (taskList.size() > 0) {
                        txtNotice.setVisibility(View.GONE);
                        txtNumTask.setVisibility(View.VISIBLE);
                    } else {
                        txtNumTask.setVisibility(View.GONE);
                        txtNotice.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    task = dataSnapshot.getValue(Task.class);
                    for (int i = 0; i < taskList.size(); i++) {
                        if (taskList.get(i).getId().equals(task.getId())) {
                            taskList.set(i, task);
                            taskAdapter.notifyItemChanged(i);
                            taskAdapter.notifyItemRangeChanged(i, taskList.size());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() & getActivity() != null) {
                    task = dataSnapshot.getValue(Task.class);
                    for (int i = 0; i < taskList.size(); i++) {
                        if (taskList.get(i).getId().equals(task.getId())) {
                            taskList.remove(i);
                            taskAdapter.notifyItemRemoved(i);
                            taskAdapter.notifyItemRangeRemoved(i, taskList.size());
                            recyclerTask.removeViewAt(i);
                            break;
                        }
                    }
                    txtNumTask.setText(getString(R.string.numTask, taskList.size()));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onClick(final View view, final int position) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_CODE);
        } else {
            if (taskList.get(position).getUidCreator().equals(userCurrent.getUid())) {
                Intent intentInforTask = new Intent(getContext(), InforTaskActivity.class);
                intentInforTask.putExtra("InformationTask", taskList.get(position));
                startActivity(intentInforTask);
            } else {
                scoreUserRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(taskList.get(position).getIdClass())
                        .child("task").child(taskList.get(position).getId()).child("result").child(userCurrent.getUid());
                scoreUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            resultTask = dataSnapshot.getValue(ResultTask.class);
                            ShowResultTask showResultTask = new ShowResultTask(getContext(), resultTask, taskList.get(position));
                            showResultTask.ShowResult();
                        } else {
                            if (view.getTag().equals("expired")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                        .setCancelable(true)
                                        .setTitle("Thông báo")
                                        .setMessage("Bài tập đã hết hạn!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setCancelable(true);
                                builder.setTitle("Thông báo")
                                        .setMessage(getContext().getString(R.string.message, taskList.get(position).getTimeDone()));
                                builder.setPositiveButton("Làm bài", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intentTask = new Intent(getContext(), TaskActivity.class);
                                        intentTask.putExtra("InformationTask", taskList.get(position));
                                        startActivity(intentTask);
                                    }
                                }).setNegativeButton("Trở về", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_CODE & grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Cần cấp quyền truy cập bộ nhớ!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
