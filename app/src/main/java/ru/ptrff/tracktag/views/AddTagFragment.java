package ru.ptrff.tracktag.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.FragmentAddTagBinding;

public class AddTagFragment extends Fragment {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private FragmentAddTagBinding binding;

    public AddTagFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddTagBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserData data = UserData.getInstance();
        if (data.isLoggedIn()) {
            binding.author.setText(data.getUserName());
        } else {
            binding.author.setText(R.string.guest);
        }


        getResult();
        binding.image.setOnClickListener(v -> {
            getImage();
        });
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(intent, getString(R.string.choose_photo));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        activityResultLauncher.launch(chooser);
    }

    private void getResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getData() != null) { //from galery
                        Uri imageUri = data.getData();
                        // TODO: upload and get url

                    } else if (!data.getExtras().isEmpty()) { //from camera
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        byte[] byteArray = stream.toByteArray();
                        photo.recycle();

                        // TODO: upload and get url
                    }
                }
            }
        });
    }

}
