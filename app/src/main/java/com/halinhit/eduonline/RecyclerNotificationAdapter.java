package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class RecyclerNotificationAdapter extends RecyclerView.Adapter<RecyclerNotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> notificationList;

    private AlertDialog alertDialog;

    public RecyclerNotificationAdapter(Context mContext, List<Notification> notificationList) {
        this.mContext = mContext;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.row_recycler_notification, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (!notificationList.get(position).getUrlPhoto().equals("")) {
            holder.imgNotification.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(notificationList.get(position).getUrlPhoto()).into(holder.imgNotification);
        } else holder.imgNotification.setVisibility(View.GONE);
        holder.txtNameUser.setText(notificationList.get(position).getNamePoster());
        holder.txtContent.setText(notificationList.get(position).getContent());
        holder.txtDateCreate.setText(notificationList.get(position).getDateCreate());
        String uidUser = mContext.getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid", "");
        if (notificationList.get(position).getUidPoster().equals(uidUser)) {
            holder.imgBtnDelete.setVisibility(View.VISIBLE);
        } else holder.imgBtnDelete.setVisibility(View.GONE);
        holder.imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogImage showDialogImage = new ShowDialogImage(mContext, notificationList.get(position).getUrlPhoto());
                showDialogImage.ShowDiaglog();
            }
        });
        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("Thông báo")
                        .setMessage("Bạn có muốn xóa thông báo này không?")
                        .setCancelable(true)
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!notificationList.get(position).getUrlPhoto().equals("")) {
                                    FirebaseStorage.getInstance().getReference().child(notificationList.get(position).getIdClass())
                                            .child("notification").child(notificationList.get(position).getIdNotification()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("Classroom").child(notificationList.get(position).getIdClass())
                                                        .child("notification").child(notificationList.get(position).getIdNotification()).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    alertDialog.dismiss();
                                                                    Toast.makeText(mContext, "Đã xóa thông báo!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(mContext, mContext.getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    FirebaseDatabase.getInstance().getReference("Classroom").child(notificationList.get(position).getIdClass())
                                            .child("notification").child(notificationList.get(position).getIdNotification()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        alertDialog.dismiss();
                                                        Toast.makeText(mContext, "Đã xóa thông báo!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(mContext, mContext.getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }

                            }
                        });
                alertDialog = builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNameUser, txtContent, txtDateCreate;
        private ImageButton imgBtnDelete;
        private ImageView imgNotification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameUser = itemView.findViewById(R.id.txtNameUser);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDateCreate = itemView.findViewById(R.id.txtDateCreate);
            imgBtnDelete = itemView.findViewById(R.id.imgBtnDelete);
            imgNotification = itemView.findViewById(R.id.imgNotifi);
        }
    }
}
