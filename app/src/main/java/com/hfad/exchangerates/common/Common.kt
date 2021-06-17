package com.hfad.exchangerates.common

import com.hfad.exchangerates.`interface`.RetrofitServices
import com.hfad.exchangerates.retrofit.RetrofitClient

object Common {

    private val BASE_URL = "https://www.nbrb.by/API/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}