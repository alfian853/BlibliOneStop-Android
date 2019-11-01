package com.gdn.android.onestop.base;

import androidx.fragment.app.Fragment
import dagger.MapKey
import kotlin.reflect.KClass


@MustBeDocumented
@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FragmentKey (
    val value : KClass<out Fragment>
)
