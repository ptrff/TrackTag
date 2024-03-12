package ru.ptrff.tracktag.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.SearchFilter;
import ru.ptrff.tracktag.databinding.DialogSearchFilterBinding;

public class SearchFilterDialog extends AlertDialog {

    private DialogSearchFilterBinding binding;

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
        SearchFilter filter = SearchFilter.getInstance();
        if (filter.getByUsers() != null) {
            binding.filterByDropdown.setText(filter.getFilterBy());
            binding.sortByDropdown.setText(filter.getSortBy());
        } else {
            binding.filterByDropdown.setText(R.string.by_author);
            binding.sortByDropdown.setText(R.string.new_first);
        }

        binding.filterByDropdown.setSimpleItems(new String[]{
                getContext().getResources().getString(R.string.by_author),
                getContext().getResources().getString(R.string.by_description)
        });

        binding.sortByDropdown.setSimpleItems(new String[]{
                getContext().getResources().getString(R.string.new_first),
                getContext().getResources().getString(R.string.old_first)
        });

        String[] chips = new String[]{
                getContext().getResources().getString(R.string.with_image),
                getContext().getResources().getString(R.string.without_image),
                getContext().getResources().getString(R.string.by_guests),
                getContext().getResources().getString(R.string.by_users),
                getContext().getResources().getString(R.string.with_no_likes)
        };
        for (String chipText : chips) {
            Chip chip = new Chip(getContext(), null, com.google.android.material.R.style.Widget_Material3_Chip_Filter);
            chip.setText(chipText);
            chip.setCheckable(true);
            chip.setClickable(true);
            binding.chipGroup.addView(chip);
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
            filter.setFilterBy(binding.filterByDropdown.getText().toString());
            filter.setSortBy(binding.sortByDropdown.getText().toString());

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
