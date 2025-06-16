package com.example.xucasalonapp.ui.login.ui

import com.example.xucasalonapp.ui.login.data.LoginData

data class LoginUiState (
    val loginEnable : Boolean = false,
    val loginMessage : String = "",
    val loginChecked: Boolean = false,
    val loginData: LoginData = LoginData("","")
)
