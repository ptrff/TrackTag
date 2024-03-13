package ru.ptrff.tracktag.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.ptrff.tracktag.databinding.ItemSubBinding;
import ru.ptrff.tracktag.models.Option;

public class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.ViewHolder> {

    private List<Option> subs;
    private LayoutInflater inflater;
    private ClickListener clickListener;

    public interface ClickListener {
        void onCancelClick(Option option);
    }

    public void setOnOptionClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public SubsAdapter(LayoutInflater inflater, List<Option> subs) {
        this.inflater = inflater;
        this.subs = subs;
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
                (position + 1) + ". " + subs.get(position).getLabel()
        );

        holder.binding.cancel.setOnClickListener(v -> {
            clickListener.onCancelClick(subs.get(position));
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
