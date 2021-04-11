package com.dkb.bankapi.repositories

import com.dkb.bankapi.entities.Account
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : CrudRepository<Account, String> {
    fun findAllByTypeInOrderByType(type: List<String>): List<Account>?
}