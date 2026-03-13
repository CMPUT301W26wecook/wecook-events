package com.example.wecookproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for rendering organizer waitlist entrants in a scrollable list. Within the
 * app it acts as the presentation adapter for the entrant-management flow, binding
 * {@link OrganizerWaitlistItem} data objects to the waitlist row layout.
 *
 * Outstanding issues:
 * - The adapter is display-only and does not yet expose row interactions for selection, bulk
 *   actions, or entrant-specific organizer actions.
 */
public class OrganizerWaitlistAdapter extends RecyclerView.Adapter<OrganizerWaitlistAdapter.WaitlistViewHolder> {
    private final List<OrganizerWaitlistItem> items = new ArrayList<>();

    /**
     * Creates a new view holder for an organizer waitlist row.
     *
     * @param parent the parent view group that will host the row
     * @param viewType the adapter view type for the row
     * @return a newly created waitlist view holder
     */
    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organizer_waitlist_entrant, parent, false);
        return new WaitlistViewHolder(view);
    }

    /**
     * Binds a waitlist item to the provided row view holder.
     *
     * @param holder the holder whose views should be populated
     * @param position the adapter position of the item to bind
     * @return no value
     */
    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        OrganizerWaitlistItem item = items.get(position);
        holder.tvAvatar.setText(item.getAvatarLabel());
        holder.tvName.setText(item.getDisplayName());
        holder.tvSubtitle.setText(item.getSubtitle());
    }

    /**
     * Returns the number of waitlist rows currently available to render.
     *
     * @return the total number of waitlist items in the adapter
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Replaces the adapter contents with a new waitlist snapshot.
     *
     * @param newItems the items that should replace the current adapter contents
     * @return no value
     */
    public void submitList(List<OrganizerWaitlistItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * View holder for a single organizer waitlist row.
     */
    static class WaitlistViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAvatar;
        private final TextView tvName;
        private final TextView tvSubtitle;

        /**
         * Creates a view holder backed by the provided item view.
         *
         * @param itemView the inflated row view used to display entrant details
         */
        WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_entrant_avatar);
            tvName = itemView.findViewById(R.id.tv_entrant_name);
            tvSubtitle = itemView.findViewById(R.id.tv_entrant_subtitle);
        }
    }
}
