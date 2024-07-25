package com.anjo.spacetradersservice.utils

import com.anjo.spacetradersservice.utils.Constants.BEARER_PREFIX

fun prepareToken(token: String): String {
    return "$BEARER_PREFIX $token"
}
