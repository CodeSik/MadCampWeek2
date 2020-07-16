package com.example.project1.ui.argame;

import com.google.ar.sceneform.rendering.ModelRenderable;

import java.lang.ref.WeakReference;
import java.util.function.BiFunction;

public class ModelLoader {
    private final WeakReference<ARGameFragment> owner;

    protected ModelLoader(WeakReference<ARGameFragment> owner) {
        this.owner = owner;
    }

    protected void loadModel(int resourceId) {
        if (owner.get() == null)
            return;

        ModelRenderable.builder()
                .setSource(owner.get().getActivity(), resourceId)
                .build()
                .handle(new BiFunction<ModelRenderable, Throwable, Object>() {
                    @Override
                    public Object apply(ModelRenderable modelRenderable, Throwable throwable) {
                        ARGameFragment fragment = owner.get();

                        if (fragment == null)
                            return null;
                        else if (throwable != null)
                            fragment.onException(throwable);
                        else {
                            if (fragment.wineBottle != null)
                                fragment.wineBottle.setRenderable(modelRenderable);

                            fragment.bottleRenderable = modelRenderable;
                            fragment.loadingComplete = true;
                        }

                        return null;
                    }
                });
    }
}
