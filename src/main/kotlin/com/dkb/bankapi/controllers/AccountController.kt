package com.dkb.bankapi.controllers

import com.dkb.bankapi.entities.Account
import com.dkb.bankapi.entities.AccountTransactionHistory
import com.dkb.bankapi.models.LockAccountResponse
import com.dkb.bankapi.models.*
import com.dkb.bankapi.services.AccountService
import com.dkb.bankapi.services.TransactionHistoryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/")
class AccountController(
    private val accountService: AccountService,
    private val transactionHistoryService: TransactionHistoryService,
) {

    @GetMapping("accounts")
    @ResponseBody
    fun getAllAccounts(): List<Account>? {
        return accountService.getAllAccounts()
    }

    @GetMapping("accounts", params = ["type"])
    @ResponseBody
    fun getAccountsByType(@RequestParam type: List<String>?): List<Account>? {
        return accountService.getAccountsByType(type!!)
    }

    @GetMapping("account/balance/{iban}")
    @ResponseBody
    fun getCurrentBalance(@PathVariable iban: String): CurrentBalanceResponse {
        if (iban.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide an Iban")
        val account = accountService.getBalance(iban)
        return CurrentBalanceResponse(account.iban, account.balance)
    }

    @GetMapping("account/history/{iban}")
    @ResponseBody
    fun getAccountHistory(@PathVariable iban: String): List<AccountTransactionHistory> {
        return transactionHistoryService.getAccountHistory(iban)
    }

    @PostMapping("account")
    @ResponseBody
    fun createNewAccount(@RequestBody account: Account): Account? {
        if (account.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is missing")
        return accountService.createNewAccount(account)
    }

    @PostMapping("account/deposit")
    @ResponseBody
    fun deposit(@RequestBody deposit: DepositRequest): Account {
        if (deposit.iban.isEmpty() || deposit.amount <= 0.0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide Iban and Amount")
        return accountService.depositMoney(deposit.iban, deposit.amount)
    }

    @PostMapping("account/transfer")
    @ResponseBody
    fun deposit(@RequestBody transfer: TransferRequest): TransferResponse {
        if (transfer.fromIban.isEmpty() || transfer.toIban.isEmpty() || transfer.amount <= 0.0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide Iban, Amount and Type to deposit")
        return accountService.transferMoney(
            transfer.fromIban,
            transfer.toIban,
            transfer.amount
        )
    }

    @PostMapping("account/lock")
    @ResponseBody
    fun lockAccount(@RequestBody account: Account): LockAccountResponse {
        val lockedAccount = accountService.lockUnlockAccount(account.iban, true)
        if (lockedAccount === null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not found")
        }
        return LockAccountResponse(lockedAccount.iban, true, "Account Locked Successfully")
    }

    @PostMapping("account/unlock")
    @ResponseBody
    fun unlockAccount(@RequestBody account: Account): LockAccountResponse {
        val lockedAccount = accountService.lockUnlockAccount(account.iban, false)
        if (lockedAccount === null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not found")
        }
        return LockAccountResponse(lockedAccount.iban, false, "Account Unlocked Successfully")
    }

}