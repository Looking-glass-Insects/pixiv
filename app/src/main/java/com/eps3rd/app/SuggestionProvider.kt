package com.eps3rd.app

import android.content.SearchRecentSuggestionsProvider

class SuggestionProvider: SearchRecentSuggestionsProvider() {

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.eps3rd.SuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }


}