package ru.ptrff.tracktag.views;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.OptionActions;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.FragmentAddTagBinding;
import ru.ptrff.tracktag.interfaces.MainFragmentCallback;
import ru.ptrff.tracktag.viewmodels.AddTagViewModel;

public class AddTagFragment extends Fragment {

    private FragmentAddTagBinding binding;
    private AddTagViewModel viewModel;

    public AddTagFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddTagBinding.inflate(inflater);

        viewModel = new ViewModelProvider(this).get(AddTagViewModel.class);

        if (getArguments() != null) {
            double latitude = getArguments().getDouble("latitude");
            double longitude = getArguments().getDouble("longitude");
            viewModel.setPoint(latitude, longitude);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initObservers();
    }

    private void initObservers() {
        viewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), R.string.tag_created, Toast.LENGTH_SHORT).show();
                ((MainFragmentCallback) requireActivity()).performAction(OptionActions.LIST);
                // TODO reload list
            }
        });
    }

    private void initViews() {
        // restore data after recreation
        if (viewModel.getImageUri() != null) {
            previewImage(viewModel.getImageUri());
        }
        binding.descriptionField.setText(viewModel.getDescription());


        UserData data = UserData.getInstance();
        if (data.isLoggedIn()) {
            binding.author.setText(data.getUserName());
        } else {
            binding.author.setText(R.string.guest);
        }

        binding.image.setOnClickListener(v -> {
            showImageSourceDialog();
        });

        binding.descriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setDescription(s.toString());
            }
        });

        binding.doneButton.setOnClickListener(v -> {
            if (binding.descriptionField.getText().length() < 1) {
                Toast.makeText(requireContext(), R.string.type_description, Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.createTag(
                    requireContext().getContentResolver(),
                    viewModel.getLatitude(),
                    viewModel.getLongitude(),
                    binding.descriptionField.getText().toString()
            );
        });
    }

    private void showImageSourceDialog() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireContext());
        dialog.setTitle(R.string.select_image_source);
        dialog.setItems(new String[]{getString(R.string.take_photo), getString(R.string.choose_from_gallery)}, (d, which) -> {
            switch (which) {
                case 0: // from camera
                    captureImage();
                    break;
                case 1: // from gallery
                    pickImageLauncher.launch("image/*");
                    break;
            }
        });
        dialog.show();
    }

    private void captureImage() {
        viewModel.createImageUri(requireContext().getContentResolver());
        if (viewModel.getImageUri() != null) {
            takePictureLauncher.launch(viewModel.getImageUri());
        } else {
            Log.e(getClass().getCanonicalName(), "Error creating image URI");
        }
    }

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    previewImage(viewModel.getImageUri());
                } else {
                    viewModel.setImageUri(null);
                }
            }
    );

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    viewModel.setImageUri(uri);
                    previewImage(uri);
                }
            }
    );

    private void previewImage(Uri imageUri) {
        if (imageUri != null) {
            binding.image.setImageURI(imageUri);
        }
    }

}
