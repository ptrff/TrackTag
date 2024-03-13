package ru.ptrff.tracktag.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import kotlin.collections.AbstractMutableList;
import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.SearchFilter;
import ru.ptrff.tracktag.databinding.ItemTagBinding;
import ru.ptrff.tracktag.models.Tag;

public class TagsAdapter extends ListAdapter<Tag, TagsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private TagEvents tagEvents;
    private List<Tag> allTags;
    private boolean newFirst = true;

    public interface TagEvents {
        void onLikeClick(Tag tag);

        void onFocusClick(Tag tag);
    }

    public TagsAdapter(Context context) {
        super(new TagsDiffCallback());
        this.inflater = LayoutInflater.from(context);
    }

    public void setTagEvents(TagEvents tagEvents) {
        this.tagEvents = tagEvents;
    }

    public void setAllTags(List<Tag> list) {
        allTags = list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTagBinding binding = ItemTagBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = getItem(position);

        // Picture
        if (tag.getImage() != null && !tag.getImage().isEmpty() && !tag.getImage().startsWith("/storage/")) {
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
        } else {
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

    public void filter(CharSequence query, Resources r) {
        /*SearchFilter filter = SearchFilter.getInstance();

        if ((query == null || query.toString().isEmpty()) && filter.getSortBy() == null) {
            submitList(null);
            submitList(allTags);
            return;
        }

        List<Tag> list = new ArrayList<>();

        if (filter.getSortBy() == null || filter.getSortBy().equals(r.getString(R.string.new_first))) {
            if (allTags == null) return;
            if (!newFirst) {
                Collections.reverse(allTags);
                newFirst = true;
            }
        } else {
            if (allTags == null) return;
            if (newFirst) {
                Collections.reverse(allTags);
                newFirst = false;
            }
        }

        if (query != null && !query.toString().isEmpty()) {
            if (allTags == null) return;

            list.addAll(allTags.stream()
                    .filter(item -> {
                        boolean mask = true;

                        // with image
                        if (filter.getWithImage() != null && filter.getWithImage()) {
                            if (item.getImage() == null || item.getImage().isEmpty() || item.getImage().startsWith("/storage/")) {
                                mask = false;
                            }
                        }

                        // without image
                        if (filter.getWithoutImage() != null && filter.getWithoutImage()) {
                            if (item.getImage() == null || item.getImage().isEmpty() || item.getImage().startsWith("/storage/")) {
                                mask = true;
                            }
                        }


                        // Filter
                        if (filter.getFilterBy() == null ||
                                filter.getFilterBy().equals(r.getString(R.string.by_author))) {
                            if (item.getUser() != null) {
                                mask = mask && item.getUser().getUsername().toLowerCase().contains(query.toString().toLowerCase());
                            } else {
                                mask = false;
                            }
                        } else {
                            mask = mask && item.getDescription().toLowerCase().contains(query.toString().toLowerCase());
                        }

                        return mask;
                    })
                    .collect(Collectors.toList()));

            submitList(list);
            return;
        }

        // submit all tags (no filter)*/
//        submitList(null);
        // TODO rework
        submitList(allTags);
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
