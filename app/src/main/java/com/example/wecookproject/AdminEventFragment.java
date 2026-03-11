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

public class AdminEventFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListElementAdapter adapter;
    private List<User> eventList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_event_list, container, false);

        recyclerView = view.findViewById(R.id.rv_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        eventList = new ArrayList<>();
        eventList.add(new User("Event 1"));
        eventList.add(new User("Event 2"));
        eventList.add(new User("Event 3"));

        adapter = new ListElementAdapter(eventList);
        recyclerView.setAdapter(adapter);

        // Delete all checked items
        view.findViewById(R.id.btn_delete_selected).setOnClickListener(v -> {
            List<Boolean> selected = adapter.getSelectedList();
            for (int i = eventList.size() - 1; i >= 0; i--) {
                if (selected.get(i)) {
                    eventList.remove(i);
                    selected.remove(i);
                }
            }
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}
