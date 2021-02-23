package com.halinhit.eduonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InforTaskActivity extends AppCompatActivity {

    private TextView txtTitle;
    private ViewPager viewPageTask;
    private TabLayout tabViewClass;
    private ImageButton imgBtnBack;

    private Task currentTask;
    private FragmentManager fragmentManager;
    private PagerClassAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_task);
        init();
        currentTask = (Task) getIntent().getSerializableExtra("InformationTask");
        if (currentTask.getTitle().length() > 20) {
            txtTitle.setText(currentTask.getTitle().substring(0, 20) + "...");
        } else txtTitle.setText(currentTask.getTitle());
        fragmentManager = getSupportFragmentManager();
        pagerAdapter = new PagerClassAdapter(fragmentManager);
        pagerAdapter.setArgunment(currentTask);
        viewPageTask.setAdapter(pagerAdapter);
        tabViewClass.setupWithViewPager(viewPageTask);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    private void init() {
        txtTitle = findViewById(R.id.txtTitle);
        viewPageTask = findViewById(R.id.viewPageTask);
        tabViewClass = findViewById(R.id.tabViewClass);
        imgBtnBack = findViewById(R.id.imgBtnBack);
    }
}
