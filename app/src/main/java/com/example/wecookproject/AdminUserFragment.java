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

public class AdminUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListElementAdapter adapter;
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        recyclerView = view.findViewById(R.id.rv_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        userList = new ArrayList<>();
        userList.add(new User("User 1"));
        userList.add(new User("User 2"));
        userList.add(new User("User 3"));

        adapter = new ListElementAdapter(userList);
        recyclerView.setAdapter(adapter);

        // Set menu action listener for Show User Detail and Delete
        adapter.setOnMenuActionListener(new ListElementAdapter.OnMenuActionListener() {
            @Override
            public void onShowDetail(User user) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AdminUserProfileFragment())
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onDelete(User user, int position) {
                userList.remove(position);
                adapter.getSelectedList().remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        // Delete all checked items
        view.findViewById(R.id.btn_delete_selected).setOnClickListener(v -> {
            List<Boolean> selected = adapter.getSelectedList();
            for (int i = userList.size() - 1; i >= 0; i--) {
                if (selected.get(i)) {
                    userList.remove(i);
                    selected.remove(i);
                }
            }
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}
