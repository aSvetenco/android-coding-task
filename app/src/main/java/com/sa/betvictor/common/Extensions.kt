package com.sa.betvictor.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

fun Context?.hideKeyboard(view: View?) {
    if (this != null && view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Context?.toast(resId: Int?, duration: Int = Toast.LENGTH_SHORT) {
    if (this != null && resId != null)
        Toast.makeText(this, resId, duration).show()
}