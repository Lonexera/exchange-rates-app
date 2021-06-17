package com.hfad.exchangerates.`interface`

import com.hfad.exchangerates.model.CurRate
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServices {

    @GET("exrates/rates")
    fun getRatesList(@Query("ondate") date: String,
                     @Query("periodicity") periodicity: Int): Call<MutableList<CurRate>>

}