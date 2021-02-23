package com.halinhit.eduonline;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.List;

public class GridQuestionAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> answerList;

    public GridQuestionAdapter(Context mContext, List<String> answerList) {
        this.mContext = mContext;
        this.answerList = answerList;
    }

    @Override
    public int getCount() {
        return answerList.size();
    }

    @Override
    public Object getItem(int position) {
        return answerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String answer;
        TextView txtView;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        txtView = convertView.findViewById(android.R.id.text1);
        txtView.setTypeface(null, Typeface.BOLD);
        if (mContext instanceof TaskActivity) {
            txtView.setTextSize(14);
        } else txtView.setTextSize(16);
        answer = answerList.get(position);
        if (answer.equals("?")) {
            txtView.setText(String.valueOf(position + 1));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#067DCC"));
            txtView.setTextColor(Color.WHITE);
            txtView.setText(position + 1 + ": " + answer);
        }
        return convertView;
    }
}
