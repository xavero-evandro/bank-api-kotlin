package com.dkb.bankapi.services

import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class IBANGenerator {

    fun generateNewIban(): String {
        val iban = ""
        val germanyCountryCode = "DE"
        val checkNumber = Random.nextInt(0, 99).toString().padEnd(2, '0')
        val bankIdentifier = Random.nextLong(0, 99999999).toString().padEnd(8, '0')
        val accountNumber = Random.nextLong(0, 99999999).toString().padEnd(8, '0')
        val accountVerifier = Random.nextInt(0, 99).toString().padEnd(2, '0')
        return iban
            .plus(germanyCountryCode)
            .plus(checkNumber)
            .plus(bankIdentifier)
            .plus(accountNumber)
            .plus(accountVerifier)
    }

}