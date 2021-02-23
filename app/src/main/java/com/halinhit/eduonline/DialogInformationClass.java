package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class DialogInformationClass {

    private Context mContext;
    private Classroom classroom;

    private TextView txtNameClas, txtCodeClass, txtCodeSecure, txtCreator, txtDateCreate, txtNumMember;
    private Button btnDelete;
    private AlertDialog alertDialog;
    private SharedPreferences userPref;
    private DatabaseReference classRef;
    private String uidUser;

    public DialogInformationClass(Context mContext, Classroom classroom) {
        this.mContext = mContext;
        this.classroom = classroom;
    }

    public void ShowDialog() {
        userPref = mContext.getSharedPreferences("User", Context.MODE_PRIVATE);
        uidUser = userPref.getString("uid", "");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View customView = inflater.inflate(R.layout.dialog_showinforclass, null);
        builder.setView(customView);
        builder.setCancelable(true);
        txtNameClas = customView.findViewById(R.id.txtNameClass);
        txtCodeClass = customView.findViewById(R.id.txtCodeClass);
        txtCodeSecure = customView.findViewById(R.id.txtCodeSecure);
        txtCreator = customView.findViewById(R.id.txtNameCreator);
        txtDateCreate = customView.findViewById(R.id.txtDateCreate);
        txtNumMember = customView.findViewById(R.id.txtNumMember);
        btnDelete = customView.findViewById(R.id.btnDeleteClass);
        if (classroom.getUidCreator().equals(uidUser)) {
            btnDelete.setVisibility(View.VISIBLE);
        } else btnDelete.setVisibility(View.GONE);
        if (classroom.getTitle().length() > 15) {
            txtNameClas.setText(classroom.getTitle().substring(0, 15) + "...");
        } else txtNameClas.setText(classroom.getTitle());

        txtCodeClass.setText(mContext.getString(R.string.code, classroom.getId()));
        if (classroom.getCodeSecure().equals("")) {
            txtCodeSecure.setText(mContext.getString(R.string.codesecure, "không có"));
        } else
            txtCodeSecure.setText(mContext.getString(R.string.codesecure, classroom.getCodeSecure()));
        txtCreator.setText(mContext.getString(R.string.creator, classroom.getNameCreator()));
        txtDateCreate.setText(mContext.getString(R.string.datecreate, classroom.getDateCreate()));
        txtNumMember.setText(mContext.getString(R.string.numMember, classroom.getNumMember() + 1));
        alertDialog = builder.show();
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnection.getConnectivityStatus(mContext) != CheckInternetConnection.TYPE_NOT_CONNECTED) {
                    AlertDialog.Builder dialogDelete = new AlertDialog.Builder(mContext);
                    dialogDelete.setTitle("Thông báo")
                            .setMessage("Bạn có muốn xóa lớp học này không?")
                            .setCancelable(true);
                    dialogDelete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            classRef = FirebaseDatabase.getInstance().getReference("Classroom").child(classroom.getId());
                            classRef.child("task").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            final String idFile = snapshot.getKey();
                                            FirebaseStorage.getInstance().getReference().child(classroom.getId()).child("task/" + idFile + ".pdf").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(mContext, mContext.getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    classRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mContext, "Đã xóa lớp học!", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogDelete.show();
                } else
                    Toast.makeText(mContext, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
