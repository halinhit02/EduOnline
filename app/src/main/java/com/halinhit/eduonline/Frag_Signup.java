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
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Frag_Signup extends Fragment {

    private RelativeLayout layoutSignup;
    private Button btnSignup;
    private TextInputEditText inpEdtName, inpEdtUser, inpEdtPassword, inpEdtRePassword;
    private ProgressBar mLoginProgress;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private View view;
    private String name, uidUser, username, email, password, repassword;
    private Bundle bundle;
    private AlertDialog.Builder loginAlertDialog;
    private Intent intentGotoMain;
    private Frag_Verify frag_verify;
    private DatabaseReference rootRef, userRef, accountsRef;
    private InforUser inforUser;
    private SharedPreferences.Editor editInfor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_signup, container, false);
        init();
        editInfor = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE).edit();
        editInfor.apply();
        rootRef = FirebaseDatabase.getInstance().getReference();
        mLoginProgress.setVisibility(View.INVISIBLE);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignup.setEnabled(false);
                name = inpEdtName.getText().toString();
                username = inpEdtUser.getText().toString();
                password = inpEdtPassword.getText().toString();
                repassword = inpEdtRePassword.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(getActivity(), "Nhập họ và tên!", Toast.LENGTH_SHORT).show();
                    btnSignup.setEnabled(true);
                } else if (username.equals("")) {
                    Toast.makeText(getActivity(), "Nhập Email/Số điện thoại!", Toast.LENGTH_SHORT).show();
                    btnSignup.setEnabled(true);
                }
                else if (password.equals("")) {
                    Toast.makeText(getActivity(), "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    btnSignup.setEnabled(true);
                }
                else if (repassword.equals("")) {
                    Toast.makeText(getActivity(), "Nhập lại mật khẩu!", Toast.LENGTH_SHORT).show();
                    btnSignup.setEnabled(true);
                }
                else if (!password.equals(repassword)) {
                    Toast.makeText(getActivity(), "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    btnSignup.setEnabled(true);
                }
                else {
                    mLoginProgress.setVisibility(View.VISIBLE);
                    btnSignup.setEnabled(false);
                    if (username.endsWith(".com") || username.endsWith("@gmail.com")) {
                        email = username.substring(0, username.indexOf("@gmail.com"));
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    rootRef.child("Accounts").child(email).child("Password").setValue(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password);
                                            uidUser = FirebaseAuth.getInstance().getUid();
                                            updateData();
                                        }
                                    });
                                } else {
                                    mLoginProgress.setVisibility(View.INVISIBLE);
                                    btnSignup.setEnabled(true);
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    inpEdtUser.setText("");
                                    inpEdtPassword.setText("");
                                    inpEdtRePassword.setText("");
                                }
                            }
                        });
                    }
                    else if (isInteger(username)) {
                        if (username.startsWith("0") & username.length() == 10) {
                            username = "+84" + username.substring(1);
                        }
                       rootRef.child("Accounts").child(username).child("Password")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(String.class) == null) {
                                            FirebaseAuth.getInstance().setLanguageCode("vi");
                                            PhoneAuthProvider.getInstance().verifyPhoneNumber(username, 60, TimeUnit.SECONDS, getActivity(), mCallbacks);
                                        }
                                        else if (dataSnapshot.getValue(String.class) != null) {
                                            if (getActivity() != null) {
                                                mLoginProgress.setVisibility(View.INVISIBLE);
                                                loginAlertDialog = new AlertDialog.Builder(getActivity())
                                                        .setTitle("Thông báo")
                                                        .setMessage("Số điện thoại này đã có sẵn. Bạn có muốn đăng nhập không?")
                                                        .setCancelable(false)
                                                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                inpEdtUser.setText("");
                                                                inpEdtPassword.setText("");
                                                                inpEdtRePassword.setText("");
                                                                btnSignup.setEnabled(true);
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                username = "";
                                                                password = "";
                                                                setFragment(new Frag_Signin());
                                                            }
                                                        });
                                                loginAlertDialog.show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        btnSignup.setEnabled(true);
                                        mLoginProgress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "Kết nối bị gián đoạn!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
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
                Toast.makeText(getActivity(), "Yêu cầu xác minh thất bại!", Toast.LENGTH_SHORT).show();
                mLoginProgress.setVisibility(View.INVISIBLE);
                btnSignup.setEnabled(true);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                frag_verify = new Frag_Verify();
                bundle = new Bundle();
                bundle.putString("AuthCredentials", s);
                bundle.putString("PhoneNumber", username);
                bundle.putString("Password", password);
                bundle.putString("Login", "Signup");
                bundle.putString("Name", name);
                frag_verify.setArguments(bundle);
                setFragment(frag_verify);
            }
        };
        layoutSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        return view;
    }

    private void init() {
        layoutSignup = view.findViewById(R.id.layoutSignup);
        inpEdtName = view.findViewById(R.id.inpEdtName);
        btnSignup = view.findViewById(R.id.btnSignup);
        inpEdtUser = view.findViewById(R.id.inpEdtUser);
        inpEdtPassword = view.findViewById(R.id.inpEdtPassword);
        inpEdtRePassword = view.findViewById(R.id.inpEdtRePassword);
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
    private void updateData() {
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
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void setFragment(Fragment frag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tranc = fragmentManager.beginTransaction();
        tranc.replace(R.id.frag_login, frag);
        tranc.commit();
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
                                accountsRef = FirebaseDatabase.getInstance().getReference().child("Accounts");
                                accountsRef.child(username).child("Password").setValue(password);
                                SetDataUser();
                            } else {
                                Log.w("halinhit", "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            mLoginProgress.setVisibility(View.INVISIBLE);
                            btnSignup.setEnabled(true);
                        }
                    });
        }
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
}
