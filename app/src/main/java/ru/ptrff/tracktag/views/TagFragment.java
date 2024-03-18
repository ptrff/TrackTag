package ru.ptrff.tracktag.views;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.OptionActions;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.FragmentTagBinding;
import ru.ptrff.tracktag.interfaces.MainFragmentCallback;
import ru.ptrff.tracktag.models.Tag;
import ru.ptrff.tracktag.viewmodels.TagViewModel;

public class TagFragment extends Fragment {

    private FragmentTagBinding binding;
    private TagViewModel viewModel;
    private MainFragmentCallback callback;

    public TagFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTagBinding.inflate(inflater);
        Tag tag = getArguments().getParcelable("tag");
        viewModel = new ViewModelProvider(this).get(TagViewModel.class);
        callback = (MainFragmentCallback) requireActivity();
        if (tag != null) {
            setTag(tag);
            initObservers();
        }
        return binding.getRoot();
    }

    public void setTag(Tag tag) {
        binding.tag.getRoot().post(() -> {
            // background
            binding.tag.background.setClickable(false);
            binding.tag.background.setBackground(null);

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

            // options
            if (tag.getUser() != null) {
                binding.tag.optionsButton.setVisibility(View.VISIBLE);
                if (UserData.getInstance().isLoggedIn()
                        && UserData.getInstance().getUserName().equals(tag.getUser().getUsername())) {
                    binding.tag.optionsButton.setCheckable(false);
                    binding.tag.optionsButton.setIconResource(R.drawable.ic_delete);
                    binding.tag.optionsButton.setOnClickListener(v -> {
                        viewModel.deleteTag(tag);
                    });
                } else {
                    binding.tag.optionsButton.setCheckable(true);
                    binding.tag.optionsButton.setIconResource(R.drawable.sl_notification);
                    binding.tag.optionsButton.setChecked(UserData.getInstance().isSubscribed(tag.getUser()));
                    binding.tag.optionsButton.setOnClickListener(v -> {
                        viewModel.subscribe(tag);
                        checkForPermission();
                    });
                }
            } else {
                binding.tag.optionsButton.setVisibility(View.GONE);
            }

            // description
            binding.tag.description.setText(tag.getDescription());

            // like
            binding.tag.likeButton.clearOnCheckedChangeListeners();
            binding.tag.likeButton.setCheckable(UserData.getInstance().isLoggedIn());
            binding.tag.likeButton.setChecked(tag.getLiked());
            binding.tag.likeButton.setText("" + tag.getLikes());
            binding.tag.likeButton.addOnCheckedChangeListener((button, isChecked) -> {
                tag.setLiked(isChecked);
                if (isChecked) {
                    tag.setLikes(tag.getLikes() + 1);
                    viewModel.likeTag(tag, true);
                } else {
                    tag.setLikes(tag.getLikes() - 1);
                    viewModel.likeTag(tag, false);
                }
                binding.tag.likeButton.setText("" + tag.getLikes());
            });
            //focus
            binding.tag.focusButton.setOnClickListener(v -> {
                callback.focusOnTag(tag);
            });

            // back
            binding.backButton.setOnClickListener(v -> {
                callback.performAction(OptionActions.LIST);
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

    private void initObservers(){
        viewModel.getDeleteDone().observe(getViewLifecycleOwner(), success -> {
            Toast.makeText(requireContext(), R.string.tag_deleted, Toast.LENGTH_SHORT).show();
            callback.performAction(OptionActions.LIST);
        });
    }

    private void checkForPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            UserData.getInstance().setNotificationsAllowed(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1
                );
            }
            Toast.makeText(requireContext(), R.string.allow_notifications_to_receive_them, Toast.LENGTH_SHORT).show();
            return;
        }
        UserData.getInstance().setNotificationsAllowed(true);
    }
}
