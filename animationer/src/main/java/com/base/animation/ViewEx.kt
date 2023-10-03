package com.base.animation

import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

fun View.findFragmentOfGivenView(): Fragment? {
    var fragment: Fragment? = null
    tryCatch({
    }, { fragment = FragmentManager.findFragment(this) })
    return fragment
}

fun View.getCurrentLifeCycleOwner(): LifecycleOwner? {
    return findFragmentOfGivenView() ?: getFragmentActivity()
}

fun View.getFragmentActivity(): FragmentActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is FragmentActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}