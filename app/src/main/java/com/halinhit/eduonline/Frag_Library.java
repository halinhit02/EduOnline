package com.halinhit.eduonline;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Frag_Library extends Fragment {

    private TextView txtNotice;
    private RecyclerView recyclerLibrary;
    private View view;
    private List<File> fileList;
    private Classroom currentClass;
    private String idClass, uidUser;
    private DatabaseReference taskRef;
    private RecyclerLibraryAdapter libraryAdapter;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_library, container, false);
        txtNotice = view.findViewById(R.id.txtNotice);
        recyclerLibrary = view.findViewById(R.id.recyclerLibrary);
        currentClass = (Classroom) getArguments().getSerializable("informationClass");
        idClass = currentClass.getId();
        uidUser = getContext().getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid", "");
        fileList = new ArrayList<>();
        libraryAdapter = new RecyclerLibraryAdapter(getContext(), fileList);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerLibrary.setLayoutManager(layoutManager);
        recyclerLibrary.setAdapter(libraryAdapter);
        recyclerLibrary.setHasFixedSize(true);
        taskRef = FirebaseDatabase.getInstance().getReference("Classroom").child(idClass).child("task");
        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                        if (dataSnap.child("result").child(uidUser).child("score").exists() || uidUser.equals(currentClass.getUidCreator())) {
                            fileList.add(dataSnap.getValue(File.class));
                            libraryAdapter.notifyDataSetChanged();
                            txtNotice.setVisibility(View.GONE);
                            recyclerLibrary.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
