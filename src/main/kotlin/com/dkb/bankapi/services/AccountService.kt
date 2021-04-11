package com.dkb.bankapi.services

import com.dkb.bankapi.entities.Account
import com.dkb.bankapi.entities.AccountTransactionHistory
import com.dkb.bankapi.entities.AccountType
import com.dkb.bankapi.models.TransferResponse
import com.dkb.bankapi.repositories.AccountRepository
import com.dkb.bankapi.repositories.AccountTransactionHistoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AccountService(
    private val repository: AccountRepository,
    private val transactionHistoryService: TransactionHistoryService
) {

    @Autowired
    val ibanGenerator = IBANGenerator()

    @Autowired
    val bicGenerator = BICGenerator()

    fun getAllAccounts(): List<Account> = repository.findAll().toList()

    fun createNewAccount(account: Account): Account? {
        val result = when (account.type) {
            AccountType.SAVINGS.value -> {
                val checkingAccount = createAccount(account, AccountType.CHECKING)
                createSavingsAccount(checkingAccount)
            }
            AccountType.PRIVATELOAN.value -> {
                createAccount(account, AccountType.PRIVATELOAN)
            }
            else -> {
                createAccount(account, AccountType.CHECKING)
            }
        }

        transactionHistoryService.addTransactionHistory(result.iban, "Account of type ${account.type} created")
        return result
    }

    private fun createAccount(account: Account, type: AccountType): Account {
        account.iban = ibanGenerator.generateNewIban()
        account.bic = bicGenerator.generateBIC()
        account.type = type.value
        account.referenceAccount = ""
        return repository.save(account)
    }

    private fun createSavingsAccount(account: Account): Account {
        val savingsAccount = Account(
            iban = ibanGenerator.generateNewIban(),
            bic = bicGenerator.generateBIC(),
            name = account.name,
            type = AccountType.SAVINGS.value,
            referenceAccount = account.iban,
            balance = account.balance
        )
        transactionHistoryService.addTransactionHistory(
            savingsAccount.iban,
            "Account of type ${savingsAccount.type} created"
        )
        return repository.save(savingsAccount)
    }

    fun depositMoney(iban: String, amount: Double): Account {
        val account = repository.findById(iban).get()
        account.balance = account.balance.plus(amount)
        transactionHistoryService.addTransactionHistory(account.iban, "Deposit of $amount", amount)
        return repository.save(account)
    }

    fun lockUnlockAccount(iban: String, lock: Boolean): Account? {
        val lockAccount = repository.findById(iban).get()
        lockAccount.isLocked = lock
        val locked = if (lock) "Locked" else "Unlocked"
        transactionHistoryService.addTransactionHistory(lockAccount.iban, "Account $locked")
        return repository.save(lockAccount)
    }

    fun transferMoney(fromIban: String, toIban: String, amount: Double): TransferResponse {
        val fromAccount = repository.findById(fromIban).get()
        val toAccount = repository.findById(toIban).get()

        when (fromAccount.type) {
            AccountType.SAVINGS.value -> {
                if (fromAccount.referenceAccount !== toAccount.iban)
                    throw ResponseStatusException(
                        HttpStatus.NOT_ACCEPTABLE,
                        "Transfer from savings to checking only possible between references accounts"
                    )
                transferFromSavingsToChecking(savingsAccount = fromAccount, checkingAccount = toAccount, amount)
            }
            AccountType.CHECKING.value -> {
                transferFromChecking(fromAccount, toAccount, amount)
            }
            else -> {
                throw ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE,
                    "Withdrawal from private loan account is not possible"
                )
            }
        }
        transactionHistoryService.addTransactionHistory(
            fromAccount.iban,
            "Transferred amount of $amount to account: ${toAccount.iban}",
            amount
        )
        return TransferResponse(fromAccount.iban, toAccount.iban, amount, "Transfer done successfully")
    }

    private fun transferFromSavingsToChecking(savingsAccount: Account, checkingAccount: Account, amount: Double) {
        savingsAccount.balance = savingsAccount.balance.minus(amount)

        if (savingsAccount.balance < 0)
            throw ResponseStatusException(
                HttpStatus.NOT_ACCEPTABLE,
                "Savings Balance not available"
            )

        checkingAccount.balance = checkingAccount.balance.plus(amount)
        repository.save(checkingAccount)
        repository.save(savingsAccount)
    }

    private fun transferFromChecking(fromAccount: Account, toAccount: Account, amount: Double) {
        fromAccount.balance = fromAccount.balance.minus(amount)

        if (fromAccount.balance < 0)
            throw ResponseStatusException(
                HttpStatus.NOT_ACCEPTABLE,
                "Balance not available"
            )

        toAccount.balance = toAccount.balance.plus(amount)
        repository.save(toAccount)
        repository.save(fromAccount)

    }

    fun getBalance(iban: String): Account {
        return repository.findById(iban).get()
    }

    fun getAccountsByType(type: List<String>): List<Account>? {
        return repository.findAllByTypeInOrderByType(type)
    }

}