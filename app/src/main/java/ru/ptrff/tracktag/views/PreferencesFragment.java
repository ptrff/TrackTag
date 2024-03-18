package ru.ptrff.tracktag.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.FragmentPreferencesBinding;
import ru.ptrff.tracktag.databinding.PreferenceDropdownBinding;
import ru.ptrff.tracktag.databinding.PreferenceSwitchBinding;
import ru.ptrff.tracktag.databinding.PreferenceTitleBinding;

public class PreferencesFragment extends Fragment {

    private FragmentPreferencesBinding binding;

    public PreferencesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPreferencesBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupTitle(binding.title, getString(R.string.settings));

        setupDropdownPreference(
                binding.notificationsInterval,
                R.drawable.ic_notification,
                getString(R.string.notifications_interval),
                getResources().getStringArray(R.array.notification_intervals)
                        [UserData.getInstance().getNotificationsInterval()],
                getResources().getStringArray(R.array.notification_intervals),
                newValue -> {
                    UserData.getInstance().setNotificationsInterval(
                            Arrays.asList(
                                    getResources().getStringArray(R.array.notification_intervals)
                            ).indexOf(newValue)
                    );
                }
        );

        setupSwitchPreference(
                binding.allowOptionsOnMainScreen,
                R.drawable.ic_dashboard,
                getString(R.string.allow_options_on_main_screen),
                UserData.getInstance().isAllowOptionsOnMainScreen() ? "true" : "false",
                new String[]{ getString(R.string.are_hidden), getString(R.string.are_visible)},
                newValue -> {
                    UserData.getInstance().setAllowOptionsOnMainScreen(newValue.equals("true"));
                }
        );

        setupSwitchPreference(
                binding.darkMode,
                R.drawable.ic_moon,
                getString(R.string.force_dark_mode),
                UserData.getInstance().isNightMode() ? "true" : "false",
                new String[]{getString(R.string.disabled), getString(R.string.enabled)},
                newValue -> {
                    UserData.getInstance().setNightMode(newValue.equals("true"));
                    showRestartDialog();
                }
        );
    }

    private void setupDropdownPreference(
            PreferenceDropdownBinding binding,
            Integer iconResource,
            String title,
            String defValue,
            String[] items,
            ValueListener listener) {

        binding.icon.setImageResource(iconResource);
        binding.title.setText(title);
        binding.summary.setText(defValue);
        binding.getRoot().setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(requireContext(), binding.title, Gravity.END,
                    0, R.style.PopupMenu);
            for (String item : items) {
                menu.getMenu().add(item);
            }
            menu.setOnMenuItemClickListener(item -> {
                listener.onValueChanged(item.getTitle().toString());
                binding.summary.setText(item.getTitle().toString());
                return true;
            });
            menu.show();
        });
    }

    private void setupSwitchPreference(
            PreferenceSwitchBinding binding,
            Integer iconResource,
            String title,
            String defValue,
            String[] summary,
            ValueListener listener) {

        binding.icon.setImageResource(iconResource);
        binding.title.setText(title);
        binding.switchButton.setChecked(defValue.equals("true"));
        binding.summary.setText(summary[defValue.equals("true") ? 1 : 0]);
        binding.getRoot().setOnClickListener(v -> {
            binding.switchButton.setChecked(!binding.switchButton.isChecked());
        });
        binding.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onValueChanged(String.valueOf(isChecked));
            binding.summary.setText(summary[isChecked ? 1 : 0]);
        });
    }

    private void showRestartDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.restart_required_for_applying);
        builder.setPositiveButton(R.string.restart_now, (dialog, which) -> restartApp());
        builder.setNegativeButton(R.string.later, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void restartApp() {
        Intent intent = requireActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

    private void setupTitle(PreferenceTitleBinding binding, String title) {
        binding.title.setText(title);
    }

    private interface ValueListener {
        void onValueChanged(String newValue);
    }
}
