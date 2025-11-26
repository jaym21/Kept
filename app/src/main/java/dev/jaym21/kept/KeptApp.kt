package dev.jaym21.kept

import android.app.Application
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KeptApp: Application() {

    override fun onCreate() {
        super.onCreate()
        PDFBoxResourceLoader.init(this)
    }
}