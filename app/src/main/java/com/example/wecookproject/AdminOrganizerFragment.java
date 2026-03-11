package com.example.wecookproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminOrganizerFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListElementAdapter adapter;
    private List<User> organizerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_organizer_list, container, false);

        recyclerView = view.findViewById(R.id.rv_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        organizerList = new ArrayList<>();
        organizerList.add(new User("Organizer 1"));
        organizerList.add(new User("Organizer 2"));
        organizerList.add(new User("Organizer 3"));

        adapter = new ListElementAdapter(organizerList);
        recyclerView.setAdapter(adapter);

        // Delete all checked items
        view.findViewById(R.id.btn_delete_selected).setOnClickListener(v -> {
            List<Boolean> selected = adapter.getSelectedList();
            for (int i = organizerList.size() - 1; i >= 0; i--) {
                if (selected.get(i)) {
                    organizerList.remove(i);
                    selected.remove(i);
                }
            }
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}
