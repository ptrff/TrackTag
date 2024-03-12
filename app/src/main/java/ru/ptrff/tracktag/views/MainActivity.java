package ru.ptrff.tracktag.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

import ru.ptrff.tracktag.BuildConfig;
import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.adapters.TagsAdapter;
import ru.ptrff.tracktag.data.OptionActions;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.databinding.ActivityMainBinding;
import ru.ptrff.tracktag.interfaces.MainFragmentCallback;
import ru.ptrff.tracktag.models.Tag;


public class MainActivity extends AppCompatActivity implements MainFragmentCallback, TagsAdapter.TagEvents {

    private ActivityMainBinding binding;

    // Bottom sheet states used for animations and gestures
    // 0 - collapsed, 1 - half expanded, 2 - expanded
    // -1 - auto collapsed, -2 - auto half expanded, -3 - auto expanded
    private int bottomState = 1;

    // Current option fragment
    private OptionActions selectedOption = OptionActions.LIST;

    private BottomSheetBehavior<FragmentContainerView> bottomSheetBehavior;
    private final TypedValue typedValue = new TypedValue();
    private GradientDrawable bottomSheetBackground;
    private ValueAnimator statusColorAnimator;

    private HomeFragment homeFragment;

    private NavController navController;

    private Map map;
    private final List<MapObjectTapListener> placemarkTapListeners = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Restore User Data
            UserData.getInstance().restoreData(getPreferences(MODE_PRIVATE));

            // Init MapKit
            initMapKit();
        }

        // Init binding and set content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init views
        initMap();
        setupStatusBar();
        setupBottomSheet();


        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.bottom_sheet);
        navController = navHostFragment.getNavController();
        //TODO fix crash, remove object
        homeFragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    private void initMap() {
        // init map
        map = binding.mapView.getMapWindow().getMap();

        // Dark mode
        int nightModeFlags = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            map.setNightModeEnabled(true);
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
        bottomSheetBehavior =
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
                setBottomSheetState(0);
                homeFragment.scrollUp();
            }
            if (item.getItemId() == R.id.home) {
                setBottomSheetState(1);
                homeFragment.scrollUp();
            }
            if (item.getItemId() == R.id.more) {
                setBottomSheetState(2);
                setSelectedOption();
            } else {
                navController.navigateUp();
            }
            return true;
        });
    }

    private void setBottomSheetState(int state) {
        if (state == 1) {
            if (bottomState != 1) bottomState = -2;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            changeBottomSheetCorners(true);
            animateStatusBarColorChange(true, 500);
        }
        if (state == 0) {
            if (bottomState != 0) bottomState = -1;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            changeBottomSheetCorners(true);
            animateStatusBarColorChange(true, 500);
        }
        if (state == 2) {
            if (bottomState != 2) bottomState = -3;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            changeBottomSheetCorners(false);
            animateStatusBarColorChange(false, 250);
        }
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

    @Override
    public void onTagsLoaded(List<Tag> tags) {
        MapObjectCollection mapObjects = map.getMapObjects();
        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.ic_placeholder);

        IconStyle style = new IconStyle();
        style.setAnchor(new PointF(0.5f, 1f));
        style.setScale(0.06f);
        for (Tag tag : tags) {
            mapObjects.addPlacemark(placemarkMapObject -> {
                placemarkMapObject.setIcon(imageProvider);
                placemarkMapObject.setGeometry(
                        new Point(
                                tag.getLatitude(),
                                tag.getLongitude()
                        )
                );
                placemarkMapObject.setIconStyle(style);

                MapObjectTapListener listener = (mapObject, point) -> {
                    openTag(tag);
                    return true;
                };
                placemarkTapListeners.add(listener);
                placemarkMapObject.addTapListener(listener);
            });
        }
    }

    private void openTag(Tag tag) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("tag", tag);
        setBottomSheetState(2);
        navController.navigate(R.id.action_global_tagFragment, bundle);
    }

    @Override
    public void focusOnTag(Tag tag) {
        setBottomSheetState(0);
        binding.bottomNavigationView.setSelectedItemId(R.id.map);

        map.move(
                new CameraPosition(
                        new Point(tag.getLatitude(), tag.getLongitude()),
                        15f,
                        map.getCameraPosition().getAzimuth(),
                        map.getCameraPosition().getTilt()
                ),
                new Animation(Animation.Type.SMOOTH, 1f),
                null
        );
    }

    @Override
    public void performAction(OptionActions action) {
        switch (action) {
            case AUTH:
                selectedOption = OptionActions.AUTH;
                binding.bottomNavigationView.setSelectedItemId(R.id.more);
                break;
        }
    }

    private void setSelectedOption() {
        if (navController.getCurrentBackStack().getValue().size() >= 3) {
            navController.popBackStack();
        }
        switch (selectedOption) {
            case LIST:
                navController.navigate(R.id.action_global_moreFragment);
                break;
            case AUTH:
                navController.navigate(R.id.action_global_authFragment);
                break;
        }
        selectedOption = OptionActions.LIST;
    }

    @Override
    public void onFocusClick(Tag tag) {
        focusOnTag(tag);
    }

    @Override
    public void onLikeClick(Tag tag) {
        //TODO
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