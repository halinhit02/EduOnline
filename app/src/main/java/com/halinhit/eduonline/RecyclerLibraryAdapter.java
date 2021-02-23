package com.halinhit.eduonline;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerLibraryAdapter extends RecyclerView.Adapter<RecyclerLibraryAdapter.ViewHolder> {

    private Context mContext;
    private List<File> fileList;

    public RecyclerLibraryAdapter(Context mContext, List<File> fileList) {
        this.mContext = mContext;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.row_recycler_library, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtTitle.setText(fileList.get(position).getTitle());
        holder.imgBtnDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDownload = new Intent(Intent.ACTION_VIEW);
                intentDownload.setData(Uri.parse(fileList.get(position).getUrlFile()));
                mContext.startActivity(intentDownload);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFileTask showFileTask = new ShowFileTask(mContext, fileList.get(position).getTitle(), fileList.get(position).getUrlFile());
                showFileTask.showDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private ImageButton imgBtnDownLoad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgBtnDownLoad = itemView.findViewById(R.id.imgBtnDownLoad);
        }
    }
}
