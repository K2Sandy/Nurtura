package com.tumbuhsehat.app.di

import android.content.Context
import com.tumbuhsehat.app.data.local.AppDatabase
import com.tumbuhsehat.app.data.repository.GrowthReferenceRepository
import com.tumbuhsehat.app.data.repository.NutritionCalculator
import com.tumbuhsehat.app.data.repository.ReferenceRepository
import com.tumbuhsehat.app.data.repository.UserRepository
import com.tumbuhsehat.app.reminder.ReminderScheduler

/**
 * Manual DI container - deliberately simple (no Hilt/Koin) so the project
 * stays easy to follow. `Application.container` is the single entry point;
 * ViewModels pull what they need from it.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val referenceRepository = ReferenceRepository(context)
    private val growthReferenceRepository = GrowthReferenceRepository(context)
    private val reminderScheduler = ReminderScheduler(context)

    val userRepository = UserRepository(
        db = database,
        referenceRepository = referenceRepository,
        growthReferenceRepository = growthReferenceRepository,
        reminderScheduler = reminderScheduler,
    )

    val nutritionCalculator = NutritionCalculator(growthReferenceRepository, referenceRepository)

    val reference get() = referenceRepository
    val growthReference get() = growthReferenceRepository
}
