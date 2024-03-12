package ru.ptrff.tracktag.views;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.FragmentAuthBinding;
import ru.ptrff.tracktag.viewmodels.AuthViewModel;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;
    private AuthViewModel viewModel;

    public AuthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClickListeners();
        initObservers();
    }

    private void initClickListeners() {
        binding.password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.password.clearFocus();
            }
            return false;
        });

        binding.login.setOnClickListener(v -> {
            String login = binding.username.getText().toString();
            String password = binding.password.getText().toString();

            if (badFields(login, password)) {
                return;
            }

            viewModel.login(login, password);
        });

        binding.registration.setOnClickListener(v -> {
            String login = binding.username.getText().toString();
            String password = binding.password.getText().toString();

            if (badFields(login, password)) {
                return;
            }

            viewModel.register(login, password);
        });
    }

    private void initObservers() {
        viewModel.getAuthError().observe(
                getViewLifecycleOwner(),
                string -> Toast.makeText(requireContext(), getResources().getString(string), Toast.LENGTH_SHORT).show()
        );

        viewModel.getLoggedIn().observe(getViewLifecycleOwner(), loggedIn -> {
            if (loggedIn) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private boolean badFields(String login, String password) {
        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return true;
        }

        if (password.length() < 3) {
            Toast.makeText(requireContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return true;
        }

        if (login.length() < 3) {
            Toast.makeText(requireContext(), R.string.username_too_short, Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}
