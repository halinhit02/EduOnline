package com.halinhit.eduonline;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAnswerAdapter extends RecyclerView.Adapter<RecyclerAnswerAdapter.ViewHolder> {

    private Context mContext;
    private ResultTask resultTask;

    public RecyclerAnswerAdapter(Context mContext, ResultTask resultTask) {
        this.mContext = mContext;
        this.resultTask = resultTask;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.row_recycler_answer, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String answerTask = resultTask.getAnswerTask().trim().toUpperCase();
        String answerUser = resultTask.getAnswerUser().trim().toUpperCase();
        if (position<9) {
            holder.txtQuestion.setText("0" + (position + 1));
        } else holder.txtQuestion.setText(String.valueOf(position + 1));
        holder.txtAnCorrect.setText(String.valueOf(answerTask.charAt(position)));
        if (String.valueOf(answerUser.charAt(position)).equals("?")) {
            holder.txtAnswerUser.setTextColor(Color.parseColor("#D13131"));
            holder.txtResult.setTextColor(Color.parseColor("#D13131"));
            holder.txtAnswerUser.setText("--");
            holder.txtResult.setText("--  ");
        } else if (answerTask.charAt(position) == answerUser.charAt(position)) {
            holder.txtAnswerUser.setTextColor(Color.parseColor("#0EA5CA"));
            holder.txtResult.setTextColor(Color.parseColor("#0EA5CA"));
            holder.txtAnswerUser.setText(String.valueOf(answerUser.charAt(position)));
            holder.txtResult.setText("Đúng");
        } else {
            holder.txtAnswerUser.setTextColor(Color.parseColor("#D13131"));
            holder.txtResult.setTextColor(Color.parseColor("#D13131"));
            holder.txtAnswerUser.setText(String.valueOf(answerUser.charAt(position)));
            holder.txtResult.setText("Sai  ");
        }
    }

    @Override
    public int getItemCount() {
        return resultTask.getNumQuestion();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtQuestion, txtAnCorrect, txtAnswerUser, txtResult;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtAnCorrect = itemView.findViewById(R.id.txtAnCorrect);
            txtAnswerUser = itemView.findViewById(R.id.txtAnswerUser);
            txtResult = itemView.findViewById(R.id.txtResult);
        }
    }
}
