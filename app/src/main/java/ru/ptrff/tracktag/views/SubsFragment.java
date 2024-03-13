package ru.ptrff.tracktag.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import ru.ptrff.tracktag.adapters.SubsAdapter;
import ru.ptrff.tracktag.data.Options;
import ru.ptrff.tracktag.databinding.FragmentSubsBinding;

public class SubsFragment extends Fragment {

    private FragmentSubsBinding binding;

    public SubsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.subsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        SubsAdapter adapter = new SubsAdapter(
                getLayoutInflater(),
                Options.user
        );
        binding.subsList.setAdapter(adapter);
    }
}