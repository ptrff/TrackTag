package ru.ptrff.tracktag.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;

import ru.ptrff.tracktag.databinding.DialogSearchFilterBinding;

public class SearchFilterDialog extends AlertDialog {

    private DialogSearchFilterBinding binding;

    public SearchFilterDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogSearchFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        binding.sortByDropdown.setSimpleItems(new String[]{"a", "б", "в", "г"});
        binding.typeDropdown.setSimpleItems(new String[]{"a", "б", "в", "г"});

        String[] chips = new String[]{"чипс1", "чипс2", "чипс3", "чипс4"};
        for (String chipText : chips) {
            Chip chip = new Chip(getContext());
            chip.setText(chipText);
            binding.chipGroup.addView(chip);
        }
    }
}
