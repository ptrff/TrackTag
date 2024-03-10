package ru.ptrff.tracktag.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.Arrays;
import java.util.List;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.adapters.OptionsListAdapter;
import ru.ptrff.tracktag.data.Options;
import ru.ptrff.tracktag.databinding.FragmentMoreBinding;
import ru.ptrff.tracktag.interfaces.MoreFragmentCallback;
import ru.ptrff.tracktag.models.Option;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;
    private MoreFragmentCallback callback;
    private boolean isAuthorized = false;

    public MoreFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (MoreFragmentCallback) requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMoreBinding.inflate(inflater, container, false);

        binding.optionsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        OptionsListAdapter adapter = new OptionsListAdapter(inflater, isAuthorized ? Options.authorized : Options.guest);
        adapter.setOnOptionClickListener(option -> callback.performAction(option.getAction()));
        binding.optionsList.setAdapter(adapter);

        return binding.getRoot();
    }
}