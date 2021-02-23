package com.halinhit.eduonline;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Frag_Member extends Fragment {

    private TextView txtNumMem;
    private RecyclerView recyMember;
    private LinearLayoutManager layoutManager;
    private List<InforUser> memberList;
    private RecyclerMemberAdapter memberAdapter;
    private DatabaseReference classroomRef;
    FirebaseUser userCurrent;
    String idClass;
    private InforUser inforUser;
    private Classroom inforClass;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_member, container, false);
        recyMember = view.findViewById(R.id.recyclerMember);
        txtNumMem = view.findViewById(R.id.txtNumMember);
        inforClass = (Classroom) getArguments().getSerializable("informationClass");
        idClass = inforClass.getId();
        userCurrent = FirebaseAuth.getInstance().getCurrentUser();
        memberList = new ArrayList<>();
        memberAdapter = new RecyclerMemberAdapter(getActivity(), memberList, inforClass);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyMember.setAdapter(memberAdapter);
        recyMember.setLayoutManager(layoutManager);
        recyMember.setHasFixedSize(true);
        classroomRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(idClass);
        classroomRef.child("member").orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() & getActivity() != null) {
                    inforUser = new InforUser();
                    inforUser.setName(dataSnapshot.getValue(String.class));
                    inforUser.setUid(dataSnapshot.getKey());
                    memberList.add(inforUser);
                    memberAdapter.notifyDataSetChanged();
                }
                if (getActivity() != null) {
                    txtNumMem.setText(getActivity().getString(R.string.numMember, memberList.size()));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() & getActivity() != null ) {
                    for (int i=0; i<memberList.size();i++) {
                        if (memberList.get(i).getUid().equals(dataSnapshot.getKey())) {
                            memberList.remove(i);
                            memberAdapter.notifyItemRemoved(i);
                            memberAdapter.notifyItemRangeRemoved(i, memberList.size());
                            recyMember.removeViewAt(i);
                            break;
                        }
                    }
                }
                if (getActivity() != null) {
                    txtNumMem.setText(getActivity().getString(R.string.numMember, memberList.size()));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.errorFirebase), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
