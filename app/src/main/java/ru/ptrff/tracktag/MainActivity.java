package ru.ptrff.tracktag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yandex.mapkit.MapKitFactory;

import ru.ptrff.tracktag.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Bottom sheet states used for animations and gestures
    // 0 - collapsed, 1 - half expanded, 2 - expanded
    // -1 - auto collapsed, -2 - auto half expanded, -3 - auto expanded
    private int bottomState = 1;

    private GradientDrawable bottomSheetBackground;
    private float bottomSheetBackgroundRadius;

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
            binding.mapView.getMapWindow().getMap().setNightModeEnabled(true);
        }
    }

    private void setupBottomSheet() {
        // Setup vars for corner animation
        bottomSheetBackground = (GradientDrawable) ContextCompat.getDrawable(
                this, R.drawable.bottom_sheet_dialog_background);
        binding.bottomSheet.setBackground(bottomSheetBackground);
        if (bottomSheetBackground.getCornerRadii() != null) {
            bottomSheetBackgroundRadius = bottomSheetBackground.getCornerRadii()[0];
        } else {
            bottomSheetBackgroundRadius = 0;
        }

        // Setup bottom sheet states and gestures
        BottomSheetBehavior<FragmentContainerView> bottomSheetBehavior =
                BottomSheetBehavior.from(binding.bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (bottomState != -2) animateCorners(false);
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

        // Setup bottom navigation bar
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            //if (bottomState < 0) return false; // do not change state while animating

            if (item.getItemId() == R.id.map) {
                if (bottomState == 2 || bottomState == -3) animateCorners(true);
                if (bottomState != 0) bottomState = -1;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (item.getItemId() == R.id.home) {
                if (bottomState == 2 || bottomState == -3) animateCorners(true);
                if (bottomState != 1) bottomState = -2;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
            if (item.getItemId() == R.id.more) {
                if (bottomState != 2) {
                    bottomState = -3;
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                // TODO change fragment to options
            }
            return true;
        });
    }

    private void animateCorners(boolean fromZero) {
        float init, fin;
        ValueAnimator animator;

        if (fromZero) {
            fin = bottomSheetBackgroundRadius;
            init = 0;
        } else {
            init = bottomSheetBackgroundRadius;
            fin = 0;
        }

        animator = ValueAnimator.ofFloat(init, fin);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            bottomSheetBackground.setCornerRadii(
                    new float[]{animatedValue, animatedValue, 0, 0, 0, 0, 0, 0}
            );
            binding.bottomSheet.invalidate();
        });
        animator.start();
    }

    private void initMapKit() {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    protected void onStop() {
        binding.mapView.onStop();
        super.onStop();
    }

}