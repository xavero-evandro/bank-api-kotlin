package com.dkb.bankapi.services

import org.springframework.stereotype.Service

@Service
class BICGenerator {

    //Fix to DKB BIC value
    fun generateBIC(): String {
        return "BYLADEM1001"
    }
}