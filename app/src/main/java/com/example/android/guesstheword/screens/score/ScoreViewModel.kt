package com.example.android.guesstheword.screens.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController

class ScoreViewModel(private val score: Int): ViewModel() {

    val finalScore: String = score.toString()

    // When restarting game
    private val _onRestartGame = MutableLiveData<Boolean>(false)
    val onRestartGame: LiveData<Boolean>
        get() = _onRestartGame


    /** Methods for button press **/

    fun onPlayAgain() {
        _onRestartGame.value = true
    }

}