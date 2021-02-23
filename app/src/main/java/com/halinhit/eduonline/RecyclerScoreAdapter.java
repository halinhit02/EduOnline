package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class RecyclerScoreAdapter extends RecyclerView.Adapter<RecyclerScoreAdapter.ViewHolder> {

    private Context mContext;
    private List<ResultTask> resultTaskList;
    private Fragment fragCurrent;

    private AlertDialog alertDialog;

    public RecyclerScoreAdapter(Context mContext, List<ResultTask> resultTaskList, Fragment fragCurrent) {
        this.mContext = mContext;
        this.resultTaskList = resultTaskList;
        this.fragCurrent = fragCurrent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.row_recycler_score, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtName.setText(resultTaskList.get(position).getName());
        holder.txtScore.setText(mContext.getString(R.string.score, resultTaskList.get(position).getScore()));
        int minutes = (int) ((resultTaskList.get(position).getTimeDone() / 1000) / 60);
        int seconds = (int) ((resultTaskList.get(position).getTimeDone() / 1000) % 60);
        String timeLeftFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds + 1);
        holder.txtTimeDone.setText(mContext.getString(R.string.time, timeLeftFormat));
        if (fragCurrent instanceof Frag_ScoreBoard) {
            holder.imgBtnReset.setVisibility(View.VISIBLE);
        } else holder.imgBtnReset.setVisibility(View.INVISIBLE);
        final String idClass = resultTaskList.get(position).getIdClass();
        final String idTask = resultTaskList.get(position).getIdTask();
        final String uidUser = resultTaskList.get(position).getUidUser();
        final String nameUser = resultTaskList.get(position).getName();
        holder.imgBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("Thông báo")
                        .setMessage("Bạn có muốn đặt lại bài làm của " + nameUser + " không?")
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
                                FirebaseDatabase.getInstance().getReference("Classroom").child(idClass)
                                        .child("task").child(idTask).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            int complete = dataSnapshot.child("complete").getValue(int.class);
                                            dataSnapshot.getRef().child("complete").setValue(complete - 1);
                                            dataSnapshot.getRef().child("result").child(uidUser).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                alertDialog.dismiss();
                                                                Toast.makeText(mContext, "Đã đặt lại bài làm của " + nameUser, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        });
                alertDialog = builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultTaskList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imgBtnReset;
        private TextView txtName, txtScore, txtTimeDone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtTimeDone = itemView.findViewById(R.id.txtTimeDone);
            imgBtnReset = itemView.findViewById(R.id.imgBtnReset);
        }
    }
}
