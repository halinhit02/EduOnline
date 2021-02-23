package com.halinhit.eduonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private ImageButton imgBtnBack, imgBtnAnswer, imgBtnOpen;
    private Button btnSubmit, btnPreQues, btnNextQues, btnA, btnB, btnC, btnD, btnLoad;
    private PDFView pdfTask;
    private TextView txtTitle, txtQuestion, txtTimeLeft;
    private GridView gridAnswer;
    private ProgressBar progressLoad;

    private Task currentTask;
    private Button[] listBtn;
    private List<String> answerList, answerTaskList;
    private String timeDone, answerTask, answerUser;
    private int pos;
    private int numQues;
    private int numCorrect;
    private String score, nameUser, uidUser;
    private DatabaseReference taskRef;
    private SharedPreferences userSharedPref;
    private AlertDialog.Builder alertShowAnswer;
    private AlertDialog dialogShowAnswer;
    private GridQuestionAdapter adapterAnswer;
    private CountDownTimer countDownTimer;
    private long timeleftMilis, timeFinish;
    private boolean timeup;
    private ResultTask resultTask;
    private ProgressDialog progressUpload;
    private Boolean reWork;
    private AlertDialog.Builder alertNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        init();
        SetupProgressDialog();
        SetupDialogReturn();
        listBtn = new Button[]{btnA, btnB, btnC, btnD};
        userSharedPref = getSharedPreferences("User", MODE_PRIVATE);
        nameUser = userSharedPref.getString("name", "");
        uidUser = userSharedPref.getString("uid", "");
        currentTask = (Task) getIntent().getSerializableExtra("InformationTask");
        reWork = getIntent().getStringExtra("Function") != null;
        numQues = currentTask.getNumQuestion();
        timeDone = currentTask.getTimeDone().trim();
        answerTask = currentTask.getAnswer().toUpperCase().trim();
        timeFinish = Long.valueOf(timeDone) * 60000;
        timeleftMilis = timeFinish;
        score = "0";
        if (currentTask.getTitle().length() > 20) {
            txtTitle.setText(currentTask.getTitle().substring(0, 20) + "...");
        } else txtTitle.setText(currentTask.getTitle());
        LoadFile();
        answerList = new ArrayList<>();
        answerTaskList = new ArrayList<>();
        for (int stt = 0; stt < numQues; stt++) {
            answerList.add("?");
            answerTaskList.add(String.valueOf(answerTask.charAt(stt)));
        }
        pos = 0;
        SetAnswer(pos);
        imgBtnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertShowAnswer = new AlertDialog.Builder(TaskActivity.this);
                alertShowAnswer.setCancelable(true);
                LayoutInflater inflater = LayoutInflater.from(TaskActivity.this);
                View customView = inflater.inflate(R.layout.dialog_showanswer, null);
                alertShowAnswer.setView(customView);
                gridAnswer = customView.findViewById(R.id.gridAnswer);
                adapterAnswer = new GridQuestionAdapter(TaskActivity.this, answerList);
                gridAnswer.setAdapter(adapterAnswer);
                dialogShowAnswer = alertShowAnswer.show();
                gridAnswer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pos = position;
                        dialogShowAnswer.dismiss();
                        SetAnswer(pos);
                    }
                });
            }
        });
        imgBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
                intentOpenFile.setData(Uri.parse(currentTask.getUrlFile()));
                startActivity(intentOpenFile);
            }
        });
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadFile();
            }
        });
        btnPreQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 0) {
                    pos = numQues - 1;
                    SetAnswer(pos);
                } else {
                    pos -= 1;
                    SetAnswer(pos);
                }
            }
        });
        btnNextQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == numQues - 1) {
                    pos = 0;
                    SetAnswer(pos);
                } else {
                    pos += 1;
                    SetAnswer(pos);
                }

            }
        });
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBtnClick(btnA, "A");
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBtnClick(btnB, "B");
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBtnClick(btnC, "C");
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBtnClick(btnD, "D");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timeup) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this)
                            .setCancelable(true)
                            .setTitle("Thông báo")
                            .setMessage("Thời gian làm bài vẫn còn bạn có muốn nộp bài không?")
                            .setPositiveButton("Nộp bài", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertNotice = null;
                                    SubmitTask();
                                }
                            }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    private void init() {
        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnOpen = findViewById(R.id.imgBtnOpen);
        imgBtnAnswer = findViewById(R.id.imgBtnAnswer);
        pdfTask = findViewById(R.id.pdfTask);
        txtTitle = findViewById(R.id.txtTitle);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtTimeLeft = findViewById(R.id.txtTime);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNextQues = findViewById(R.id.btnNextQues);
        btnPreQues = findViewById(R.id.btnPreQues);
        btnLoad = findViewById(R.id.btnLoad);
        progressLoad = findViewById(R.id.progressLoad);
        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);
    }

    private void SetupProgressDialog() {
        progressUpload = new ProgressDialog(this);
        progressUpload.setCancelable(false);
        progressUpload.setMessage("Đang nộp bài...");
    }

    private void SubmitTask() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        progressUpload.show();
        numCorrect = 0;
        answerUser = "";
        for (int i = 0; i < answerList.size(); i++) {
            answerUser = answerUser.concat(answerList.get(i));
            if (answerList.get(i).toUpperCase().equals(answerTaskList.get(i))) {
                numCorrect += 1;
            }
        }
        score = String.valueOf((double) Math.round((((float) 10) / numQues * numCorrect) * 100) / 100);
        if (score.endsWith(".0")) {
            score = score.substring(0, score.indexOf(".0"));
        }
        resultTask = new ResultTask(nameUser, uidUser, score, answerUser, answerTask, currentTask.getShowAnswer(), currentTask.getUrlFile(), currentTask.getTitle(), currentTask.getId(), currentTask.getIdClass(), numCorrect, numQues, timeFinish - timeleftMilis);
        if (reWork) {
            progressUpload.dismiss();
            ShowResultTask showResultTask = new ShowResultTask(TaskActivity.this, resultTask, null);
            showResultTask.ShowResult();
        } else {
            taskRef = FirebaseDatabase.getInstance().getReference("Classroom").child(currentTask.getIdClass()).child("task").child(currentTask.getId());
            taskRef.child("result").child(uidUser).setValue(resultTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                    taskRef.child("complete").setValue(currentTask.getComplete() + 1);
                    Toast.makeText(TaskActivity.this, "Nộp bài thành công!", Toast.LENGTH_SHORT).show();
                    progressUpload.dismiss();
                    ShowResultTask showResultTask = new ShowResultTask(TaskActivity.this, resultTask, currentTask);
                    showResultTask.ShowResult();
                }
            });
        }
    }

    private void SetupDialogReturn() {
        alertNotice = new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn có muốn thoát khi chưa làm xong không? Bạn sẽ được 0 điểm.")
                .setCancelable(true)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int stt = 0; stt < numQues; stt++) {
                            answerList.set(stt, "?");
                        }
                        SubmitTask();
                        dialog.dismiss();
                        alertNotice = null;
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    private void SetCountDownTime() {
        countDownTimer = new CountDownTimer(timeleftMilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeup = false;
                timeleftMilis = millisUntilFinished;
                UpdateCountDownText();
            }

            @Override
            public void onFinish() {
                timeup = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this)
                        .setTitle("Thông báo")
                        .setMessage("Thời gian làm bài đã hết. Nộp bài ngay bây giờ.")
                        .setCancelable(false)
                        .setPositiveButton("Nộp bài", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertNotice = null;
                                SubmitTask();
                            }
                        });
                builder.show();
            }
        }.start();
    }

    private void UpdateCountDownText() {
        int minutes = (int) ((timeleftMilis / 1000) / 60);
        int seconds = (int) ((timeleftMilis / 1000) % 60);
        String timeLeftFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        txtTimeLeft.setText(timeLeftFormat);
    }

    private void LoadFile() {
        btnLoad.setVisibility(View.GONE);
        progressLoad.setVisibility(View.VISIBLE);
        FileLoader.with(TaskActivity.this)
                .load(currentTask.getUrlFile().trim())
                .fromDirectory("PDFFiles", FileLoader.DIR_EXTERNAL_PUBLIC)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        SetCountDownTime();
                        btnLoad.setVisibility(View.GONE);
                        progressLoad.setVisibility(View.GONE);
                        pdfTask.fromFile(response.getBody())
                                .password(null)
                                .defaultPage(0)
                                .enableDoubletap(true)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .onRender(new OnRenderListener() {
                                    @Override
                                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                        pdfTask.fitToWidth();
                                    }
                                })
                                .enableAnnotationRendering(true)
                                .invalidPageColor(Color.WHITE)
                                .load();
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Toast.makeText(TaskActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        btnLoad.setVisibility(View.VISIBLE);
                        progressLoad.setVisibility(View.GONE);
                    }
                });
    }

    private void SetBtnClick(Button btnClick, String answer) {
        answerList.set(pos, answer);
        btnClick.setBackgroundResource(R.drawable.custom_btn_answer_clicked);
        btnClick.setTextColor(Color.WHITE);
        for (int i = 0; i < listBtn.length; i++) {
            if (listBtn[i] != btnClick) {
                listBtn[i].setBackgroundResource(R.drawable.custom_button_answer);
                listBtn[i].setTextColor(Color.BLACK);
            }
        }
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

    @Override
    public void onBackPressed() {
        if (alertNotice != null) {
            alertNotice.show();
        } else {
            super.onBackPressed();
        }
    }
}
