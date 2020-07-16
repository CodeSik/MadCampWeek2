package com.example.project1.ui.argame;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project1.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.lang.ref.WeakReference;

public class ARGameFragment extends Fragment {
    private static final int RC_PERMISSIONS = 1;
    private static LinearLayout.LayoutParams lp;
    private ArSceneView arSceneView;
    private GestureDetector gestureDetector;
    private boolean cameraPermissionRequested;
    private AnchorNode anchorNode;
    private ModelLoader modelLoader;
    private ImageView selectedImageView;
    protected boolean bottlePlaced;
    protected boolean loadingComplete;
    protected ModelRenderable bottleRenderable;
    protected RotatingNode wineBottle;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_argame, container, false);

        if (!PermissionUtils.checkIsSupportedDeviceOrFinish(this.getActivity()))
            return root;

        lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(100, 0, 0, 0);
        arSceneView = root.findViewById(R.id.ar_scene_view);
        modelLoader = new ModelLoader(new WeakReference<>(this));
        modelLoader.loadModel(R.raw.champagne);
        initializeGallery(root);
        gestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onSingleTap(e);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        arSceneView.getScene().setOnTouchListener(new Scene.OnTouchListener() {
            @Override
            public boolean onSceneTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if (!bottlePlaced)
                    return gestureDetector.onTouchEvent(motionEvent);

                return false;
            }
        });

        PermissionUtils.requestCameraPermission(this.getActivity(), RC_PERMISSIONS);

        return root;
    }

    private void initializeGallery(View root) {
        LinearLayout gallery = root.findViewById(R.id.ar_gallery);

        ImageView champagne = generateImageViewFromResource(R.drawable.thumbnail_champagne, R.raw.champagne);
        ImageView soju = generateImageViewFromResource(R.drawable.thumbnail_soju, R.raw.soju);
        ImageView cola = generateImageViewFromResource(R.drawable.thumbnail_cola, R.raw.cola);
        ImageView mangmang = generateImageViewFromResource(R.drawable.thumbnail_mangmang, R.raw.mangmang);
        ImageView knife = generateImageViewFromResource(R.drawable.thumbnail_knife, R.raw.knife);

        gallery.addView(champagne);
        gallery.addView(soju);
        gallery.addView(cola);
        gallery.addView(mangmang);
        gallery.addView(knife);

        selectedImageView = champagne;
        selectedImageView.setBackgroundResource(R.drawable.imageview_border);
    }

    private ImageView generateImageViewFromResource(int thumbnailResourceID, int modelResourceID) {
        ImageView imageView = new ImageView(this.getContext());
        imageView.setImageResource(thumbnailResourceID);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(lp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARGameFragment.this.loadModel(modelResourceID);

                selectedImageView.setBackgroundResource(0);
                imageView.setBackgroundResource(R.drawable.imageview_border);
                selectedImageView = imageView;
            }
        });

        return imageView;
    }

    private void loadModel(int resourceId) {
        modelLoader.loadModel(resourceId);
    }

    private void onSingleTap(MotionEvent e) {
        if (!loadingComplete)
            return;

        Frame frame = arSceneView.getArFrame();

        if (frame != null && !bottlePlaced && placeBottleSuccessful(e, frame))
            bottlePlaced = true;

    }

    private boolean placeBottleSuccessful(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();

                if (trackable instanceof Plane && ((Plane)trackable).isPoseInPolygon(hit.getHitPose())) {
                    Anchor anchor = hit.createAnchor();
                    anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arSceneView.getScene());
                    wineBottle = new RotatingNode();
                    wineBottle.setRenderable(bottleRenderable);
                    anchorNode.addChild(wineBottle);
                    return true;
                }
            }
        }
        return false;
    }

    public void onException(Throwable throwable) {
        Toast.makeText(this.getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (arSceneView == null)
            return;

        if (arSceneView.getSession() == null) {
            try {
                Config.LightEstimationMode lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR;
                Session session = cameraPermissionRequested ?
                        PermissionUtils.createArSessionWithInstallRequest(this.getActivity(), lightEstimationMode) :
                        PermissionUtils.createArSessionNoInstallRequest(this.getActivity(), lightEstimationMode);
                if (session == null) {
                    cameraPermissionRequested = PermissionUtils.hasCameraPermission(this.getActivity());
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                PermissionUtils.handleSessionException(this.getActivity(), e);
            }
        }
        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            PermissionUtils.displayError(this.getContext(), "Unable to get camera", ex);
            this.getActivity().finish();
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (arSceneView != null)
            arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arSceneView != null)
            arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!PermissionUtils.hasCameraPermission(this.getActivity())) {

            if (!PermissionUtils.shouldShowRequestPermissionRationale(this.getActivity()))
                PermissionUtils.launchPermissionSettings(this.getActivity());
            else
                Toast.makeText(this.getActivity(), "Camera permission is needed to run this fragment", Toast.LENGTH_LONG).show();
            this.getActivity().finish();
        }
    }
}
