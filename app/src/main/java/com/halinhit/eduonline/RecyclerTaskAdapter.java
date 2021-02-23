package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecyclerTaskAdapter extends RecyclerView.Adapter<RecyclerTaskAdapter.ViewHolder> {

    private Context mContext;
    private List<Task> taskList;

    private RecyclerViewClickListener listener;
    private String uidUser, uidCreator, idClass, idTask, dateFinish, dateStart;
    private ResultTask resultTask;
    private SharedPreferences userPref;
    private DatabaseReference taskRef;
    private Calendar calCurrentTime;
    private int monthCurent, dayCurrent, yearCurrent;
    private String[] dayfinish, dayStart;
    private Classroom classCurrent;

    public RecyclerTaskAdapter(Context mContext, List<Task> taskList) {
        this.mContext = mContext;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.row_recycler_task, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtTitle.setText(taskList.get(position).getTitle());
        holder.txtTimeDone.setText(mContext.getString(R.string.timedone, taskList.get(position).getTimeDone()));
        userPref = mContext.getSharedPreferences("User", Context.MODE_PRIVATE);
        uidUser = userPref.getString("uid", "");
        uidCreator = taskList.get(position).getUidCreator();
        idClass = taskList.get(position).getIdClass();
        idTask = taskList.get(position).getId();
        taskRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(idClass).child("task").child(idTask);
        FirebaseDatabase.getInstance().getReference("Classroom").child(idClass).child("numMember").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() & position <= taskList.size() & mContext != null) {
                    holder.txtNumber.setText(mContext.getString(R.string.doneall, taskList.get(position).getComplete(), dataSnapshot.getValue(int.class)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        taskRef.child("result").child(uidUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resultTask = dataSnapshot.getValue(ResultTask.class);
                    holder.txtScore.setVisibility(View.VISIBLE);
                    holder.txtNote.setVisibility(View.GONE);
                    holder.txtScore.setText(resultTask.getScore());
                } else {
                    if (uidUser.equals(uidCreator)) {
                        holder.itemView.setClickable(true);
                        holder.txtNote.setVisibility(View.GONE);
                    } else holder.txtNote.setVisibility(View.VISIBLE);
                    holder.txtScore.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (uidCreator.equals(uidUser)) {
            holder.imgBtnEdit.setVisibility(View.VISIBLE);
        } else holder.imgBtnEdit.setVisibility(View.GONE);
        holder.imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inCreateTask = new Intent(mContext, CreateTaskActivity.class);
                inCreateTask.putExtra("informationClass", classCurrent);
                inCreateTask.putExtra("editTask", taskList.get(position));
                mContext.startActivity(inCreateTask);
            }
        });
        calCurrentTime = Calendar.getInstance();
        yearCurrent = calCurrentTime.get(Calendar.YEAR);
        monthCurent = calCurrentTime.get(Calendar.MONTH) + 1;
        dayCurrent = calCurrentTime.get(Calendar.DAY_OF_MONTH);
        holder.txtDateCreate.setText(mContext.getString(R.string.dateStart, taskList.get(position).getDate()));
        dateStart = taskList.get(position).getDate();
        dayStart = dateStart.split("/");
        if ((Integer.decode(dayStart[0]) <= dayCurrent) & (Integer.decode(dayStart[1]) == monthCurent) & (Integer.decode(dayStart[2]) == yearCurrent) || uidUser.equals(uidCreator)
                || ((Integer.decode(dayStart[1]) < monthCurent) & (Integer.decode(dayStart[2]) == yearCurrent)) || (Integer.decode(dayStart[2]) < yearCurrent)) {
            holder.itemView.setClickable(true);
            holder.txtNote.setText("");
        } else {
            holder.itemView.setClickable(false);
            holder.txtNote.setTextColor(Color.parseColor("#BB440B"));
            holder.txtNote.setText("Chưa mở");
        }

        dateFinish = taskList.get(position).getDateFinish();
        holder.itemView.setTag("unexpired");
        if (!dateFinish.equals("")) {
            holder.txtDateFinish.setText(mContext.getString(R.string.datefinish, dateFinish));
            dayfinish = dateFinish.split("/");
            holder.itemView.setTag("unexpired");
            if (holder.itemView.isClickable()) {
                if (((Integer.decode(dayfinish[0]) < dayCurrent) & (Integer.decode(dayfinish[1]) == monthCurent) & (Integer.decode(dayfinish[2]) == yearCurrent))
                        || ((Integer.decode(dayfinish[1]) < monthCurent) & (Integer.decode(dayfinish[2]) == yearCurrent)) || (Integer.decode(dayfinish[2]) < yearCurrent)) {
                    holder.txtNote.setTextColor(Color.parseColor("#AA1307"));
                    holder.txtNote.setText("Đã hết hạn");
                    holder.itemView.setTag("expired");
                } else if ((Integer.decode(dayfinish[0]) == dayCurrent) & (Integer.decode(dayfinish[1]) == monthCurent) & (Integer.decode(dayfinish[2]) == yearCurrent)) {
                    holder.txtNote.setTextColor(Color.parseColor("#CA470A"));
                    holder.txtNote.setText("Sắp hết hạn");
                    holder.itemView.setTag("unexpired");
                }
            }
        } else holder.txtDateFinish.setText(mContext.getString(R.string.datefinish, "không có"));
    }

    public void setClassCurrent(Classroom classCurrent) {
        this.classCurrent = classCurrent;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void RecyclerViewClickListener(RecyclerViewClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtDateCreate, txtDateFinish, txtTimeDone, txtNumber, txtScore, txtNote;
        private ImageButton imgBtnEdit;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDateCreate = itemView.findViewById(R.id.txtDateCreate);
            txtDateFinish = itemView.findViewById(R.id.txtDateFinish);
            txtTimeDone = itemView.findViewById(R.id.txtTimeDone);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtNote = itemView.findViewById(R.id.txtNote);
            imgBtnEdit = itemView.findViewById(R.id.imgBtnEdit);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onClick(itemView, position);
                    }
                }
            });
        }
    }
}
