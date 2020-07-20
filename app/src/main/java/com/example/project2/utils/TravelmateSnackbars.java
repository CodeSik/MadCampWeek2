package com.example.project2.utils;


import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public interface TravelmateSnackbars {

    static Snackbar createSnackBar(View view, int message, int duration) {
        return Snackbar.make(view, message, duration);
    }
    static Snackbar createSnackBar(View view, String message, int duration) {
        return Snackbar.make(view, message, duration);
    }
}