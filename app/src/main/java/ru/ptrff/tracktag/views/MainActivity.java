package ru.ptrff.tracktag.views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
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
import ru.ptrff.tracktag.models.User;


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

    private NavController navController;

    private Map map;
    private final List<MapObjectTapListener> placemarkTapListeners = new ArrayList<>();
    private InputListener mapClickListener;
    private float[] clickPoint;
    private Point targetPoint;
    private CameraPosition cameraPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Restore User Data
            UserData.getInstance().restoreData(getPreferences(MODE_PRIVATE));

            // Init MapKit
            initMapKit();
        } else {
            bottomState = savedInstanceState.getInt("bottomState", 1);
            selectedOption = OptionActions.values()[savedInstanceState.getInt("selectedOption")];

            //cameraPosition
            Point cameraPoint = new Point(
                    savedInstanceState.getDouble("cameraPositionPointLat"),
                    savedInstanceState.getDouble("cameraPositionPointLon")
            );
            cameraPosition = new CameraPosition(
                    cameraPoint,
                    savedInstanceState.getFloat("cameraPositionZoom"),
                    savedInstanceState.getFloat("cameraPositionAzimuth"),
                    savedInstanceState.getFloat("cameraPositionTilt")
            );
        }

        // Init view binding and set content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init views
        initMap();
        setupStatusBar();
        setupBottomSheet();
        initNavController();
        initMapClick();
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

        // Restore cameraPosition
        if (cameraPosition != null) {
            map.move(cameraPosition);
        }
    }

    private void initNavController() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.bottom_sheet);
        navController = navHostFragment.getNavController();
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

        if (bottomState == 0) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (bottomState == 1) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
        if (bottomState == 2) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            changeBottomSheetCorners(false);
            animateStatusBarColorChange(false, 250);
        }

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
            }
            if (item.getItemId() == R.id.home) {
                setBottomSheetState(1);
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

    @Override
    public void setBottomPeekHeight(int height) {
        bottomSheetBehavior.setPeekHeight(height);
    }

    @Override
    public void setBottomSheetState(int state) {
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
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
        Point point = new Point(tag.getLatitude(), tag.getLongitude());
        focusOnPoint(point, 15f);
    }

    public void focusOnPoint(Point point, float zoom) {
        setBottomSheetState(0);
        binding.bottomNavigationView.setSelectedItemId(R.id.map);

        map.move(
                new CameraPosition(
                        point,
                        zoom,
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
            case SUBS:
                selectedOption = OptionActions.SUBS;
                binding.bottomNavigationView.setSelectedItemId(R.id.more);
                break;
        }
    }

    private void initMapClick() {
        binding.mapClickView.setOnTouchListener((v, event) -> {
            binding.mapClickView.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                clickPoint = new float[]{event.getX(), event.getY()};
            }
            return false;
        });

        PopupMenu menu = new PopupMenu(this, binding.targetPoint);
        menu.getMenuInflater().inflate(R.menu.map_menu, menu.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            menu.setForceShowIcon(true);
        }
        menu.setOnDismissListener(menu1 -> setTargetPointVisible(false));
        menu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.add) {
                setTargetPointVisible(false);
                setBottomSheetState(2);
                navController.navigate(R.id.action_global_addTagFragment);
            }
            if (id == R.id.zoom_in) {
                focusOnPoint(targetPoint, map.getCameraPosition().getZoom() + 2);
            }
            if (id == R.id.zoom_out) {
                focusOnPoint(targetPoint, map.getCameraPosition().getZoom() - 2);
            }
            return false;
        });

        mapClickListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                if (clickPoint != null) {
                    targetPoint = point;
                    binding.targetPoint.setTranslationX(
                            clickPoint[0] - (float) binding.targetPoint.getWidth() / 2
                    );
                    binding.targetPoint.setTranslationY(
                            clickPoint[1] - (float) binding.targetPoint.getHeight() / 2
                    );
                    setTargetPointVisible(true);
                    menu.show();
                    clickPoint = null;
                }
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
            }
        };
        map.addInputListener(mapClickListener);
    }

    private void setTargetPointVisible(boolean visible) {
        float start = binding.targetPoint.getAlpha();
        float end;
        if (visible) {
            end = 1f;
        } else {
            end = 0f;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> binding.targetPoint.setAlpha((float) animation.getAnimatedValue()));
        animator.start();
    }

    @SuppressLint("RestrictedApi")
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
            case SUBS:
                navController.navigate(R.id.action_global_subsFragment);
                break;
        }
        selectedOption = OptionActions.LIST;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("bottomState", bottomState);
        outState.putInt("selectedOption", selectedOption.ordinal());

        //cameraPosition
        cameraPosition = map.getCameraPosition();
        outState.putFloat("cameraPositionAzimuth", cameraPosition.getAzimuth());
        outState.putFloat("cameraPositionTilt", cameraPosition.getTilt());
        outState.putFloat("cameraPositionZoom", cameraPosition.getZoom());
        outState.putDouble("cameraPositionPointLat", cameraPosition.getTarget().getLatitude());
        outState.putDouble("cameraPositionPointLon", cameraPosition.getTarget().getLongitude());
    }

    @Override
    public void onLikeClick(Tag tag) {
        // TODO
    }

    @Override
    public void onSubscribeClick(Tag tag) {
        UserData data = UserData.getInstance();
        User user = tag.getUser();
        if (!data.isSubscribed(user)) {
            data.addSub(user);
        } else {
            data.removeSub(user);
        }
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