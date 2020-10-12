package com.sa.betvictor.app

import android.app.Application
import com.sa.betvictor.app.di.DependenciesContainer

class App : Application() {
    val container = DependenciesContainer(this)
}