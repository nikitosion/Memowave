package com.memowave.app.domain.usecase.auth

sealed class ChangePasswordError(message: String) : Exception(message) {
    object WrongCurrentPassword : ChangePasswordError("Wrong current password")
    object Forbidden : ChangePasswordError("Action not allowed")
    object ServerError : ChangePasswordError("Server error")
    object SamePassword : ChangePasswordError("New password must differ from current")
    object InvalidPassword : ChangePasswordError("Password does not meet requirements")
    object Unknown : ChangePasswordError("Unknown error")
}
