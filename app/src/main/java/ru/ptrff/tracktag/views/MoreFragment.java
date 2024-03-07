package ru.ptrff.tracktag.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ptrff.tracktag.databinding.FragmentMoreBinding;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;

    public MoreFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMoreBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}