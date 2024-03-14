package ru.ptrff.tracktag.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.ItemSubBinding;
import ru.ptrff.tracktag.models.Option;
import ru.ptrff.tracktag.models.User;

public class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.ViewHolder> {

    private List<User> subs;
    private LayoutInflater inflater;

    public SubsAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        this.subs = UserData.getInstance().getSubs();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSubBinding binding = ItemSubBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.posAndName.setText(
                (position + 1) + ". " + subs.get(position).getUsername()
        );

        holder.binding.cancel.setOnClickListener(v -> {
            UserData.getInstance().removeSub(subs.get(position));
            subs.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return subs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSubBinding binding;

        public ViewHolder(@NonNull ItemSubBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
