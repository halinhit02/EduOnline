package com.halinhit.eduonline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerClassAdapter extends RecyclerView.Adapter<RecyclerClassAdapter.ViewHolder>  {

    private Context mContext;
    private List<Classroom> classroomList;

    public RecyclerClassAdapter(Context mContext, List<Classroom> classroomList) {
        this.mContext = mContext;
        this.classroomList = classroomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.row_recycler_class, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtTitle.setText(classroomList.get(position).getTitle());
        holder.txtCode.setText(mContext.getString(R.string.code, classroomList.get(position).getId()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClass = new Intent(mContext, ClassroomActivity.class);
                intentClass.putExtra("InformationClass", classroomList.get(position));
                mContext.startActivity(intentClass);
            }
        });
        holder.imgBtnInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInformationClass dialoginforclass = new DialogInformationClass(mContext, classroomList.get(position));
                dialoginforclass.ShowDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtCode;
        private ImageButton imgBtnInfor;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCode = itemView.findViewById(R.id.txtCode);
            imgBtnInfor = itemView.findViewById(R.id.imgBtnInfor);
            view = itemView;
        }
    }
}
