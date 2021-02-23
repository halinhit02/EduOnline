package com.halinhit.eduonline;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class Frag_CreateInfor extends Fragment {

    private LinearLayout layoutInformation;
    private RelativeLayout layoutFile;
    private Button btnGetFile, btnViewFile, btnNext;
    private TextView txtNameFile, txtFileDialog;
    private PDFView pdfView;
    private TextInputLayout lyEdtImAnswer;
    private TextInputEditText edtTitle, edtTimeDone, edtNumQues, edtImAnswer;
    private EditText edtTimeFinish, edtTimeStart;
    private Switch swShowAnswer, swImportAnswer;

    private int REQUEST_STORAGE_CODE = 123;
    private int REQUEST_GETFILE_CODE = 456;
    private Intent intentGetFile;
    private Classroom inforClass;
    private Task newTask;
    private DatabaseReference taskDataRef;
    private String idTask, title, timedone, datefinish, uidCreator, idClass, nameFile, answerTask, urlFile;
    private int numQues, numComplete, yearCurrent, dayCurrent, monthCurrent;
    private Uri uriFile;
    private View view;
    private Frag_CreateAnswer frag_createAnswer;
    private Bundle bundle;
    private String showAnswer, dateStart;
    private Task currentTask;
    private DatePickerDialog datePickerDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_createinfor, container, false);
        init();
        inforClass = (Classroom) getArguments().getSerializable("informationClass");
        uidCreator = inforClass.getUidCreator();
        idClass = inforClass.getId();
        if (getArguments().getSerializable("editTask") != null) {
            currentTask = (Task) getArguments().getSerializable("editTask");
            btnGetFile.setVisibility(View.GONE);
            layoutInformation.setVisibility(View.VISIBLE);
            layoutFile.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            edtTimeDone.setEnabled(false);
            edtNumQues.setEnabled(false);
            edtTitle.setText(currentTask.getTitle());
            edtTimeDone.setText(currentTask.getTimeDone());
            edtTimeFinish.setText(currentTask.getDateFinish());
            edtNumQues.setText(String.valueOf(currentTask.getNumQuestion()));
            if (currentTask.getTitle().length() > 20) {
                txtNameFile.setText(currentTask.getTitle().substring(0, 20) + "...");
            } else txtNameFile.setText(currentTask.getTitle());
            swShowAnswer.setChecked(Boolean.parseBoolean(currentTask.getShowAnswer()));
            idTask = currentTask.getId();
            answerTask = currentTask.getAnswer();
            numComplete = currentTask.getComplete();
            numQues = currentTask.getNumQuestion();
            urlFile = currentTask.getUrlFile();
            dateStart = currentTask.getDate();
            showAnswer = currentTask.getShowAnswer();
            edtTimeStart.setText(dateStart);
            edtImAnswer.setText(answerTask);
            lyEdtImAnswer.setCounterMaxLength(numQues);
        } else {
            numComplete = 0;
            answerTask = "";
            showAnswer = "false";
            dateStart = "";
            taskDataRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(inforClass.getId()).child("task");
            idTask = taskDataRef.push().getKey();
        }
        Calendar calendar = Calendar.getInstance();
        yearCurrent = calendar.get(Calendar.YEAR);
        monthCurrent = calendar.get(Calendar.MONTH);
        dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);
        btnGetFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_CODE);
                } else {
                    GetFile();
                }
            }
        });
        swImportAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lyEdtImAnswer.setVisibility(View.VISIBLE);
                } else {
                    lyEdtImAnswer.setVisibility(View.GONE);
                }
            }
        });
        edtNumQues.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    numQues = Integer.decode(s.toString().trim());
                    lyEdtImAnswer.setCounterMaxLength(numQues);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        swShowAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showAnswer = "true";
                } else {
                    showAnswer = "false";
                }
            }
        });
        btnViewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTask != null) {
                    ShowFileTask showFileTask = new ShowFileTask(getContext(), currentTask.getTitle(), currentTask.getUrlFile());
                    showFileTask.showDialog();
                } else {
                    DialogViewFile();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTitle.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Nhập tên bài tập!", Toast.LENGTH_SHORT).show();
                } else if (edtTimeDone.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Nhập thời gian làm bài!", Toast.LENGTH_SHORT).show();
                } else if (edtTimeStart.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Nhập thời gian bắt đầu!", Toast.LENGTH_SHORT).show();
                } else if (edtNumQues.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Nhập số câu hỏi!", Toast.LENGTH_SHORT).show();
                } else if (swImportAnswer.isChecked() & (edtImAnswer.getText().toString().length() != numQues)) {
                    if (edtImAnswer.getText().toString().length() > numQues) {
                        Toast.makeText(getContext(), "Nhập thừa đáp án!", Toast.LENGTH_SHORT).show();
                    } else if (edtImAnswer.getText().toString().length() < numQues) {
                        Toast.makeText(getContext(), "Nhập thiếu đáp án!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    bundle = new Bundle();
                    title = edtTitle.getText().toString();
                    timedone = edtTimeDone.getText().toString();
                    dateStart = edtTimeStart.getText().toString();
                    datefinish = edtTimeFinish.getText().toString();
                    numQues = Integer.decode(edtNumQues.getText().toString().trim());
                    answerTask = edtImAnswer.getText().toString().trim().toUpperCase();
                    newTask = new Task(idTask, title, dateStart, urlFile, timedone, datefinish, numComplete, numQues, answerTask, showAnswer, idClass, uidCreator);
                    frag_createAnswer = new Frag_CreateAnswer();
                    bundle.putSerializable("newTask", newTask);
                    frag_createAnswer.setArguments(bundle);
                    SetFragment(frag_createAnswer);
                }
            }
        });
        edtTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edtTimeStart.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, yearCurrent, monthCurrent, dayCurrent);
                datePickerDialog.show();
            }
        });
        edtTimeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edtTimeFinish.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, yearCurrent, monthCurrent, dayCurrent + 1);
                datePickerDialog.show();
            }
        });
        return view;
    }

    private void init() {
        btnGetFile = view.findViewById(R.id.btnGetFile);
        txtNameFile = view.findViewById(R.id.txtNameFile);
        btnViewFile = view.findViewById(R.id.btnView);
        layoutFile = view.findViewById(R.id.layoutFile);
        layoutInformation = view.findViewById(R.id.layoutInformation);
        edtTitle = view.findViewById(R.id.edtNameTask);
        edtTimeDone = view.findViewById(R.id.edtTimeDone);
        edtTimeFinish = view.findViewById(R.id.edtTimeFinish);
        edtNumQues = view.findViewById(R.id.edtNumQues);
        btnNext = view.findViewById(R.id.btnNext);
        swShowAnswer = view.findViewById(R.id.sw_showAnswer);
        swImportAnswer = view.findViewById(R.id.sw_importAnswer);
        edtImAnswer = view.findViewById(R.id.edtImAnswer);
        lyEdtImAnswer = view.findViewById(R.id.lyEdtImAnswer);
        edtTimeStart = view.findViewById(R.id.edtTimeStart);
    }

    private void DialogViewFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View viewCustom = inflater.inflate(R.layout.dialog_viewfile, null);
        builder.setView(viewCustom);
        builder.setCancelable(true);
        pdfView = viewCustom.findViewById(R.id.pdfViewer);
        txtFileDialog = viewCustom.findViewById(R.id.txtNameFile);
        txtFileDialog.setText(nameFile);
        pdfView.fromUri(uriFile)
                .password(null)
                .defaultPage(0)
                .enableDoubletap(true)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth();
                    }
                })
                .enableAnnotationRendering(true)
                .invalidPageColor(Color.WHITE)
                .load();
        builder.show();
    }

    private void GetFile() {
        intentGetFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentGetFile.setType("application/pdf");
        intentGetFile.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intentGetFile, "Chọn tệp bằng"), REQUEST_GETFILE_CODE);
    }

    private void SetFragment(Fragment frag) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frameCreate, frag);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GETFILE_CODE & resultCode == RESULT_OK & data != null) {
            uriFile = data.getData();
            urlFile = uriFile.toString();
            layoutInformation.setVisibility(View.VISIBLE);
            layoutFile.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            if (uriFile.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getContext().getContentResolver().query(uriFile, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        nameFile = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriFile.toString().startsWith("file://")) {
                nameFile = new File(uriFile.toString()).getName();
            }
            txtNameFile.setText(nameFile);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_CODE & grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetFile();
            } else {
                Toast.makeText(getContext(), "Cần cấp quyền truy cập bộ nhớ!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
