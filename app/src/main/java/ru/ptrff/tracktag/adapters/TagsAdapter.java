package ru.ptrff.tracktag.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.databinding.ItemTagBinding;
import ru.ptrff.tracktag.models.Tag;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private AsyncListDiffer<Tag> differ;
    private TagEvents tagEvents;

    public interface TagEvents{
        void onLikeClick(Tag tag);
        void onFocusClick(Tag tag);
    }

    public TagsAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        differ = new AsyncListDiffer<>(this, new TagsDiffCallback());
    }

    public void setTagEvents(TagEvents tagEvents) {
        this.tagEvents = tagEvents;
    }

    @SuppressLint("CheckResult")
    public void updateList(List<Tag> newTags) {
        differ.submitList(newTags);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTagBinding binding = ItemTagBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = differ.getCurrentList().get(position);

        // author
        if (tag.getUser() != null) {
            holder.binding.author.setText(tag.getUser().getUsername());
        } else {
            holder.binding.author.setText(
                    R.string.guest
            );
        }

        // description
        holder.binding.description.setText(tag.getDescription());

        // like
        if(tag.getLiked()) holder.binding.likeButton.setChecked(true);
        holder.binding.likeButton.setText("" + tag.getLikes());
        holder.binding.likeButton.addOnCheckedChangeListener((button, isChecked) -> {
            holder.binding.likeButton.post(() -> {
                if (isChecked) {
                    holder.binding.likeButton.setText("" + (tag.getLikes() + 1));
                } else {
                    holder.binding.likeButton.setText("" + tag.getLikes());
                }
            });
        });

        //focus
        holder.binding.focusButton.setOnClickListener(v -> {
            if (tagEvents != null) {
                tagEvents.onFocusClick(tag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTagBinding binding;

        public ViewHolder(@NonNull ItemTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class TagsDiffCallback extends DiffUtil.ItemCallback<Tag> {
        @Override
        public boolean areItemsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }
}
