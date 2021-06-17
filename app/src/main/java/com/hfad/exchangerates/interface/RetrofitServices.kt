package com.hfad.exchangerates.`interface`

import com.hfad.exchangerates.model.Rate
import com.hfad.exchangerates.model.RateShort
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitServices {

    @GET("exrates/rates")
    fun getRatesList(@Query("ondate") date: String,
                     @Query("periodicity") periodicity: Int): Call<MutableList<Rate>>

    @GET("exrates/rates/dynamics/{cur_id}")
    fun getRatesShortList(@Path("cur_id") curId: String,
                          @Query("startdate") startDate: String,
                          @Query("enddate") endDate: String): Call<MutableList<RateShort>>

}