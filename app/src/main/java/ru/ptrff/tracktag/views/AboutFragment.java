package ru.ptrff.tracktag.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ru.ptrff.tracktag.BuildConfig;
import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.version.setText("Ver: " + BuildConfig.VERSION_NAME);

        binding.app.setOnClickListener(v -> {
            followLink("https://github.com/ptrff/TrackTag");
        });

        binding.contacts.setOnClickListener(v -> {
            showRestartDialog(new String[]{
                    "https://vk.com/i_petroff",
                    "https://t.me/i_petroff"
            });
        });
    }

    private void showRestartDialog(String[] links) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.choose_link);
        builder.setItems(links, (dialog, which) -> {
            Toast.makeText(requireContext(), " "+which, Toast.LENGTH_SHORT).show();
            followLink(links[which]);
        });
        builder.show();
    }

    private void followLink(String link){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        startActivity(intent);
    }
}
