package com.dkb.bankapi.entities

import com.beust.klaxon.Json
import com.dkb.bankapi.services.BICGenerator
import com.dkb.bankapi.services.IBANGenerator
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

enum class AccountType(val value: String) {
    CHECKING("checking"),
    SAVINGS("savings"),
    PRIVATELOAN("privateloan")
}

@Entity
data class Account(
    @Id @Json var iban: String,
    @Json var bic: String,
    @Json var name: String,
    @Json var type: String = AccountType.CHECKING.value,
    @Json var referenceAccount: String = "",
    @Json var balance: Double = 0.0,
    @Json var isLocked: Boolean = false,
)