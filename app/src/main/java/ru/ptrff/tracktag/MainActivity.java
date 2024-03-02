package ru.ptrff.tracktag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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

    private final TypedValue typedValue = new TypedValue();
    private GradientDrawable bottomSheetBackground;
    private ValueAnimator statusColorAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) initMapKit();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        applyMapStyle();
        setupStatusBar();
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
        // Setup bottom sheet height (original height - status bar height)
        binding.bottomSheet.post(() -> {
            ViewGroup.LayoutParams params = binding.bottomSheet.getLayoutParams();
            params.height = binding.bottomSheet.getMeasuredHeight() - getStatusBarHeight();
            binding.bottomSheet.setLayoutParams(params);
        });

        // Setup bottom sheet background for animations
        bottomSheetBackground = (GradientDrawable) ContextCompat.getDrawable(
                this,
                R.drawable.bottom_sheet_dialog_background
        );

        // Setup bottom sheet states and gestures
        BottomSheetBehavior<FragmentContainerView> bottomSheetBehavior =
                BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    changeBottomSheetCorners(false);
                    animateStatusBarColorChange(false, 250);
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
                if (newState == BottomSheetBehavior.STATE_DRAGGING
                        && bottomSheet.getBackground() != bottomSheetBackground) {
                    changeBottomSheetCorners(true);
                    animateStatusBarColorChange(true, 500);
                }
            }

            // Setup gestures
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (bottomState < 0) return; // do not animate on auto state

                if (slideOffset > 0.65f) {
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
                if (bottomState != 0) bottomState = -1;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                changeBottomSheetCorners(true);
                animateStatusBarColorChange(true, 500);
            }
            if (item.getItemId() == R.id.home) {
                if (bottomState != 1) bottomState = -2;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                changeBottomSheetCorners(true);
                animateStatusBarColorChange(true, 500);
            }
            if (item.getItemId() == R.id.more) {
                if (bottomState != 2) bottomState = -3;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                changeBottomSheetCorners(false);
                animateStatusBarColorChange(false, 250);
                // TODO change fragment to options
            }
            return true;
        });
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void changeBottomSheetCorners(boolean rounded) {
        if (rounded) {
            binding.bottomSheet.setBackground(bottomSheetBackground);
        } else {
            getTheme().resolveAttribute(
                    com.google.android.material.R.attr.colorSurfaceContainer,
                    typedValue,
                    true
            );
            binding.bottomSheet.setBackgroundColor(typedValue.data);
        }
    }

    private void animateStatusBarColorChange(boolean transparent, int duration) {
        Window window = getWindow();
        int from = window.getStatusBarColor();
        int to;
        if (transparent) {
            to = Color.TRANSPARENT;
        } else {
            getTheme().resolveAttribute(
                    com.google.android.material.R.attr.colorSurfaceContainer,
                    typedValue,
                    true
            );
            to = typedValue.data;
        }

        if (from == to) return;

        if (statusColorAnimator != null && statusColorAnimator.isRunning())
            statusColorAnimator.cancel();

        statusColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        statusColorAnimator.setDuration(duration);
        statusColorAnimator.addUpdateListener(
                animator -> window.setStatusBarColor((int) animator.getAnimatedValue())
        );

        statusColorAnimator.start();
    }

    private int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return statusBarHeight - contentViewTop;
    }

    private void initMapKit() {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        binding.mapView.onStart();
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        binding.mapView.onStop();
        super.onStop();
    }

}