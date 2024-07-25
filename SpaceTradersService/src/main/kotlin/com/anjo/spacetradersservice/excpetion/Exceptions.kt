package com.anjo.spacetradersservice.excpetion

sealed class SpaceTraderException(message: String) : RuntimeException(message)

class NonSpaceTraderValidException(message: String) : SpaceTraderException(message)
class NotFoundTokenException(message: String) : SpaceTraderException(message)