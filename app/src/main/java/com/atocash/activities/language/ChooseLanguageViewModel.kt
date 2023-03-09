package com.atocash.activities.language

import com.atocash.base.common.BaseViewModel

class ChooseLanguageViewModel:
    BaseViewModel<ChooseLanguageNavigator>() {

        fun onContinueClick() {
            getNavigator().onLanguageChosen()
        }
}