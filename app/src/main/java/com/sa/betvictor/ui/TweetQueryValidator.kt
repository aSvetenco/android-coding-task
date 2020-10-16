package com.sa.betvictor.ui

import androidx.annotation.StringRes
import com.sa.betvictor.R

class TweetQueryValidator {

    var listener: TweetQueryValidationListener? = null

    fun isValid(query: String) =
        validateIsQueryMissing(query) && validateQueryLength(query)

    private fun validateIsQueryMissing(query: String): Boolean {
        val isValid = query.isNotEmpty()
        if (!isValid) listener?.onInvalidQuery(R.string.error_query_validation_empty_query)
        return isValid
    }

    private fun validateQueryLength(query: String): Boolean {
        val isValid = query.length <= MAX_ALLOWED_SYMBOLS_COUNT
        if (!isValid) listener?.onInvalidQuery(R.string.error_query_validation_to_long_query)
        return isValid
    }

    interface TweetQueryValidationListener {
        fun onInvalidQuery(@StringRes errorResId: Int)
    }

    private companion object {
        const val MAX_ALLOWED_SYMBOLS_COUNT = 128
    }
}
