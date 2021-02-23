package com.halinhit.eduonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

public class CreateTaskActivity extends AppCompatActivity {

    private TextView txtTitle;
    private ImageButton imgBtnBack, imgBtnDelete;
    private Classroom inforClass;
    private Bundle bundle;
    private Frag_CreateInfor frag_createInfor;
    private Task currentTask;
    public static Boolean editTask;
    public static String answerTask, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        imgBtnBack = findViewById(R.id.imgBtnBack);
        txtTitle = findViewById(R.id.txtTitle);
        imgBtnDelete = findViewById(R.id.imgBtnDelete);
        inforClass = (Classroom) getIntent().getSerializableExtra("informationClass");
        bundle = new Bundle();
        bundle.putSerializable("informationClass", inforClass);
        editTask = false;
        if (getIntent().getSerializableExtra("editTask") != null) {
            currentTask = (Task) getIntent().getSerializableExtra("editTask");
            bundle.putSerializable("editTask", currentTask);
            txtTitle.setText("Chỉnh sửa bài tập");
            imgBtnDelete.setVisibility(View.VISIBLE);
            editTask = true;
            answerTask = currentTask.getAnswer();
            title = currentTask.getTitle();
        }
        frag_createInfor = new Frag_CreateInfor();
        frag_createInfor.setArguments(bundle);
        SetFragment(frag_createInfor);
        imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogDelete();
            }
        });
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });
    }

    private void ShowDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Nếu xóa bài tập toàn bộ điểm và thông tin cũng bị xóa. Bạn có muốn xóa bài tập này không?")
                .setCancelable(true)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseStorage.getInstance().getReference().child(currentTask.getIdClass() + "/task/" + currentTask.getId() + ".pdf")
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("Classroom").child(currentTask.getIdClass())
                                            .child("task").child(currentTask.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(CreateTaskActivity.this, "Đã xóa bài tập thành công!", Toast.LENGTH_SHORT).show();
                                                finish();
                                                onBackPressed();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(CreateTaskActivity.this, getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void SetFragment(Fragment frag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frameCreate, frag);
        transaction.commit();
    }
}
