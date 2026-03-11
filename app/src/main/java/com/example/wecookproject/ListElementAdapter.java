package com.example.wecookproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListElementAdapter extends RecyclerView.Adapter<ListElementAdapter.ListElementViewHolder> {

    public interface OnMenuActionListener {
        void onShowDetail(User user);
        void onDelete(User user, int position);
    }

    private final List<User> userList;
    private final List<Boolean> selectedList;
    private OnMenuActionListener menuActionListener;

    public ListElementAdapter(List<User> userList) {
        this.userList = userList;
        this.selectedList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            selectedList.add(false);
        }
    }

    public void setOnMenuActionListener(OnMenuActionListener listener) {
        this.menuActionListener = listener;
    }

    @NonNull
    @Override
    public ListElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_element, parent, false);
        return new ListElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListElementViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvElementName.setText(user.getName());

        holder.cbSelectElement.setOnCheckedChangeListener(null);
        holder.cbSelectElement.setChecked(selectedList.get(holder.getBindingAdapterPosition()));

        holder.cbSelectElement.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                selectedList.set(currentPos, isChecked);
            }
        });

        holder.btnElementMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Show Detail");
            popup.getMenu().add("Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (menuActionListener != null) {
                    if (item.getTitle().equals("Show Detail")) {
                        menuActionListener.onShowDetail(user);
                    } else if (item.getTitle().equals("Delete")) {
                        menuActionListener.onDelete(user, holder.getBindingAdapterPosition());
                    }
                }
                return true;
            });
            popup.show();
        });

        // Remove the whole item click listener as per requirement
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public List<Boolean> getSelectedList() {
        return selectedList;
    }

    public static class ListElementViewHolder extends RecyclerView.ViewHolder {
        TextView tvElementName;
        CheckBox cbSelectElement;
        ImageButton btnElementMenu;

        public ListElementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvElementName = itemView.findViewById(R.id.tv_element_name);
            cbSelectElement = itemView.findViewById(R.id.cb_select_element);
            btnElementMenu = itemView.findViewById(R.id.btn_element_menu);
        }
    }
}
