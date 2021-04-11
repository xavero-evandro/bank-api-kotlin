package com.dkb.bankapi.models

data class DepositRequest(
    val iban: String,
    val amount: Double
)