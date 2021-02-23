package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowDialogImage {

    private Context context;
    private String urlPhoto;

    private ImageButton imgBtnClose;
    private ImageView imgView;
    private AlertDialog.Builder imageAlertDialog;
    private AlertDialog alertDialog;
    private LayoutInflater inflater;
    private View customView;

    public ShowDialogImage(Context context, String urlPhoto) {
        this.context = context;
        this.urlPhoto = urlPhoto;
    }

    public void ShowDiaglog() {
        inflater = LayoutInflater.from(context);
        customView = inflater.inflate(R.layout.dialog_showimage, null);
        imageAlertDialog = new AlertDialog.Builder(context);
        imageAlertDialog.setView(customView);
        imageAlertDialog.setCancelable(true);
        imgBtnClose = customView.findViewById(R.id.imgBtnClose);
        imgView = customView.findViewById(R.id.imgShow);
        Glide.with(context).load(urlPhoto).into(imgView);
        alertDialog = imageAlertDialog.show();
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}

