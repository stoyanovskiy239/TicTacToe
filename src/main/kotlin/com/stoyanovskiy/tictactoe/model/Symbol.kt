package com.stoyanovskiy.tictactoe.model

enum class Symbol {
    X, O, NONE;

    fun opponent() = when (this) {
        X -> O
        O -> X
        NONE -> NONE
    }

    override fun toString(): String = when (this) {
        X -> "X"
        O -> "O"
        NONE -> " "
    }
}
