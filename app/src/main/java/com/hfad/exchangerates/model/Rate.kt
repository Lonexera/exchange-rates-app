package com.hfad.exchangerates.model

data class Rate(
    val Cur_Abbreviation: String? = null,
    val Cur_ID: Int? = null,
    val Cur_Name: String? = null,
    val Cur_OfficialRate: Double? = null,
    val Cur_Scale: Int? = null,
    val Date: String? = null
)