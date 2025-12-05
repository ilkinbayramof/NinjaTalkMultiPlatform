package com.ilkinbayramov.ninjatalk.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class MviViewModel<Event, State, Effect>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    protected fun setState(reducer: State.() -> State) {
        _uiState.update { current -> current.reducer() }
    }

    protected fun sendEffect(builder: () -> Effect) {
        viewModelScope.launch { _effect.send(builder()) }
    }

    abstract fun onEvent(event: Event)
}
