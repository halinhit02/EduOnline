package com.halinhit.eduonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private TextView txtNotice;
    private Button btnCreateClass, btnSearchClass, btnCreate, btnSearch;
    private RecyclerView recyclerClass;
    private TextInputEditText edtTitle, edtCode;
    private EditText edtTxtCode, edtCodeSecure;
    private AlertDialog alertDialog;
    private AlertDialog.Builder dialogCreateClass, dialogSearchClass;

    private DatabaseReference classRef, versionRef;
    private SharedPreferences prefUser, versionSharedRef;
    private Intent intentLogout;
    private List<Classroom> classroomList;
    private RecyclerClassAdapter classAdapter;
    private LinearLayoutManager layoutManager;
    private LayoutInflater inflater;
    private View customView;
    private Classroom classroom, classSearch;
    private int numRandom, numMember;
    private Float versionName;
    private String uidUser, idClass, title, code, name;
    private Version version;
    public static TextView txtStatusConn;
    private Boolean clickCheckUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerClass = findViewById(R.id.recyclerClass);
        btnCreateClass = findViewById(R.id.btnCreateClass);
        btnSearchClass = findViewById(R.id.btnSearchClass);
        txtNotice = findViewById(R.id.txtNotice);

        clickCheckUpdate = false;
        versionSharedRef = getSharedPreferences("Version", MODE_PRIVATE);
        prefUser = getSharedPreferences("User", MODE_PRIVATE);
        name = prefUser.getString("name", "");
        uidUser = FirebaseAuth.getInstance().getUid();
        classRef = FirebaseDatabase.getInstance().getReference().child("Classroom");
        classroomList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerClass.setLayoutManager(layoutManager);
        recyclerClass.setHasFixedSize(true);
        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recyclerClass.removeAllViews();
                classroomList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                        classroom = datasnap.getValue(Classroom.class);
                        if (datasnap.child("member").child(uidUser).getValue() != null) {
                            classroomList.add(classroom);
                            txtNotice.setVisibility(View.GONE);
                        }
                        if (classroomList.size() > 0) {
                            txtNotice.setVisibility(View.GONE);
                        } else txtNotice.setVisibility(View.VISIBLE);
                    }
                    classAdapter = new RecyclerClassAdapter(MainActivity.this, classroomList);
                    recyclerClass.setAdapter(classAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateClassDialog();
            }
        });
        btnSearchClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchClassDialog();
            }
        });
    }

    private void CreateClassDialog() {
        numRandom = (int) (Math.random() * 9999);
        if (numRandom <= 1000) {
            numRandom = numRandom + 1000;
        }
        idClass = String.valueOf(numRandom);
        inflater = LayoutInflater.from(this);
        customView = inflater.inflate(R.layout.dialog_createclass, null);
        btnCreate = customView.findViewById(R.id.btnCreate);
        edtTitle = customView.findViewById(R.id.edtTitle);
        edtCode = customView.findViewById(R.id.edtCode);
        dialogCreateClass = new AlertDialog.Builder(this)
                .setView(customView)
                .setCancelable(true);
        alertDialog = dialogCreateClass.show();
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnection.getConnectivityStatus(MainActivity.this) != CheckInternetConnection.TYPE_NOT_CONNECTED) {
                    title = edtTitle.getText().toString();
                    if (!edtCode.getText().toString().isEmpty()) {
                        code = edtCode.getText().toString();
                    } else code = "";
                    if (title.equals("")) {
                        Toast.makeText(MainActivity.this, "Nhập tiêu đề lớp học!", Toast.LENGTH_SHORT).show();
                    } else {
                        classroom = new Classroom(idClass, title, 0, uidUser, name, code, getDate());
                        classRef.child(idClass).setValue(classroom).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    classRef.child(idClass).child("member").child(uidUser).setValue(name);
                                    Toast.makeText(MainActivity.this, "Tạo lớp học thành công!", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else
                    Toast.makeText(MainActivity.this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowDialogUpdate(final Version lastVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Thông báo")
                .setMessage("Đã Có Bản Cập Nhật Mới. Hãy cập nhật phiên bản mới nhất để trải nghiệm ứng dụng tốt hơn.")
                .setCancelable(true)
                .setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(lastVersion.getUrlDownload()));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Để sau", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        versionSharedRef.edit().putFloat("version", lastVersion.getVersionName()).apply();
                    }
                });
        builder.show();
    }

    private void CheckUpdate() {
        versionName = versionSharedRef.getFloat("version", getVersionName());
        versionRef = FirebaseDatabase.getInstance().getReference().child("Versions");
        versionRef.orderByChild("versionName").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    version = dataSnapshot.getValue(Version.class);
                    if (version.getVersionName() > versionName) {
                        ShowDialogUpdate(version);
                    }
                    if (clickCheckUpdate) {
                        clickCheckUpdate = false;
                        if (version.getVersionName() > getVersionName()) {
                            ShowDialogUpdate(version);
                        } else {
                            Toast.makeText(MainActivity.this, "Không có bản cập nhật mới!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private float getVersionName() {
        String versionName = "1.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Float.valueOf(versionName);
    }

    private void SearchClassDialog() {
        dialogSearchClass = new AlertDialog.Builder(this);
        inflater = LayoutInflater.from(this);
        customView = inflater.inflate(R.layout.dialog_searchclass, null);
        btnSearch = customView.findViewById(R.id.btnSearch);
        edtTxtCode = customView.findViewById(R.id.edtCodeClass);
        edtCodeSecure = customView.findViewById(R.id.edtCodeSecure);
        dialogSearchClass.setCancelable(true);
        dialogSearchClass.setView(customView);
        alertDialog = dialogSearchClass.show();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnection.getConnectivityStatus(MainActivity.this) != CheckInternetConnection.TYPE_NOT_CONNECTED) {
                    if (edtTxtCode.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "Nhập mã lớp!", Toast.LENGTH_SHORT).show();
                    } else {
                        classRef.child(edtTxtCode.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child("member").child(uidUser).exists()) {
                                        Toast.makeText(MainActivity.this, "Bạn đang trong lớp học này!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        classSearch = dataSnapshot.getValue(Classroom.class);
                                        numMember = classSearch.getNumMember();
                                        if (classSearch.getCodeSecure().equals("") || classSearch.getCodeSecure().equals(edtCodeSecure.getText().toString().trim())) {
                                            numMember += 1;
                                            classRef.child(edtTxtCode.getText().toString()).child("numMember").setValue(numMember);
                                            classRef.child(edtTxtCode.getText().toString()).child("member").child(uidUser).setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(MainActivity.this, "Vào lớp thành công!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else if (!edtCodeSecure.getText().toString().equals("")) {
                                            edtCodeSecure.setText("");
                                            alertDialog.show();
                                            Toast.makeText(MainActivity.this, "Mã bảo vệ không đúng!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            alertDialog.show();
                                            Toast.makeText(MainActivity.this, "Nhập mã bảo vệ!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    edtTxtCode.setText("");
                                    edtCodeSecure.setText("");
                                    alertDialog.show();
                                    Toast.makeText(MainActivity.this, "Không tìm thấy lớp học!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MainActivity.this, getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.dismiss();
                    }
                } else
                    Toast.makeText(MainActivity.this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                if (CheckInternetConnection.getConnectivityStatus(this) != CheckInternetConnection.TYPE_NOT_CONNECTED) {
                    prefUser.edit().clear().apply();
                    FirebaseAuth.getInstance().signOut();
                    intentLogout = new Intent(this, LoginActivity.class);
                    intentLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentLogout);
                } else Toast.makeText(this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.checkUpdate:
                if (CheckInternetConnection.getConnectivityStatus(this) != CheckInternetConnection.TYPE_NOT_CONNECTED) {
                    clickCheckUpdate = true;
                    CheckUpdate();
                } else Toast.makeText(this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact:
                startActivity(new Intent(this, ContactActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection().onReceive(this, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new CheckInternetConnection().onReceive(this, null);
        CheckUpdate();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentLogin);
            finish();
        }
    }
}
