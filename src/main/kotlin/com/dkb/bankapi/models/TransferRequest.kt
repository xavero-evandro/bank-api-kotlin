package com.dkb.bankapi.models

data class TransferRequest(
    val fromIban: String,
    val toIban: String,
    val amount: Double
)