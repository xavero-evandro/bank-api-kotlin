package com.dkb.bankapi.models

data class LockAccountResponse(
    val iban: String,
    val locked: Boolean,
    val message: String
)