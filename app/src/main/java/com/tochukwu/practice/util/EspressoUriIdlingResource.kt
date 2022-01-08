package com.tochukwu.practice.util

import androidx.test.espresso.idling.net.UriIdlingResource

object EspressoUriIdlingResource {
    private const val RESOURCE = "DATA_LOADED"
    private const val TIMEOUT_MS = 2000L
    @JvmField
    val uriIdlingResource = UriIdlingResource(RESOURCE, TIMEOUT_MS)

    fun beginLoad(){
        uriIdlingResource.beginLoad(BASE_URL)
    }

    fun endLoad(){
        uriIdlingResource.endLoad(BASE_URL)
    }

}
