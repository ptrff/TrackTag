package ru.ptrff.tracktag.views;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.api.MapsRepository;
import ru.ptrff.tracktag.databinding.FragmentAuthBinding;
import ru.ptrff.tracktag.models.RegisterRequest;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;
    private MapsRepository repo;

    public AuthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater);

        repo = new MapsRepository();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.password.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId== EditorInfo.IME_ACTION_DONE){
                binding.password.clearFocus();
            }
            return false;
        });

        binding.login.setOnClickListener(v -> {

        });

        binding.registration.setOnClickListener(v -> {
            String login = binding.username.getText().toString();
            String password = binding.password.getText().toString();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            //repo.register(new RegisterRequest(login, password));
        });
    }
}
