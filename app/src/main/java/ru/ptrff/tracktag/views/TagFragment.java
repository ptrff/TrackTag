package ru.ptrff.tracktag.views;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.databinding.FragmentTagBinding;
import ru.ptrff.tracktag.interfaces.MainFragmentCallback;
import ru.ptrff.tracktag.models.Tag;

public class TagFragment extends Fragment {

    private FragmentTagBinding binding;
    private MainFragmentCallback mainFragmentCallback;

    public TagFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTagBinding.inflate(inflater);
        Tag tag = getArguments().getParcelable("tag");
        mainFragmentCallback = (MainFragmentCallback) requireActivity();
        if (tag != null) setTag(tag);
        return binding.getRoot();
    }

    public void setTag(Tag tag) {
        binding.tag.getRoot().post(() -> {

            // Picture
            if (tag.getImage() != null && !tag.getImage().isEmpty()) {
                binding.tag.image.setVisibility(View.VISIBLE);
                Glide.with(binding.tag.image.getContext())
                        .load("https://maps.rtuitlab.dev" + tag.getImage())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                setImageSize();
                                Log.d(this.getClass().getCanonicalName(), "No image: " + tag.getImage());
                                binding.tag.image.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .transition(withCrossFade())
                        .into(binding.tag.image);
            } else {
                setImageSize();
            }


            // author
            if (tag.getUser() != null) {
                binding.tag.author.setText(tag.getUser().getUsername());
            } else {
                binding.tag.author.setText(R.string.guest);
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
                if (mainFragmentCallback != null) {
                    mainFragmentCallback.focusOnTag(tag);
                }
            });

            //back
            binding.backButton.setOnClickListener(v -> {
                getParentFragmentManager().popBackStack();
            });
        });
    }

    private void setImageSize() {
        binding.tag.image.setVisibility(View.INVISIBLE);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.tag.image.getLayoutParams().width = binding.backButton.getMeasuredWidth() * 5 / 2;
        } else {
            binding.tag.image.getLayoutParams().height = binding.backButton.getMeasuredHeight();
        }
        binding.tag.image.requestLayout();
    }
}
