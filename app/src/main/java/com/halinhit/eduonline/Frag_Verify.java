package com.halinhit.eduonline;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Frag_Verify extends Fragment {

    private TextView txtVerify;
    private EditText edtNumVerify;
    private Button btnConfirm, btnRecode;
    private ProgressBar mProgress;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private View view;
    private String mAuthCredentials, name, uidUser, title, phoneNumber, password, typeLogin;
    private SharedPreferences.Editor editInfor;
    private DatabaseReference accountsRef, userRef;
    private Bundle bundle;
    private Intent intentGotoMain;
    private InforUser inforUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_verify, container, false);
        init();
        editInfor = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE).edit();
        editInfor.apply();
        accountsRef = FirebaseDatabase.getInstance().getReference().child("Accounts");
        bundle = getArguments();
        mAuthCredentials = bundle.getString("AuthCredentials");
        password = bundle.getString("Password");
        typeLogin = bundle.getString("Login");
        phoneNumber = bundle.getString("PhoneNumber");
        name = bundle.getString("Name");
        title = getString(R.string.txtVerifyPhone, phoneNumber);
        btnConfirm.setText("Xác nhận");
        txtVerify.setText(title);
        btnRecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().setLanguageCode("vi");
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, getActivity(), mCallbacks);
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
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getActivity(), "Mã kích hoạt đã được gửi!", Toast.LENGTH_SHORT).show();
                mAuthCredentials = s;
            }
        };
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setEnabled(false);
                if (edtNumVerify.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập mã xác nhận!", Toast.LENGTH_SHORT).show();
                    btnConfirm.setEnabled(true);
                } else {
                    mProgress.setVisibility(View.VISIBLE);
                    btnConfirm.setEnabled(false);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthCredentials, edtNumVerify.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        return view;
    }

    private void init() {
        txtVerify = view.findViewById(R.id.txt_Verify);
        edtNumVerify = view.findViewById(R.id.edtNumVerify);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        mProgress = view.findViewById(R.id.verifyProgress);
        btnRecode = view.findViewById(R.id.btnReCode);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uidUser = task.getResult().getUser().getUid();
                            if (typeLogin.equals("Signup")) {
                                accountsRef.child(phoneNumber).child("Password").setValue(password)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    SetDataUser();
                                                } else {
                                                    btnConfirm.setEnabled(true);
                                                    mProgress.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getActivity(), "Kết nối bị gián đoạn! Thử lại!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            if (typeLogin.equals("Signin")) {
                                updateData();
                            }
                        } else {
                            Log.d("halinhit", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mProgress.setVisibility(View.INVISIBLE);
                                btnConfirm.setEnabled(true);
                            }
                        }
                    }
                });
    }

    private void updateData() {
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uidUser);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
            }
        });
    }

    private void SetDataUser() {
        inforUser = new InforUser(uidUser, name);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uidUser);
        userRef.setValue(inforUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    editInfor.putString("uid", uidUser);
                    editInfor.putString("name", name);
                    editInfor.apply();
                    gotoMainActivity();
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                }
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

    public void setFragment(Fragment frag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tranc = fragmentManager.beginTransaction();
        tranc.replace(R.id.frag_login, frag);
        tranc.commit();
    }
}
