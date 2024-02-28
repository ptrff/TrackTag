package ru.ptrff.tracktag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yandex.mapkit.MapKitFactory;

import ru.ptrff.tracktag.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Bottom sheet states used for animations and gestures
    // 0 - collapsed, 1 - half expanded, 2 - expanded
    // -1 - auto collapsed, -2 - auto half expanded, -3 - auto expanded
    private int bottomState = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) initMapKit();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        applyMapStyle();
        setupBottomSheet();
    }

    // Auto dark mode
    private void applyMapStyle() {
        int nightModeFlags = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.mapview.getMapWindow().getMap().setNightModeEnabled(true);
        }
    }

    private void setupBottomSheet() {
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior =
                BottomSheetBehavior.from(binding.bottomSheet.root);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomState = 2;
                }
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.home);
                    bottomState = 1;
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.map);
                    bottomState = 0;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (bottomState < 0) return; // do not animate on auto state

                if (slideOffset > 0.6f) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (slideOffset > 0.3f) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (bottomState < 0) return false; // do not change state while animating

            if (item.getItemId() == R.id.map) {
                if (bottomState != 0) bottomState = -1;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (item.getItemId() == R.id.home) {
                if (bottomState != 1) bottomState = -2;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
            if (item.getItemId() == R.id.more) {
                if (bottomState != 2) bottomState = -3;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return true;
        });
    }

    private void initMapKit() {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapview.onStart();
    }

    @Override
    protected void onStop() {
        binding.mapview.onStop();
        super.onStop();
    }

}