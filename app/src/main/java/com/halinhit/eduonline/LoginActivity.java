package com.halinhit.eduonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference rootRef;
    private FirebaseUser user;
    private FragmentManager fragmentManager;
    private String email;
    private Fragment fragLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setFragment(new Frag_Signin());
    }

    public void btnLogin(View view) {
        switch (view.getId()) {
            case R.id.btnsingup:
                setFragment(new Frag_Signup());
                break;
            case R.id.btnsignin:
                setFragment(new Frag_Signin());
                break;
        }
    }

    public void setFragment(Fragment frag) {
        fragmentManager = getFragmentManager();
        FragmentTransaction tranc = fragmentManager.beginTransaction();
        tranc.replace(R.id.frag_login, frag);
        tranc.commit();
    }

    @Override
    public void onBackPressed() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        fragLogin = getFragmentManager().findFragmentById(R.id.frag_login);
        if (fragLogin instanceof Frag_Verify) {
            if (user != null) {
                if (user.getEmail() != null) {
                    email = user.getEmail().substring(0, user.getEmail().indexOf("@gmail.com"));
                    rootRef.child("Accounts").child(email).removeValue();
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Đã hủy tài khoản!", Toast.LENGTH_SHORT).show();
                                setFragment(new Frag_Signin());
                            } else {
                                Toast.makeText(LoginActivity.this, "Lỗi! kiểm tra kết nối!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else setFragment(new Frag_Signup());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection().onReceive(this, null);
    }

    @Override
    protected void onStart() {
        new CheckInternetConnection().onReceive(this, null);
        super.onStart();
    }
}
