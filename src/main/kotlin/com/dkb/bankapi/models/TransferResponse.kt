package com.dkb.bankapi.models

data class TransferResponse(
    val transferredFrom: String,
    val transferredTo: String,
    val amount: Double,
    val message: String,
)