package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

enum class BuzzType(val pattern: LongArray) {
    CORRECT(CORRECT_BUZZ_PATTERN),
    GAME_OVER(GAME_OVER_BUZZ_PATTERN),
    COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
    NO_BUZZ(NO_BUZZ_PATTERN)
}

class GameViewModel: ViewModel() {

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the time panic buzzing starts at
        const val PANIC_TIME = 9000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 10000L
    }

    private val timer: CountDownTimer

    // Buzzing events
    private val _buzzingPattern = MutableLiveData<BuzzType>(BuzzType.NO_BUZZ)
    val buzzingPattern: LiveData<BuzzType>
        get() = _buzzingPattern

    // When game finishes
    private val _onGameFinish = MutableLiveData<Boolean>(false)
    val onGameFinish: LiveData<Boolean>
        get() = _onGameFinish

    // The current time
    private val _currentTime = MutableLiveData<Long>(0L)
    val currentTime: LiveData<Long>
        get() = _currentTime

    val currentTimeString = Transformations.map(_currentTime) {
        DateUtils.formatElapsedTime(it)
    }

    // The current word
    private val _word = MutableLiveData("")
    val word: LiveData<String>
        get() = _word

    // The current score
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        resetList()
        nextWord()

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.postValue(millisUntilFinished / 1000)
                if (millisUntilFinished == PANIC_TIME) {
                    _buzzingPattern.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                if (currentTime.value == DONE) {
                    _buzzingPattern.value = BuzzType.GAME_OVER
                    _onGameFinish.postValue(true)
                }
            }
        }

        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.postValue(score.value?.minus(1))
        nextWord()
    }

    fun onCorrect() {
        _buzzingPattern.value = BuzzType.CORRECT
        _score.postValue(score.value?.plus(1))
        nextWord()
    }

    /**
     * Resets game state
     */
    fun resetGameFinish() {
        _onGameFinish.value = false
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.postValue(wordList.removeAt(0))

    }

}