package com.halinhit.eduonline;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Frag_Signin extends Fragment {

    private RelativeLayout layoutSignin;
    private TextInputEditText inpEdtUser, inpEdtPassword;
    private Button btnSignin;
    private ProgressBar mLoginProgress;

    private View view;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String username, password, uidUser;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private SharedPreferences.Editor editInfor;
    private AlertDialog.Builder loginAlertDialog;
    private Intent intentGotoMain;
    private Frag_Verify frag_verify;
    private Bundle bundleCodeSent;
    private InforUser inforUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_signin, container, false);
        editInfor = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE).edit();
        editInfor.apply();
        init();
        mAuth = FirebaseAuth.getInstance();
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignin.setEnabled(false);
                username = inpEdtUser.getText().toString().trim();
                password = inpEdtPassword.getText().toString().trim();
                if (username.equals("")) {
                    Toast.makeText(getActivity(), "Nhập Email/Số điện thoại", Toast.LENGTH_SHORT).show();
                    btnSignin.setEnabled(true);
                } else if (password.equals("")) {
                    Toast.makeText(getActivity(), "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    btnSignin.setEnabled(true);
                }
                else if (username.endsWith(".com") || username.endsWith("@gmail.com")) {
                    mLoginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                uidUser = task.getResult().getUser().getUid();
                                updateData();
                            }
                            else {
                                btnSignin.setEnabled(true);
                                mLoginProgress.setVisibility(View.INVISIBLE);
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if (isInteger(username)) {
                    if (username.startsWith("0") & username.length() == 10) {
                        username = "+84" + username.substring(1);
                    }
                    mLoginProgress.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("Accounts").child(username).child("Password")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue(String.class) == null) {
                                        loginAlertDialog = new AlertDialog.Builder(getActivity())
                                                .setTitle("Thông báo")
                                                .setMessage("Số điện thoại này chưa đăng kí. Bạn có muốn đăng kí tài khoản bằng số điện thoại này không?")
                                                .setCancelable(false)
                                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        inpEdtUser.setText("");
                                                        inpEdtPassword.setText("");
                                                        mLoginProgress.setVisibility(View.INVISIBLE);
                                                        btnSignin.setEnabled(true);
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        setFragment(new Frag_Signup());
                                                    }
                                                });
                                        loginAlertDialog.show();
                                    }
                                    else if (dataSnapshot.getValue(String.class).equals(password)) {
                                        FirebaseAuth.getInstance().setLanguageCode("vi");
                                        PhoneAuthProvider.getInstance().verifyPhoneNumber(username, 60, TimeUnit.SECONDS, getActivity(), mCallbacks);
                                    }
                                    else {
                                        btnSignin.setEnabled(true);
                                        mLoginProgress.setVisibility(View.INVISIBLE);
                                        inpEdtPassword.setText("");
                                        Toast.makeText(getActivity(), "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getActivity(), "Kết nối bị gián đoạn! Thử lại!", Toast.LENGTH_SHORT).show();
                                    btnSignin.setEnabled(true);
                                    mLoginProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getActivity(), "Lỗi! Yêu cầu xác minh thất bại!", Toast.LENGTH_SHORT).show();
                mLoginProgress.setVisibility(View.INVISIBLE);
                btnSignin.setEnabled(true);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                frag_verify = new Frag_Verify();
                bundleCodeSent = new Bundle();
                bundleCodeSent.putString("AuthCredentials", s);
                bundleCodeSent.putString("PhoneNumber", username);
                bundleCodeSent.putString("Password", password);
                bundleCodeSent.putString("Login", "Signin");
                frag_verify.setArguments(bundleCodeSent);
                setFragment(frag_verify);
            }
        };
        layoutSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        return view;
    }

    private void init() {
        layoutSignin = view.findViewById(R.id.layoutSignin);
        btnSignin = view.findViewById(R.id.btnSignin);
        inpEdtUser = view.findViewById(R.id.inpEdtUser);
        inpEdtPassword = view.findViewById(R.id.inpEdtPassword);
        mLoginProgress = view.findViewById(R.id.loginProgress);
    }
    public static boolean isInteger(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), 10) < 0) return false;
        }
        return true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateData() {
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uidUser);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    inforUser = dataSnapshot.getValue(InforUser.class);
                    editInfor.putString("uid", inforUser.getUid());
                    editInfor.putString("name", inforUser.getName());
                    editInfor.apply();
                    gotoMainActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.errorFirebase), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void gotoMainActivity() {
        intentGotoMain = new Intent(getActivity(), MainActivity.class);
        intentGotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentGotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentGotoMain);
        getActivity().finish();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (getActivity() != null) {
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("halinhit", "signInWithCredential:success");
                                uidUser = task.getResult().getUser().getUid();
                                updateData();
                            } else {
                                Log.w("halinhit", "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            mLoginProgress.setVisibility(View.INVISIBLE);
                            btnSignin.setEnabled(true);
                        }
                    });
        }
    }

    public void setFragment(Fragment frag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tranc = fragmentManager.beginTransaction();
        tranc.replace(R.id.frag_login, frag);
        tranc.commit();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            gotoMainActivity();
        }
    }
}
