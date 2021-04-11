package com.dkb.bankapi.repositories

import com.dkb.bankapi.entities.AccountTransactionHistory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountTransactionHistoryRepository : CrudRepository<AccountTransactionHistory, Long> {
    fun findByIban(iban: String): List<AccountTransactionHistory>
}