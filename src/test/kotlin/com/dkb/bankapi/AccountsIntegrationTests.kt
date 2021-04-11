package com.dkb.bankapi

import com.beust.klaxon.JsonObject
import com.dkb.bankapi.entities.Account
import com.dkb.bankapi.entities.AccountType
import com.dkb.bankapi.models.CurrentBalanceResponse
import com.dkb.bankapi.models.LockAccountResponse
import com.dkb.bankapi.models.TransferResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsIntegrationTests(
    @Autowired private val restTemplate: TestRestTemplate
) {

    private fun createAccount(name: String, type: AccountType): ResponseEntity<Account>? {
        val account = JsonObject()
        account["name"] = name
        account["type"] = type.value
        return restTemplate.postForEntity("/api/v1/account", account, Account::class.java)
    }

    private fun depositMoney(iban: String, amount: Double): ResponseEntity<Account>? {
        val depositRequest = JsonObject()
        depositRequest["iban"] = iban
        depositRequest["amount"] = amount
        return restTemplate.postForEntity("/api/v1/account/deposit", depositRequest, Account::class.java)
    }

    @Test
    fun `Create a new Account`() {
        val response = createAccount("Evandro 0", AccountType.CHECKING)
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(true, response?.hasBody())
    }

    @Test
    fun `Deposit Money from checking account to another checking account `() {
        //Given
        val responseAccount = createAccount("Evandro 1", AccountType.CHECKING)
        //When
        val response = responseAccount?.body?.iban?.let { depositMoney(it, 200.0) }
        //Then
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(200.0, response?.body?.balance)
    }

    @Test
    fun `Show current balance of the specific bank account`() {
        //Given
        val responseAccount = createAccount("Evandro 2", AccountType.CHECKING)
        responseAccount?.body?.iban?.let { depositMoney(it, 400.0) }
        //When
        val response =
            restTemplate.getForEntity(
                "/api/v1/account/balance/${responseAccount?.body?.iban}",
                CurrentBalanceResponse::class.java
            )
        //Then
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(400.0, response?.body?.currentBalance)
    }

    @Test
    fun `Show a transaction history`() {
        //Given
        val accountResponse = createAccount("Evandro 6", AccountType.CHECKING)
        accountResponse?.body?.iban?.let { depositMoney(it, 300.0) }
        //When
        val response =
            restTemplate.getForEntity("/api/v1/account/history/${accountResponse?.body?.iban}", List::class.java)
        //Then
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(2, response?.body?.size)
    }

    @Test
    fun `Transfer some money across two bank accounts CHECKING - CHECKING`() {
        //Given
        val accountResponse = createAccount("Evandro 7", AccountType.CHECKING)
        val accountResponse2 = createAccount("Evandro 8", AccountType.CHECKING)
        accountResponse?.body?.iban?.let { depositMoney(it, 350.0) }
        //When
        val transfer = JsonObject()
        transfer["fromIban"] = accountResponse?.body?.iban
        transfer["toIban"] = accountResponse2?.body?.iban
        transfer["amount"] = 300.0
        val response = restTemplate.postForEntity("/api/v1/account/transfer", transfer, TransferResponse::class.java)
        //Then
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(accountResponse?.body?.iban, response?.body?.transferredFrom)
        Assertions.assertEquals(accountResponse2?.body?.iban, response?.body?.transferredTo)
        Assertions.assertEquals(300.0, response?.body?.amount)
        Assertions.assertEquals("Transfer done successfully", response?.body?.message)
    }

    @Test
    fun `Lock an Account`() {
        //Given
        val accountResponse = createAccount("Evandro 9", AccountType.CHECKING)
        //When
        val lock = JsonObject()
        lock["iban"] = accountResponse?.body?.iban
        val response = restTemplate.postForEntity("/api/v1/account/lock", lock, LockAccountResponse::class.java)
        //Then
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(true, response?.body?.locked)
        Assertions.assertEquals("Account Locked Successfully", response?.body?.message)
    }

    @Test
    fun `UnLock an Account`() {
        //Given
        val accountResponse = createAccount("Evandro 9", AccountType.CHECKING)
        //When
        val lock = JsonObject()
        lock["iban"] = accountResponse?.body?.iban
        val response = restTemplate.postForEntity("/api/v1/account/unlock", lock, LockAccountResponse::class.java)
        //Then
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(false, response?.body?.locked)
        Assertions.assertEquals("Account Unlocked Successfully", response?.body?.message)
    }

    @Test
    fun `Filter accounts by account type`() {
        //Given
        createAccount("Evandro 3", AccountType.SAVINGS)
        createAccount("Evandro 4", AccountType.PRIVATELOAN)
        val response = restTemplate.getForEntity("/api/v1/accounts?type=checking,savings,privateloan", List::class.java)
        Assertions.assertEquals(HttpStatus.OK, response?.statusCode)
        Assertions.assertEquals(7, response?.body?.size)
    }

    @Test
    fun `Get all Accounts `() {
        //When
        val response = restTemplate.getForEntity("/api/v1/accounts", List::class.java)
        //Then
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(8, response.body?.size)
    }

}