package ru.ptrff.tracktag.views;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ru.ptrff.tracktag.adapters.OptionsAdapter;
import ru.ptrff.tracktag.adapters.TagsAdapter;
import ru.ptrff.tracktag.data.OptionActions;
import ru.ptrff.tracktag.data.SearchFilter;
import ru.ptrff.tracktag.data.local.TagLocalRepository;
import ru.ptrff.tracktag.databinding.FragmentHomeBinding;
import ru.ptrff.tracktag.interfaces.MainFragmentCallback;
import ru.ptrff.tracktag.models.Tag;
import ru.ptrff.tracktag.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private TagsAdapter tagsAdapter;
    private OptionsAdapter optionsAdapter;
    private MainFragmentCallback mainFragmentCallback;
    private LinearLayoutManager tagsListLayoutManager;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        );

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        if (!viewModel.isInitiated()) {
            viewModel.setLocalRepo(new TagLocalRepository(requireContext()));
            viewModel.addNetworkConnectionListener(
                    Objects.requireNonNull(getSystemService(requireContext(), ConnectivityManager.class))
            );
            viewModel.setInitiated(true);
        }

        mainFragmentCallback = (MainFragmentCallback) requireActivity();
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclers();
        initObservers();
        initClickListeners();
        checkSearchFilters();
        setBottomSheetPeekHeight();
        showUpButton(false);
        setTagsListHeight();
    }

    private void initObservers() {
        viewModel.getTags().observe(getViewLifecycleOwner(), tags -> {
            tagsAdapter.setAllTags(tags);
            mainFragmentCallback.onTagsLoaded(tags);
            applySearchFilters();
            scrollUp(false);
        });

        viewModel.getOptions().observe(getViewLifecycleOwner(), options -> {
            optionsAdapter.submitList(options);
        });
    }

    private void initClickListeners() {
        binding.upButton.setOnClickListener(v -> {
            scrollUp(true);
        });

        binding.searchLayout.setEndIconOnClickListener(v -> {
            SearchFilterDialog dialog = new SearchFilterDialog(requireContext());
            dialog.setOnDismissListener(dialog1 -> {
                checkSearchFilters();
                applySearchFilters();
            });
            dialog.setOnShowListener(dialog1 -> {
                if (binding.searchField.hasFocus()) {
                    binding.searchField.clearFocus();
                }
            });
            dialog.show();
        });


        binding.searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && binding.searchField.hasFocus()) {
                    mainFragmentCallback.setBottomSheetState(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                tagsAdapter.filter(s);
                scrollUp(false);
            }
        });

        binding.searchField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                liftOptions(true);
            } else {
                hideKeyboard();
                liftOptions(false);
            }
        });

        binding.refreshButton.setOnClickListener(v -> viewModel.getData());
    }


    private boolean liftOptionsAnimation = false;
    private boolean liftOptionsVisible;

    private void liftOptions(boolean lift) {
        if (liftOptionsAnimation && lift == liftOptionsVisible) return;
        else if (liftOptionsAnimation) {
            ViewCompat
                    .animate(binding.optionsList)
                    .cancel();
            ViewCompat
                    .animate(binding.tagsList)
                    .cancel();
        }


        float destinationY;
        float destinationAlpha;
        if (lift) {
            destinationY = (binding.optionsList.getHeight()
                    + ((ViewGroup.MarginLayoutParams) binding.optionsList.getLayoutParams()).bottomMargin) * -1;
            destinationAlpha = 0;
            liftOptionsVisible = true;
        } else {
            destinationY = 0;
            destinationAlpha = 1;
            liftOptionsVisible = false;
        }

        ViewCompat
                .animate(binding.optionsList)
                .translationY(destinationY)
                .alpha(destinationAlpha)
                .setDuration(300)
                .withEndAction(() -> liftOptionsAnimation = false)
                .start();

        ViewCompat
                .animate(binding.tagsList)
                .translationY(destinationY)
                .setDuration(300)
                .start();

        liftOptionsAnimation = true;
    }

    private boolean upButtonAnimation = false;
    private boolean upButtonVisible;

    private void showUpButton(boolean visible) {
        if (upButtonAnimation && visible == upButtonVisible) return;
        else if (upButtonAnimation) {
            ViewCompat
                    .animate(binding.upButton)
                    .cancel();
        }

        float destinationX;
        if (visible) {
            destinationX = 0;
            upButtonVisible = true;
        } else {
            destinationX = binding.upButton.getMeasuredWidth()
                    + ((ViewGroup.MarginLayoutParams) binding.upButton.getLayoutParams()).getMarginEnd();
            upButtonVisible = false;
        }

        ViewCompat
                .animate(binding.upButton)
                .translationX(destinationX)
                .setDuration(300)
                .withEndAction(() -> upButtonAnimation = false)
                .start();
        upButtonAnimation = true;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(binding.searchField.getWindowToken(), 0);
    }

    private void applySearchFilters() {
        tagsAdapter.filter(binding.searchField.getText());
    }

    private void checkSearchFilters() {
        if (SearchFilter.getInstance().getFilterBy() != null) {
            final TypedValue value = new TypedValue();
            requireContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, value, true);
            ColorStateList colorStateList = ColorStateList.valueOf(value.data);
            binding.searchLayout.setEndIconTintList(colorStateList);
        } else {
            final TypedValue value = new TypedValue();
            requireContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorAccent, value, true);
            ColorStateList colorStateList = ColorStateList.valueOf(value.data);
            binding.searchLayout.setEndIconTintList(colorStateList);
        }
    }

    private void initRecyclers() {
        // tags list
        tagsListLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.tagsList.setLayoutManager(tagsListLayoutManager);
        tagsAdapter = new TagsAdapter(requireContext());
        tagsAdapter.setTagEvents(new TagsAdapter.TagEvents() {
            @Override
            public void onLikeClick(Tag tag) {
                //TODO
            }

            @Override
            public void onSubscribeClick(Tag tag) {
                viewModel.updateLastTagsIDs();
                mainFragmentCallback.onSubscribeClick(tag);
            }

            @Override
            public void focusOnTag(Tag tag) {
                mainFragmentCallback.focusOnTag(tag);
            }
        });

        binding.tagsList.setAdapter(tagsAdapter);

        initTagListScrollListener();

        // options list
        binding.optionsList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        optionsAdapter = new OptionsAdapter(requireContext());
        optionsAdapter.setOptionsEvents(option -> mainFragmentCallback.performAction(option.getAction()));
        binding.optionsList.setAdapter(optionsAdapter);
        viewModel.initOptions();
    }

    private void initTagListScrollListener() {
        binding.tagsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (tagsListLayoutManager.findFirstVisibleItemPosition() != 0) {
                    showUpButton(true);
                } else if (tagsListLayoutManager.findFirstVisibleItemPosition() == 0) {
                    showUpButton(false);
                }

                liftOptions(dy > 0  || binding.searchField.hasFocus());
            }
        });
    }

    public void scrollUp(boolean smooth) {
        if (smooth) {
            binding.tagsList.smoothScrollToPosition(0);
        } else {
            binding.tagsList.scrollToPosition(0);
        }
    }

    private void setBottomSheetPeekHeight() {
        binding.searchField.post(() -> {
            mainFragmentCallback.setBottomPeekHeight(
                    binding.searchField.getMeasuredHeight()
                            + binding.dragView.getMeasuredHeight()
                            + ((ViewGroup.MarginLayoutParams) binding.dragView.getLayoutParams()).topMargin
                            + ((ViewGroup.MarginLayoutParams) binding.dragView.getLayoutParams()).bottomMargin
                            + ((ViewGroup.MarginLayoutParams) binding.searchLayout.getLayoutParams()).bottomMargin
            );
        });
    }

    private void setTagsListHeight() {
        binding.tagsList.post(() -> {
            binding.tagsList.getLayoutParams().height = binding.tagsList.getMeasuredHeight()
                    + ((ViewGroup.MarginLayoutParams) binding.optionsList.getLayoutParams()).bottomMargin
                    + binding.optionsList.getMeasuredHeight();
        });
    }
}