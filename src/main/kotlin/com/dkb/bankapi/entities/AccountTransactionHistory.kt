package com.dkb.bankapi.entities

import com.beust.klaxon.Json
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class AccountTransactionHistory(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @Json var id: Long? = null,
    @Json var iban: String,
    @Json var action: String,
    @Json var amount: Double,
    @Json var date: String,
)