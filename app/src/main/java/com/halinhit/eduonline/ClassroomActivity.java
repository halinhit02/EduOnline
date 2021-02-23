package com.halinhit.eduonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClassroomActivity extends AppCompatActivity {

    private TextView txtTitle;
    private ImageButton btnBack, imgBtnExitClass, imgBtnCreate;
    private ViewPager pager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    private FragmentManager fragmentManager;
    private Classroom classroom;
    private DatabaseReference classRef;
    private AlertDialog.Builder alertExitClass;
    private String uidUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        classroom = (Classroom) getIntent().getSerializableExtra("InformationClass");
        txtTitle = findViewById(R.id.txtTitle);
        btnBack = findViewById(R.id.imgBtnBack);
        tabLayout = findViewById(R.id.tabs);
        pager = findViewById(R.id.container);
        imgBtnExitClass = findViewById(R.id.imgBtnExitClass);
        imgBtnCreate = findViewById(R.id.imgBtnCreate);
        txtTitle.setText(classroom.getTitle());
        uidUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uidUser.equals(classroom.getUidCreator())) {
            imgBtnExitClass.setVisibility(View.GONE);
            imgBtnCreate.setVisibility(View.VISIBLE);
        } else {
            imgBtnCreate.setVisibility(View.GONE);
            imgBtnExitClass.setVisibility(View.VISIBLE);
        }
        fragmentManager = getSupportFragmentManager();
        pagerAdapter = new PagerAdapter(fragmentManager);
        pagerAdapter.setArgunment(classroom);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });
        imgBtnExitClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertExitClass = new AlertDialog.Builder(ClassroomActivity.this);
                alertExitClass.setTitle("Thông báo")
                        .setMessage("Bạn có muốn thoát lớp học này không?")
                        .setCancelable(true)
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if (CheckInternetConnection.getConnectivityStatus(ClassroomActivity.this) != CheckInternetConnection.TYPE_NOT_CONNECTED) {
                                    classRef = FirebaseDatabase.getInstance().getReference("Classroom").child(classroom.getId());
                                    classRef.child("member").child(uidUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                onBackPressed();
                                                classroom.setNumMember(classroom.getNumMember() - 1);
                                                classRef.child("numMember").setValue(classroom.getNumMember());
                                                Toast.makeText(ClassroomActivity.this, "Bạn đã thoát lớp học của " + classroom.getNameCreator(), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                } else
                                    Toast.makeText(ClassroomActivity.this, "Không có kết nối!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertExitClass.show();
            }
        });
        imgBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inCreateTask = new Intent(ClassroomActivity.this, CreateTaskActivity.class);
                inCreateTask.putExtra("informationClass", classroom);
                startActivity(inCreateTask);
            }
        });
    }
}
