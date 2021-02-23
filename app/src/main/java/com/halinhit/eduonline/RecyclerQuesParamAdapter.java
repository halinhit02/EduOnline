package com.halinhit.eduonline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class RecyclerQuesParamAdapter extends RecyclerView.Adapter<RecyclerQuesParamAdapter.ViewHolder> {

    private Context mContext;
    private List<QuesParam> answerList;

    public RecyclerQuesParamAdapter(Context mContext, List<QuesParam> answerList) {
        this.mContext = mContext;
        this.answerList = answerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.row_recycler_quesparam, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.seekCorrect.setEnabled(false);
        holder.seekIncorrect.setEnabled(false);
        holder.seekCorrect.getThumb().mutate().setAlpha(0);
        holder.seekIncorrect.getThumb().mutate().setAlpha(0);
        if (answerList.get(position).getSerial() <= 9) {
            holder.txtQuestion.setText(mContext.getString(R.string.quesOnedigit, answerList.get(position).getSerial()));
        } else holder.txtQuestion.setText(mContext.getString(R.string.quesTwodigit, answerList.get(position).getSerial()));
        holder.txtNumCorrect.setText(String.valueOf(answerList.get(position).getNumCorrect()));
        holder.txtNumIncorrect.setText(String.valueOf(answerList.get(position).getNumIncorrect()));
        holder.seekCorrect.setMax(answerList.get(position).getNumCorrect() + answerList.get(position).getNumIncorrect());
        holder.seekIncorrect.setMax(answerList.get(position).getNumIncorrect() + answerList.get(position).getNumCorrect());
        holder.seekCorrect.setProgress(answerList.get(position).getNumCorrect());
        holder.seekIncorrect.setProgress(answerList.get(position).getNumIncorrect());
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtQuestion, txtNumCorrect, txtNumIncorrect;
        private SeekBar seekCorrect, seekIncorrect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtNumCorrect = itemView.findViewById(R.id.txtNumCorrect);
            txtNumIncorrect = itemView.findViewById(R.id.txtNumInCorrect);
            seekCorrect = itemView.findViewById(R.id.seekCorrect);
            seekIncorrect = itemView.findViewById(R.id.seekIncorrect);
        }
    }
}
