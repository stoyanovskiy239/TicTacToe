package com.stoyanovskiy.tictactoe.model

data class RoleRequest(val requestedSymbol: Symbol)
data class RoleResponse(val accepted: Boolean, val assignedSymbol: Symbol)
