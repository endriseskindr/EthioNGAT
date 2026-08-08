package com.ngat.ethiopia.util

import com.ngat.ethiopia.data.entity.UserProgress

/**
 * SM-2 Spaced Repetition Algorithm implementation.
 * Based on the SuperMemo 2 algorithm with quality values 0-5.
 *
 * Quality grades:
 * 0 - complete blackout
 * 1 - incorrect response; the correct one remembered
 * 2 - incorrect response; where the correct one seemed easy to recall
 * 3 - correct response recalled with serious difficulty
 * 4 - correct response after a hesitation
 * 5 - perfect response
 */
object SM2Algorithm {

    private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L

    data class ReviewResult(
        val repetitions: Int,
        val easeFactor: Double,
        val intervalDays: Int,
        val dueAt: Long,
        val lastReviewedAt: Long,
        val lastQuality: Int
    )

    fun calculateNextReview(
        current: UserProgress?,
        quality: Int,
        now: Long = System.currentTimeMillis()
    ): ReviewResult {
        val q = quality.coerceIn(0, 5)
        val prevReps = current?.repetitions ?: 0
        val prevEF = current?.ease_factor ?: 2.5
        val prevInterval = current?.interval_days ?: 0

        var newEF = prevEF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        if (newEF < 1.3) newEF = 1.3

        val newReps: Int
        val newInterval: Int

        if (q < 3) {
            // Failed - reset repetitions
            newReps = 0
            newInterval = 1
        } else {
            newReps = prevReps + 1
            newInterval = when (newReps) {
                1 -> 1
                2 -> 6
                else -> (prevInterval * newEF).toInt().coerceAtLeast(1)
            }
        }

        val dueAt = now + (newInterval * MILLIS_PER_DAY)

        return ReviewResult(
            repetitions = newReps,
            easeFactor = newEF,
            intervalDays = newInterval,
            dueAt = dueAt,
            lastReviewedAt = now,
            lastQuality = q
        )
    }

    fun qualityFromCorrectness(isCorrect: Boolean, difficulty: Int = 3): Int {
        return if (isCorrect) {
            difficulty.coerceIn(3, 5)
        } else {
            difficulty.coerceIn(0, 2)
        }
    }

    fun formatInterval(days: Int): String = when {
        days == 0 -> "Today"
        days == 1 -> "Tomorrow"
        days < 7 -> "$days days"
        days < 30 -> "${days / 7} week${if (days / 7 > 1) "s" else ""}"
        days < 365 -> "${days / 30} month${if (days / 30 > 1) "s" else ""}"
        else -> "${days / 365} year${if (days / 365 > 1) "s" else ""}"
    }
}
