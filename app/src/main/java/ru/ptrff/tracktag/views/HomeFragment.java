package ru.ptrff.tracktag.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
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

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.adapters.OptionsAdapter;
import ru.ptrff.tracktag.adapters.TagsAdapter;
import ru.ptrff.tracktag.data.SearchFilter;
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

    private boolean initiated = false;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        );

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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

        if (!initiated) {
            viewModel.getData();
            initiated = true;
        }
    }

    private void initObservers() {
        viewModel.getTags().observe(getViewLifecycleOwner(), tags -> {
            tagsAdapter.submitList(tags);
            mainFragmentCallback.onTagsLoaded(tags);
        });

        viewModel.getOptions().observe(getViewLifecycleOwner(), options -> {
            optionsAdapter.submitList(options);
        });
    }

    private void initClickListeners() {
        binding.upButton.setOnClickListener(v -> {
            scrollUp();
        });

        binding.searchLayout.setEndIconOnClickListener(v -> {
            SearchFilterDialog dialog = new SearchFilterDialog(requireContext());
            dialog.show();
            dialog.setOnDismissListener(dialog1 -> {
                checkSearchFilters();
            });

            hideKeyboard();
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(binding.searchField.getWindowToken(), 0);
    }

    private void checkSearchFilters(){
        if (SearchFilter.getInstance().getFilterBy()!=null){
            final TypedValue value = new TypedValue();
            requireContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, value, true);
            ColorStateList colorStateList = ColorStateList.valueOf(value.data);
            binding.searchLayout.setEndIconTintList(colorStateList);
        }else{
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
            public void onFocusClick(Tag tag) {
                mainFragmentCallback.focusOnTag(tag);
            }
        });

        binding.tagsList.setAdapter(tagsAdapter);

        initTagListScrollListener();

        // options list
        int optionListOrientation;
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            optionListOrientation = LinearLayoutManager.VERTICAL;
            ViewCompat.setNestedScrollingEnabled(binding.tagsList, false);
            ViewCompat.setNestedScrollingEnabled(binding.optionsList, false);
        } else {
            optionListOrientation = LinearLayoutManager.HORIZONTAL;
        }


        binding.optionsList.setLayoutManager(new LinearLayoutManager(requireContext(), optionListOrientation, false));
        optionsAdapter = new OptionsAdapter(requireContext(), viewModel.getOptionsAsList());
        optionsAdapter.setOptionsEvents(option -> mainFragmentCallback.performAction(option.getAction()));
        binding.optionsList.setAdapter(optionsAdapter);
    }

    private void initTagListScrollListener() {
        binding.tagsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (binding.upButton.getVisibility() == View.GONE
                        && tagsListLayoutManager.findFirstVisibleItemPosition() != 0) {
                    binding.upButton.setVisibility(View.VISIBLE);
                } else if (binding.upButton.getVisibility() == View.VISIBLE
                        && tagsListLayoutManager.findFirstVisibleItemPosition() == 0) {
                    binding.upButton.setVisibility(View.GONE);
                }
            }
        });

        /*binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int diff = scrollY - oldScrollY;
            if (binding.upButton.getVisibility() == View.GONE && scrollY > 100) {
                binding.upButton.setVisibility(View.VISIBLE);
            } else if (binding.upButton.getVisibility() == View.VISIBLE && scrollY < 100) {
                binding.upButton.setVisibility(View.GONE);
            }


            int fullHeight = binding.tagsList.getMeasuredHeight();
            int oneTagSize = fullHeight / binding.tagsList.getLayoutManager().getChildCount();
            if (binding.scrollView.getMeasuredHeight() + scrollY > fullHeight - oneTagSize && gotMore) {
                viewModel.loadMore();
                gotMore = false;
            }
        });*/
    }

    public void scrollUp() {
        binding.tagsList.fling(0, binding.tagsList.getMaxFlingVelocity() * -1);
    }
}