package ru.ptrff.tracktag.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.ptrff.tracktag.databinding.ItemOptionBinding;
import ru.ptrff.tracktag.models.Option;

public class OptionsAdapter extends ListAdapter<Option, OptionsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Option> options;
    private OptionEvents optionEvents;

    public interface OptionEvents {
        void onOptionClick(Option option);
    }

    public OptionsAdapter(Context context, List<Option> options) {
        super(new OptionsDiffCallback());
        this.options = options;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOptionsEvents(OptionEvents optionEvents) {
        this.optionEvents = optionEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOptionBinding binding = ItemOptionBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Option option = options.get(position);
        holder.binding.label.setText(option.getLabel());
        holder.binding.icon.setImageDrawable(
                AppCompatResources.getDrawable(holder.binding.icon.getContext(), option.getIcon())
        );
        holder.binding.getRoot().setOnClickListener(v -> {
            if (optionEvents != null) {
                optionEvents.onOptionClick(option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOptionBinding binding;

        public ViewHolder(@NonNull ItemOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    private static class OptionsDiffCallback extends DiffUtil.ItemCallback<Option> {
        @Override
        public boolean areItemsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
            return oldItem.getIcon() == newItem.getIcon();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
            return oldItem.equals(newItem);
        }
    }
}
