package ru.ptrff.tracktag.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Objects;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.databinding.ItemTagBinding;
import ru.ptrff.tracktag.models.Tag;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final AsyncListDiffer<Tag> differ = new AsyncListDiffer<>(
            this, new TagsDiffCallback()
    );
    private TagEvents tagEvents;

    public interface TagEvents {
        void onLikeClick(Tag tag);

        void onFocusClick(Tag tag);
    }

    public TagsAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
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

        // Picture
        if(tag.getImage()!=null && !tag.getImage().isEmpty()) {
            Glide.with(holder.binding.image.getContext())
                    .load(tag.getImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.binding.image.setVisibility(View.GONE);
                            Log.d(this.getClass().getCanonicalName(), "No image: " + tag.getImage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transition(withCrossFade())
                    .into(holder.binding.image);
        }else{
            holder.binding.image.setVisibility(View.GONE);
        }

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
        if (tag.getLiked()) holder.binding.likeButton.setChecked(true);
        holder.binding.likeButton.setText("" + tag.getLikes());
        holder.binding.likeButton.addOnCheckedChangeListener((button, isChecked) -> {
            tag.setLiked(isChecked);
            if (isChecked) {
                tag.setLikes(tag.getLikes() + 1);
            } else {
                tag.setLikes(tag.getLikes() - 1);
            }
            holder.binding.likeButton.setText("" + tag.getLikes());
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
