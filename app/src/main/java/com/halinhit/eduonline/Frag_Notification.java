package com.halinhit.eduonline;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Frag_Notification extends Fragment {

    private Button btnCreate, btnCreateDialog, btnCamera, btnLibrary;
    private TextView txtNotification;
    private EditText edtContent;
    private ImageView imgPhoto;
    private RecyclerView reNotification;
    private View view;
    private Classroom curentClass;
    private DatabaseReference notiRef;
    private List<Notification> notificationList;
    private String uidUser, idClass, idNotification;
    private LinearLayoutManager layoutManager;
    private RecyclerNotificationAdapter notificationAdapter;
    private Notification notification;
    private AlertDialog alertDialog;
    private Uri uriPhoto = null;
    private String cameraFilePath, urlPhoto = "";
    private ProgressDialog progressUpload;
    private FirebaseRecyclerAdapter adapter;
    private int SELECT_PICTURE = 123;
    private int MY_PERMISSIONS_REQUEST_CAMERA = 456;
    private int TAKE_PHOTO = 789;
    private int PERMISSION_WRITE_STORAGE = 147;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_notification, container, false);
        reNotification = view.findViewById(R.id.recyclerNotice);
        txtNotification = view.findViewById(R.id.txtNotice);
        btnCreate = view.findViewById(R.id.btnCreate);
        progressUpload = new ProgressDialog(getContext());
        progressUpload.setCanceledOnTouchOutside(false);
        progressUpload.setMessage("Đang tạo thông báo...");
        uidUser = getContext().getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid", "");
        curentClass = (Classroom) getArguments().getSerializable("informationClass");
        idClass = curentClass.getId();
        if (uidUser.equals(curentClass.getUidCreator())) {
            btnCreate.setVisibility(View.VISIBLE);
        } else btnCreate.setVisibility(View.GONE);

        notificationList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setReverseLayout(true);
        notificationAdapter = new RecyclerNotificationAdapter(getActivity(), notificationList);
        reNotification.setAdapter(notificationAdapter);
        reNotification.setLayoutManager(layoutManager);
        reNotification.setHasFixedSize(true);
        notiRef = FirebaseDatabase.getInstance().getReference("Classroom").child(idClass).child("notification");
        notiRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    txtNotification.setVisibility(View.GONE);
                    notification = dataSnapshot.getValue(Notification.class);
                    notificationList.add(notification);
                    notificationAdapter.notifyDataSetChanged();
                    reNotification.scrollToPosition(notificationList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() & getActivity() != null) {
                    idNotification = dataSnapshot.child("idNotification").getValue(String.class);
                    for (int i = 0; i < notificationList.size(); i++) {
                        if (notificationList.get(i).getIdNotification().equals(idNotification)) {
                            notificationList.remove(i);
                            notificationAdapter.notifyItemRemoved(i);
                            notificationAdapter.notifyItemRangeChanged(i, notificationList.size());
                            reNotification.scrollToPosition(i);
                            break;
                        }
                    }
                    if (getActivity() != null & notificationList.size() == 0) {
                        txtNotification.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflatDialog = LayoutInflater.from(getContext());
                View dialogView = inflatDialog.inflate(R.layout.dialog_create_notification, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                btnCreateDialog = dialogView.findViewById(R.id.btnCreate);
                edtContent = dialogView.findViewById(R.id.edtContent);
                btnCamera = dialogView.findViewById(R.id.btnCamera);
                btnLibrary = dialogView.findViewById(R.id.btnPhoto);
                imgPhoto = dialogView.findViewById(R.id.imgPhoto);
                alertDialog = builder.show();
                btnLibrary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photofromLibrary();
                    }
                });
                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        captureFromCamera();
                    }
                });
                btnCreateDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtContent.getText().toString().equals("")) {
                            Toast.makeText(getContext(), "Nhập nội dung!", Toast.LENGTH_SHORT).show();
                        } else {
                            progressUpload.show();
                            idNotification = notiRef.push().getKey();
                            if (uriPhoto != null) {
                                FirebaseStorage.getInstance().getReference(idClass).child("notification").child(idNotification).putFile(uriPhoto).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(final Uri uri) {
                                                    String urlPhoto = uri.toString();
                                                    notification = new Notification(idNotification, curentClass.getNameCreator(), uidUser, edtContent.getText().toString(), urlPhoto, getDate(), curentClass.getId());
                                                    notiRef.child(idNotification).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressUpload.dismiss();
                                                                uriPhoto = null;
                                                                Toast.makeText(getContext(), "Tạo thông báo thành công!", Toast.LENGTH_SHORT).show();
                                                                alertDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            progressUpload.dismiss();
                                            Toast.makeText(getContext(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                notification = new Notification(idNotification, curentClass.getNameCreator(), uidUser, edtContent.getText().toString(), urlPhoto, getDate(), curentClass.getId());
                                notiRef.child(idNotification).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressUpload.dismiss();
                                            Toast.makeText(getContext(), "Tạo thông báo thành công!", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        } else {
                                            progressUpload.dismiss();
                                            Toast.makeText(getContext(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE & resultCode == RESULT_OK & data != null) {
            Glide.with(this).load(data.getData()).fitCenter().into(imgPhoto);
            uriPhoto = data.getData();
        }
        if (requestCode == TAKE_PHOTO & resultCode == RESULT_OK) {
            Glide.with(this).load(Uri.parse(cameraFilePath)).into(imgPhoto);
            uriPhoto = Uri.parse(cameraFilePath);
        }
    }

    private void captureFromCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
        } else {
            try {
                Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
                startActivityForResult(intentCapture, TAKE_PHOTO);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void photofromLibrary() {
        Intent intentPhotoLibrary = new Intent();
        intentPhotoLibrary.setAction(Intent.ACTION_GET_CONTENT);
        intentPhotoLibrary.setType("image/*");
        startActivityForResult(intentPhotoLibrary, SELECT_PICTURE);
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    private java.io.File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        java.io.File storageDir = new java.io.File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        java.io.File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA & grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
                }
            } else {
                Toast.makeText(getContext(), "Cần cấp quyền chụp ảnh!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSION_WRITE_STORAGE & grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureFromCamera();
            } else {
                Toast.makeText(getContext(), "Cần cấp quyền truy cập bộ nhớ!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("Classroom").child(idClass).child("notification");
        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>()
                        .setQuery(query, new SnapshotParser<Notification>() {
                            @NonNull
                            @Override
                            public Notification parseSnapshot(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    return snapshot.getValue(Notification.class);
                                } else return new Notification();
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Notification, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext())
                        .inflate(R.layout.row_recycler_notification, parent, false);

                return new ViewHolder(view);
            }


            @Override
            public void onBindViewHolder(ViewHolder holder, final int position, final Notification notification) {
                if (notification != null) {
                    holder.SetDetails(getContext(), notification);
                }
                holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                .setTitle("Thông báo")
                                .setMessage("Bạn có muốn xóa thông báo này không?")
                                .setCancelable(true)
                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!notification.getUrlPhoto().equals("")) {
                                            FirebaseStorage.getInstance().getReference().child(notification.getIdClass())
                                                    .child("notification").child(notification.getIdNotification()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseDatabase.getInstance().getReference("Classroom").child(notification.getIdClass())
                                                                .child("notification").child(notification.getIdNotification()).removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            alertDialog.dismiss();
                                                                            Toast.makeText(getContext(), "Đã xóa thông báo!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(getContext(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            FirebaseDatabase.getInstance().getReference("Classroom").child(notification.getIdClass())
                                                    .child("notification").child(notification.getIdNotification()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                alertDialog.dismiss();
                                                                Toast.makeText(getContext(), "Đã xóa thông báo!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getContext(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });
                        alertDialog = builder.show();
                    }
                });
            }

        };
        reNotification.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/
}
