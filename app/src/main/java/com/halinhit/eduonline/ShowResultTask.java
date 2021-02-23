package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.util.Locale;

public class ShowResultTask {

    private Context mContext;
    private ResultTask resultTask;
    private Task currentTask;

    private TextView txtNameTask, txtNameUser, txtScore, txtCorrect, txtIncorrect, txtTimeDone;
    private Button btnViewFile, btnReturn, btnRework;
    private RecyclerView recyclerAnswer;
    private ConstraintLayout layoutNote;
    private AlertDialog alertShowReslt;
    private String timeLeftFormat;
    private RecyclerAnswerAdapter answerAdapter;

    public ShowResultTask(Context mContext, ResultTask resultTask, Task currentTask) {
        this.mContext = mContext;
        this.resultTask = resultTask;
        this.currentTask = currentTask;
    }

    public void ShowResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View customView = inflater.inflate(R.layout.dialog_showresult, null);
        txtNameTask = customView.findViewById(R.id.txtNameTask);
        txtNameUser = customView.findViewById(R.id.txtNameUser);
        txtScore = customView.findViewById(R.id.txtScore);
        txtCorrect = customView.findViewById(R.id.txtCorrect);
        txtIncorrect = customView.findViewById(R.id.txtIncorrect);
        txtTimeDone = customView.findViewById(R.id.txtTimeDone);
        btnViewFile = customView.findViewById(R.id.btnViewFile);
        btnReturn = customView.findViewById(R.id.btnReturn);
        recyclerAnswer = customView.findViewById(R.id.recyclerAnswer);
        layoutNote = customView.findViewById(R.id.layoutnote);
        btnRework = customView.findViewById(R.id.btnRework);

        txtNameTask.setText(resultTask.getNameFile());
        txtNameUser.setText(resultTask.getName());
        builder.setView(customView);
        builder.setCancelable(false);
        txtScore.setText(resultTask.getScore());
        txtCorrect.setText(mContext.getString(R.string.correct, resultTask.getNumCorrect()));
        txtIncorrect.setText(mContext.getString(R.string.incorrect, (resultTask.getNumQuestion() - resultTask.getNumCorrect())));
        if (currentTask != null) {
            btnRework.setVisibility(View.VISIBLE);
        } else btnRework.setVisibility(View.GONE);
        int minutes = (int) ((resultTask.getTimeDone() / 1000) / 60);
        int seconds = (int) ((resultTask.getTimeDone() / 1000) % 60);
        timeLeftFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds + 1);
        txtTimeDone.setText(mContext.getString(R.string.time, timeLeftFormat));
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof TaskActivity) {
                    ((TaskActivity) mContext).onBackPressed();
                    ((TaskActivity) mContext).finish();
                } else {
                    alertShowReslt.dismiss();
                }
            }
        });
        if (resultTask.getShowAnswer().equals("true")) {
            recyclerAnswer.setVisibility(View.VISIBLE);
            layoutNote.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            answerAdapter = new RecyclerAnswerAdapter(mContext, resultTask);
            recyclerAnswer.setAdapter(answerAdapter);
            recyclerAnswer.setLayoutManager(layoutManager);
            recyclerAnswer.setHasFixedSize(true);
            alertShowReslt = builder.show();
            alertShowReslt.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            layoutNote.setVisibility(View.GONE);
            recyclerAnswer.setVisibility(View.GONE);
            alertShowReslt = builder.show();
        }
        btnViewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFileTask showFileTask = new ShowFileTask(mContext, resultTask.getNameFile(), resultTask.getUrlFile());
                showFileTask.showDialog();
            }
        });
        btnRework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertShowReslt.dismiss();
                Intent intentTask = new Intent(mContext, TaskActivity.class);
                intentTask.putExtra("InformationTask", currentTask);
                intentTask.putExtra("Function", "Rework");
                mContext.startActivity(intentTask);
            }
        });
    }
}
