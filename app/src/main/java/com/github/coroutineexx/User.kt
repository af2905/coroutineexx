package com.github.coroutineexx

const val USER_DEFAULT_NAME = "Mark"
const val USER_DEFAULT_AGE = 35

data class User(val name: String, val age: Int) {

    companion object {

        fun getDefaultUser(): User = User(USER_DEFAULT_NAME, USER_DEFAULT_AGE)
    }
}