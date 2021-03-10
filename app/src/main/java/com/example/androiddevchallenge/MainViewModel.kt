package com.example.androiddevchallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var debug = MutableLiveData(false)
    var startPoint = MutableLiveData(0L)
    var elapsedTime = MutableLiveData(0L)
    var delta = MutableLiveData(0)
    var runs = MutableLiveData(0)
    var num = MutableLiveData(0)
    var incNum = MutableLiveData(0)
    var decNum = MutableLiveData(0)

    fun toggleDebug() {
        debug.value = debug.value?.not() ?: false
    }

    private val _clickedUp = MutableLiveData(true)
    val clickedUp: LiveData<Boolean>
        get() = _clickedUp

    private val _finished = MutableLiveData(false)
    val finished: LiveData<Boolean>
        get() = _finished

    private val _initialValue = MutableLiveData(0)
    val initialValue: LiveData<Int>
        get() = _initialValue

    private val _timerRunning = MutableLiveData(false)
    val timerRunning: LiveData<Boolean>
        get() = _timerRunning

    private val _secondsLeft = MutableLiveData(0)
    val timeRemaining: LiveData<Int>
        get() = _secondsLeft

    private fun clickedUp() {
        _clickedUp.value = true
    }

    private fun clickedDown() {
        _clickedUp.value = false
    }

    fun startTimer() {
        _secondsLeft.value = _initialValue.value
        _timerRunning.value = true
    }

    fun showFinished() {
        _finished.value = true
        stopTimer()
    }

    fun resetFinished() {
        _finished.value = false
    }

    fun stopTimer() {
        _timerRunning.value = false
    }

    fun incrementTenMinutes() {
        setTimer(_initialValue.value?.plus(600) ?: 600)
        clickedUp()
    }

    fun incrementTenSeconds() {
        setTimer(_initialValue.value?.plus(10) ?: 10)
        clickedUp()
    }

    fun decrementTenMinutes() {
        if ((_initialValue.value ?: 0) >= 600) {
            setTimer(_initialValue.value?.minus(600) ?: 0)
            clickedDown()
        }
    }

    fun decrementTenSeconds() {
        if ((_initialValue.value ?: 0) >= 10) {
            setTimer(_initialValue.value?.minus(10) ?: 0)
            clickedDown()
        }
    }

    fun incrementSeconds() {
        setTimer(_initialValue.value?.plus(1) ?: 1)
        clickedUp()
    }

    fun decrementSeconds() {
        if ((_initialValue.value ?: 0) > 0) {
            setTimer(_initialValue.value?.minus(1) ?: 0)
            clickedDown()
        }
    }

    fun incrementMinutes() {
        setTimer(_initialValue.value?.plus(60) ?: 60)
        clickedUp()
    }

    fun decrementMinutes() {
        if ((_initialValue.value ?: 0) >= 60) {
            setTimer(_initialValue.value?.minus(60) ?: 0)
            clickedDown()
        }
    }

    fun resetCountdown() {
        _timerRunning.value = false
        _initialValue.value = 0
        _secondsLeft.value = 0
    }

    fun setTimer(seconds: Int) {
        if (seconds >= 0) _initialValue.value = seconds
    }

    fun updateSecondsLeft(seconds: Int) {
        if (seconds >= 0) _secondsLeft.value = seconds
    }
}