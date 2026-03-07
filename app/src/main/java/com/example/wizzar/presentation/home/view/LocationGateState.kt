package com.example.wizzar.presentation.home.view

sealed interface LocationGateState {
    data object Checking : LocationGateState
    data object PermissionRequired : LocationGateState
    data object ServiceDisabled : LocationGateState
    data object Ready : LocationGateState
}
