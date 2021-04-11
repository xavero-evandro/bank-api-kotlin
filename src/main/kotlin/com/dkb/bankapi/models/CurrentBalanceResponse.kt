package com.dkb.bankapi.models

data class CurrentBalanceResponse(
    val iban: String,
    val currentBalance: Double
)