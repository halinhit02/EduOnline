package com.halinhit.eduonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerMemberAdapter extends RecyclerView.Adapter<RecyclerMemberAdapter.ViewHoler> {

    private Context mContext;
    private List<InforUser> inforUserList;
    private Classroom classroom;

    public RecyclerMemberAdapter(Context mContext, List<InforUser> inforUserList, Classroom classroom) {
        this.mContext = mContext;
        this.inforUserList = inforUserList;
        this.classroom = classroom;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHoler viewHoler = new ViewHoler(inflater.inflate(R.layout.row_recycler_member, parent, false));
        return viewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, final int position) {
        holder.txtName.setText(inforUserList.get(position).getName());
        final DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("Classroom").child(classroom.getId());
        final String uidMember = inforUserList.get(position).getUid();
        String uidUser  = mContext.getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid", "");
        if (uidUser.equals(classroom.getUidCreator()) & !uidUser.equals(uidMember)) {
            holder.imgBtnRemove.setVisibility(View.VISIBLE);
        } else holder.imgBtnRemove.setVisibility(View.INVISIBLE);
        holder.imgBtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classRef.child("numMember").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            classroom.setNumMember(dataSnapshot.getValue(int.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("Thông báo")
                        .setMessage("Bạn có muốn xóa thành viên " + inforUserList.get(position).getName() + " không?")
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
                                classRef.child("task").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (final DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                                final int numComplte = dataSnap.child("complete").getValue(int.class);
                                                for ( DataSnapshot snapshot : dataSnap.child("result").getChildren()) {
                                                    if (snapshot.getKey().equals(uidMember)) {
                                                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                dataSnap.getRef().child("complete").setValue(numComplte - 1);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                classRef.child("numMember").setValue(classroom.getNumMember() - 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            final String nameMember = inforUserList.get(position).getName();
                                            classRef.child("member").child(inforUserList.get(position).getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(mContext, "Đã xóa thành viên " + nameMember, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(mContext, mContext.getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        });
                builder.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return inforUserList.size();
    }

    class ViewHoler extends RecyclerView.ViewHolder {

        private TextView txtName;
        private ImageButton imgBtnRemove;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            imgBtnRemove = itemView.findViewById(R.id.imgBtnRemoveMem);
        }
    }
}
