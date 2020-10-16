package com.sa.betvictor.ui

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class TweetQueryValidatorTest {

    private val validator = TweetQueryValidator()

    @Test
    fun `isValid return false if query is empty`() {
        val query = ""

        assertFalse(validator.isValid(query))
    }

    @Test
    fun `isValid return false if query is longer then limit (128 symbols)`() {
        val query =
            "1234567890123456789012345678901234567890123456789012345678901234567890" +
                    "1234567890123456789012345678901234567890123456789012345678901234567890"

        assertFalse(validator.isValid(query))
    }

    @Test
    fun `isValid return true if query is not empty and is shorter then limit (128 symbols)`() {
        val query = "Hello"

        assertTrue(validator.isValid(query))
    }
}

