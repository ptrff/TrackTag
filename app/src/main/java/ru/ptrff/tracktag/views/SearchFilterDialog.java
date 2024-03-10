package ru.ptrff.tracktag.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;

import ru.ptrff.tracktag.R;
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
        binding.filterByDropdown.setSimpleItems(new String[]{"По автору", "По описанию"});
        binding.sortByDropdown.setSimpleItems(new String[]{"Сначала новые", "Сначала старые"});

        String[] chips = new String[]{"С картинкой", "Без картинки", "От гостей", "Без лайков"};
        for (String chipText : chips) {
            Chip chip = new Chip(getContext(), null, com.google.android.material.R.style.Widget_Material3_Chip_Filter);
            chip.setText(chipText);
            chip.setCheckable(true);
            chip.setClickable(true);
            binding.chipGroup.addView(chip);
        }
    }

    private void initClickListeners() {
        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.applyButton.setOnClickListener(v -> dismiss());
    }
}
