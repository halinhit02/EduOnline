package com.halinhit.eduonline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private ImageButton imgBtnBack, imgBtnFb, imgBtnInsta, imgBtnGmail;
    private Intent gmailSend, intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnFb = findViewById(R.id.fbbtn);
        imgBtnInsta = findViewById(R.id.instabtn);
        imgBtnGmail = findViewById(R.id.gmailbtn);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        imgBtnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setData(Uri.parse(getString(R.string.fb)));
                startActivity(intent);
            }
        });

        imgBtnInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setData(Uri.parse(getString(R.string.insta)));
                startActivity(intent);
            }
        });

        imgBtnGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email[] = {getString(R.string.gmail)};
                gmailSend = new Intent();
                gmailSend.setAction(Intent.ACTION_SEND);
                gmailSend.putExtra(Intent.EXTRA_EMAIL, email);
                gmailSend.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Subject));
                gmailSend.setType("message/rfc822");
                startActivity(Intent.createChooser(gmailSend, "Chọn Một Ứng Dụng:"));
            }
        });
    }
}
