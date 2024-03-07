package ru.ptrff.tracktag.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.adapters.TagsAdapter;
import ru.ptrff.tracktag.databinding.FragmentTagBinding;
import ru.ptrff.tracktag.models.Tag;

public class TagFragment extends Fragment {

    private FragmentTagBinding binding;
    private TagsAdapter.TagEvents tagEvents;
    private TagFragmentEvents tagFragmentEvents;

    public TagFragment() {
    }

    private Tag tag;

    public interface TagFragmentEvents{
        void backPressed();
    }

    public TagFragment(Tag tag, TagsAdapter.TagEvents tagEvents, TagFragmentEvents tagFragmentEvents) {
        this.tag = tag;
        this.tagEvents = tagEvents;
        this.tagFragmentEvents = tagFragmentEvents;
    }

    public void setTagEvents(TagsAdapter.TagEvents tagEvents) {
        this.tagEvents = tagEvents;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTagBinding.inflate(inflater);
        if (tag != null) setTag(tag);
        return binding.getRoot();
    }

    public void setTag(Tag tag) {
        binding.tag.getRoot().post(() -> {
            // author
            if (tag.getUser() != null) {
                binding.tag.author.setText(tag.getUser().getUsername());
            } else {
                binding.tag.author.setText(
                        R.string.guest
                );
            }

            // description
            binding.tag.description.setText(tag.getDescription());

            // like
            if (tag.getLiked()) binding.tag.likeButton.setChecked(true);
            binding.tag.likeButton.setText("" + tag.getLikes());
            binding.tag.likeButton.addOnCheckedChangeListener((button, isChecked) -> {
                binding.tag.likeButton.post(() -> {
                    if (isChecked) {
                        binding.tag.likeButton.setText("" + (tag.getLikes() + 1));
                    } else {
                        binding.tag.likeButton.setText("" + tag.getLikes());
                    }
                });
            });

            //focus
            binding.tag.focusButton.setOnClickListener(v -> {
                if (tagEvents != null) {
                    tagEvents.onFocusClick(tag);
                }
            });

            //back
            binding.backButton.setOnClickListener(v -> {
                if (tagFragmentEvents != null) {
                    tagFragmentEvents.backPressed();
                }
            });
        });
    }
}
