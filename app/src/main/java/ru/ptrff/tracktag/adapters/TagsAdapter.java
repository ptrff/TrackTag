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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.SearchFilter;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.ItemTagBinding;
import ru.ptrff.tracktag.models.Tag;

public class TagsAdapter extends ListAdapter<Tag, TagsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private TagEvents tagEvents;
    private List<Tag> allTags;

    public interface TagEvents {
        void onLikeClick(Tag tag);
        void onSubscribeClick(Tag tag);
        void focusOnTag(Tag tag);
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
                            holder.binding.image.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .transition(withCrossFade())
                    .into(holder.binding.image);
        }

        // author
        if (tag.getUser() != null) {
            holder.binding.author.setText(tag.getUser().getUsername());
            holder.binding.subButton.setOnClickListener(v -> {
                if (tagEvents != null) {
                    tagEvents.onSubscribeClick(tag);
                }
            });

            holder.binding.subButton.setChecked(UserData.getInstance().isSubscribed(tag.getUser()));
            holder.binding.subButton.setVisibility(View.VISIBLE);
        } else {
            holder.binding.author.setText(R.string.guest);
            holder.binding.subButton.setVisibility(View.GONE);
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
                tagEvents.focusOnTag(tag);
            }
        });
    }

    @SuppressLint("CheckResult")
    public void filter(CharSequence query) {
        SearchFilter f = SearchFilter.getInstance();

        if (allTags == null) return;

        Observable.fromIterable(allTags)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(tag -> {
                    if (f.getByUsers() != null && f.getByUsers() && tag.getUser() == null)
                        return false;
                    if (f.getByGuests() != null && f.getByGuests() && tag.getUser() != null)
                        return false;
                    if (f.getWithImage() != null && f.getWithImage() && !isImagePresent(tag.getImage()))
                        return false;
                    if (f.getWithoutImage() != null && f.getWithoutImage() && isImagePresent(tag.getImage()))
                        return false;
                    if (f.getWithNoLikes() != null && f.getWithNoLikes() && tag.getLikes() != 0)
                        return false;
                    return true;
                })
                .filter(tag -> {
                    if (query != null && !query.toString().isEmpty()) {
                        String filterPattern = query.toString().toLowerCase().trim();
                        if (f.getFilterBy() == null || f.getFilterBy() == 0) {
                            // Filter by author
                            return tag.getUser() != null && tag.getUser().getUsername() != null &&
                                    tag.getUser().getUsername().toLowerCase().contains(filterPattern);
                        } else {
                            // Filter by description
                            return tag.getDescription() != null && tag.getDescription().toLowerCase().contains(filterPattern);
                        }
                    }
                    return true;
                })
                .toSortedList((tag1, tag2) -> {
                    if (f.getSortBy() != null) {
                        switch (f.getSortBy()) {
                            case 2:
                                return Comparator.comparing(tag -> ((Tag) tag).getUser().getUsername())
                                        .compare(tag1, tag2);
                            case 3:
                                return Comparator.comparing(Tag::getLikes).reversed()
                                        .compare(tag1, tag2);
                        }
                    }
                    return 0;
                })
                .map(filteredList -> {
                    if (f.getSortBy() != null && f.getSortBy() == 1) {
                        Collections.reverse(filteredList);
                    }
                    return filteredList;
                })
                .subscribe(
                        this::submitList,
                        (@SuppressLint("CheckResult") Throwable throwable) -> {
                            Log.e(this.getClass().getCanonicalName(), "Filter error", throwable);
                        });
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
