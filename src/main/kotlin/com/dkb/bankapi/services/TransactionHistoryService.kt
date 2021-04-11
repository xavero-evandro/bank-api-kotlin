package com.dkb.bankapi.services

import com.dkb.bankapi.entities.Account
import com.dkb.bankapi.entities.AccountTransactionHistory
import com.dkb.bankapi.repositories.AccountTransactionHistoryRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TransactionHistoryService(private val repository: AccountTransactionHistoryRepository) {

    fun addTransactionHistory(iban: String, message: String, amount: Double = 0.0) {
        val transaction = AccountTransactionHistory(
            iban = iban,
            action = message,
            amount = amount,
            date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("d/M/Y H:m:ss")).toString()
        )
        repository.save(transaction)
    }

    fun getAccountHistory(iban: String): List<AccountTransactionHistory> {
        return repository.findByIban(iban)

    }
}