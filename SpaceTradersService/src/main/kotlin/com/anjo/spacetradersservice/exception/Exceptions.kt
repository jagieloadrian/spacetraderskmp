package com.anjo.spacetradersservice.exception

sealed class SpaceTraderException(message: String) : RuntimeException(message)

class NonSpaceTraderValidException(message: String) : SpaceTraderException(message)
class NotFoundTokenException(message: String) : SpaceTraderException(message)
class NonValidActionException(message:String) : SpaceTraderException(message)