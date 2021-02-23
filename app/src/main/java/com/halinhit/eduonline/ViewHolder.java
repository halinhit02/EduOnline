package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView txtNameUser, txtContent, txtDateCreate;
    public ImageButton imgBtnDelete;
    private ImageView imgNotification;
    private View mView;
    private AlertDialog alertDialog;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        txtNameUser = itemView.findViewById(R.id.txtNameUser);
        txtContent = itemView.findViewById(R.id.txtContent);
        txtDateCreate = itemView.findViewById(R.id.txtDateCreate);
        imgBtnDelete = itemView.findViewById(R.id.imgBtnDelete);
        imgNotification = itemView.findViewById(R.id.imgNotifi);
    }

    public void SetDetails(final Context mContext, final Notification notification) {
        String uidUser = mContext.getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid", "");
        txtNameUser.setText(notification.getNamePoster());
        txtContent.setText(notification.getContent());
        txtDateCreate.setText(notification.getDateCreate());
        if (!notification.getUrlPhoto().equals("")) {
            imgNotification.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(notification.getUrlPhoto()).into(imgNotification);
        } else imgNotification.setVisibility(View.GONE);
        if (notification.getUidPoster().equals(uidUser)) {
            imgBtnDelete.setVisibility(View.VISIBLE);
        } else imgBtnDelete.setVisibility(View.GONE);
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogImage showDialogImage = new ShowDialogImage(mContext, notification.getUrlPhoto());
                showDialogImage.ShowDiaglog();
            }
        });
    }
}
