package ru.ptrff.tracktag.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.ptrff.tracktag.adapters.OptionsAdapter;
import ru.ptrff.tracktag.adapters.TagsAdapter;
import ru.ptrff.tracktag.databinding.FragmentHomeBinding;
import ru.ptrff.tracktag.interfaces.MapDataCallback;
import ru.ptrff.tracktag.models.Tag;
import ru.ptrff.tracktag.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private TagsAdapter tagsAdapter;
    private OptionsAdapter optionsAdapter;
    private MapDataCallback mapDataCallback;

    private boolean gotMore = false;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        );

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mapDataCallback = (MapDataCallback) requireActivity();
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


        viewModel.getData();
    }

    private void initObservers() {
        viewModel.getTags().observe(getViewLifecycleOwner(), tags -> {
            tagsAdapter.updateList(tags);
            mapDataCallback.onTagsLoaded(tags);
            gotMore = true;
        });

        viewModel.getOptions().observe(getViewLifecycleOwner(), options -> {
            optionsAdapter.submitList(options);
        });
    }

    private void initClickListeners() {
        binding.upButton.setOnClickListener(v -> {
            scrollUp();
        });
    }

    private void initRecyclers() {
        binding.tagsList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        tagsAdapter = new TagsAdapter(requireContext());
        tagsAdapter.setTagEvents(new TagsAdapter.TagEvents() {
            @Override
            public void onLikeClick(Tag tag) {
                //TODO
            }

            @Override
            public void onFocusClick(Tag tag) {
                mapDataCallback.focusOnTag(tag);
            }
        });
        tagsAdapter.setHasStableIds(true);
        binding.tagsList.setAdapter(tagsAdapter);

        initTagListScrollListener();

        binding.optionsList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        optionsAdapter = new OptionsAdapter(requireContext(), viewModel.getOptionsAsList());
        binding.optionsList.setAdapter(optionsAdapter);
    }

    private void initTagListScrollListener() {
        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
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
        });


    }

    public void scrollUp() {
        binding.tagsList.fling(0, binding.tagsList.getMaxFlingVelocity() * -1);
    }
}