package ru.ptrff.tracktag.adapters;

import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.ptrff.tracktag.databinding.ItemOptionListBinding;
import ru.ptrff.tracktag.databinding.ItemTagBinding;
import ru.ptrff.tracktag.models.Option;

public class OptionsListAdapter extends RecyclerView.Adapter<OptionsListAdapter.ViewHolder> {

    private List<Option> options;
    private LayoutInflater inflater;
    private ClickListener clickListener;

    public interface ClickListener{
        void onOptionClick(Option option);
    }

    public void setOnOptionClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public OptionsListAdapter(LayoutInflater inflater, List<Option> options) {
        this.inflater = inflater;
        this.options = options;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOptionListBinding binding = ItemOptionListBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.image.setImageResource(options.get(position).getIcon());
        holder.binding.name.setText(options.get(position).getLabel());

        holder.binding.getRoot().setOnClickListener(v -> {
            clickListener.onOptionClick(options.get(position));
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOptionListBinding binding;

        public ViewHolder(@NonNull ItemOptionListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
