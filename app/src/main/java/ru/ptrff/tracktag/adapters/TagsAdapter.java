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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        SearchFilter f = SearchFilter.getInstance();

        if (allTags == null) return;
        Stream<Tag> tagStream = allTags.stream();
        if (f.getSortBy() != null) {
            // Сортировка
            switch (f.getSortBy()) {
                case 2: // По алфавиту авторов
                    tagStream = tagStream.filter(tag -> tag.getUser() != null)
                            .sorted(Comparator.comparing(tag -> tag.getUser().getUsername()));
                    break;
                case 3: // По лайкам
                    tagStream = tagStream.sorted(Comparator.comparing(Tag::getLikes).reversed());
                    break;
            }
            submitList(null);
        }

        if (f.getByUsers() != null && f.getByUsers()) {
            tagStream = tagStream.filter(tag -> tag.getUser() != null);
        }
        if (f.getByGuests() != null && f.getByGuests()) {
            tagStream = tagStream.filter(tag -> tag.getUser() == null);
        }
        if (f.getWithImage() != null && f.getWithImage()) {
            tagStream = tagStream.filter(tag ->
                    (f.getWithImage() && isImagePresent(tag.getImage()))
                            || (!f.getWithImage() && !isImagePresent(tag.getImage()))
            );
        }
        if (f.getWithoutImage() != null && f.getWithoutImage()) {
            tagStream = tagStream.filter(tag ->
                    (f.getWithoutImage() && !isImagePresent(tag.getImage()))
                            || (!f.getWithoutImage() && isImagePresent(tag.getImage()))
            );
        }
        if (f.getWithNoLikes() != null && f.getWithNoLikes()) {
            tagStream = tagStream.filter(tag -> tag.getLikes() == 0);
        }

        if (query != null && !query.toString().isEmpty()) {
            String filterPattern = query.toString().toLowerCase().trim();

            if (f.getFilterBy() == null || f.getFilterBy() == 0) {
                // По автору
                tagStream = tagStream.filter(
                        tag -> tag.getUser() != null
                                && tag.getUser().getUsername() != null
                                && tag.getUser().getUsername().toLowerCase().contains(filterPattern)
                );
            } else {
                // По описанию
                tagStream = tagStream.filter(
                        tag -> tag.getDescription() != null
                                && tag.getDescription().toLowerCase().contains(filterPattern)
                );
            }
        }

        List<Tag> filteredList = tagStream.collect(Collectors.toList());
        if (f.getSortBy() != null && f.getSortBy() == 1) {
            Collections.reverse(filteredList);
        }

        submitList(filteredList);
    }

    private boolean isImagePresent(String imageUrl) {
        return imageUrl != null && !imageUrl.startsWith("/storage/");
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
