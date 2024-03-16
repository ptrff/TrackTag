package ru.ptrff.tracktag.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;

import java.util.Arrays;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.SearchFilter;
import ru.ptrff.tracktag.databinding.DialogSearchFilterBinding;

public class SearchFilterDialog extends AlertDialog {

    private DialogSearchFilterBinding binding;
    private String[] filters;
    private String[] sorts;

    public SearchFilterDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        binding = DialogSearchFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupFilters();
        initClickListeners();
    }

    private void setupFilters() {
        filters = new String[]{
                getContext().getResources().getString(R.string.by_author),
                getContext().getResources().getString(R.string.by_description)
        };
        sorts = new String[]{
                getContext().getResources().getString(R.string.new_first),
                getContext().getResources().getString(R.string.old_first),
                getContext().getResources().getString(R.string.alphabet_authors),
                getContext().getResources().getString(R.string.most_liked)
        };

        SearchFilter filter = SearchFilter.getInstance();
        if (filter.getByUsers() != null) {
            binding.filterByDropdown.setText(filters[filter.getFilterBy()]);
            binding.sortByDropdown.setText(sorts[filter.getSortBy()]);
        } else {
            binding.filterByDropdown.setText(R.string.by_author);
            binding.sortByDropdown.setText(R.string.new_first);
        }


        binding.filterByDropdown.setSimpleItems(filters);
        binding.sortByDropdown.setSimpleItems(sorts);

        String[] chips = new String[]{
                getContext().getResources().getString(R.string.with_image),
                getContext().getResources().getString(R.string.without_image),
                getContext().getResources().getString(R.string.by_guests),
                getContext().getResources().getString(R.string.by_users),
                getContext().getResources().getString(R.string.with_no_likes)
        };
        Resources r = getContext().getResources();
        for (String chipText : chips) {
            Chip chip = new Chip(getContext(), null, com.google.android.material.R.style.Widget_Material3_Chip_Filter);
            chip.setText(chipText);
            chip.setCheckable(true);
            chip.setClickable(true);
            checkChipState(chip, filter, r);
            binding.chipGroup.addView(chip);
        }
    }

    private void checkChipState(Chip chip, SearchFilter filter, Resources r) {
        String chipText = chip.getText().toString();
        if (filter.getWithImage() != null
                && filter.getWithImage()
                && chipText.equals(r.getString(R.string.with_image))) {
            chip.setChecked(true);
        }
        if (filter.getWithoutImage() != null
                && filter.getWithoutImage()
                && chipText.equals(r.getString(R.string.without_image))) {
            chip.setChecked(true);
        }
        if (filter.getByGuests() != null
                && filter.getByGuests()
                && chipText.equals(r.getString(R.string.by_guests))) {
            chip.setChecked(true);
        }
        if (filter.getByUsers() != null
                && filter.getByUsers()
                && chipText.equals(r.getString(R.string.by_users))) {
            chip.setChecked(true);
        }
        if (filter.getWithNoLikes() != null
                && filter.getWithNoLikes()
                && chipText.equals(r.getString(R.string.with_no_likes))) {
            chip.setChecked(true);
        }
    }

    private void initClickListeners() {
        binding.cancelButton.setOnClickListener(v -> {
            SearchFilter.removeInstance();
            dismiss();
        });

        binding.applyButton.setOnClickListener(v -> {
            SearchFilter filter = SearchFilter.getInstance();

            // dropdown data
            int filterResourceId = Arrays.asList(filters).indexOf(
                    binding.filterByDropdown.getText().toString()
            );

            int sortResourceId = Arrays.asList(sorts).indexOf(
                    binding.sortByDropdown.getText().toString()
            );

            filter.setFilterBy(filterResourceId);
            filter.setSortBy(sortResourceId);

            // chip data
            for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                if (i == 0) filter.setWithImage(chip.isChecked());
                if (i == 1) filter.setWithoutImage(chip.isChecked());
                if (i == 2) filter.setByGuests(chip.isChecked());
                if (i == 3) filter.setByUsers(chip.isChecked());
                if (i == 4) filter.setWithNoLikes(chip.isChecked());
            }
            dismiss();
        });
    }
}
