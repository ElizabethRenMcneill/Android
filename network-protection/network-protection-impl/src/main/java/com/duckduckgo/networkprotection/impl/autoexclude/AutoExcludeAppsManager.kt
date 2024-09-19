/*
 * Copyright (c) 2024 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.networkprotection.impl.autoexclude

import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.networkprotection.impl.autoexclude.AutoExcludeAppsManager.FlaggedApp
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface AutoExcludeAppsManager {
    suspend fun getFlaggedApps(): List<FlaggedApp>

    fun markAppAsShown(app: FlaggedApp)

    fun isAppMarkedAsNotCompatible(appPackage: String): Boolean

    data class FlaggedApp(
        val appPackage: String,
        val appName: String,
    )
}

@ContributesBinding(AppScope::class)
class RealAutoExcludeAppsManager @Inject constructor() : AutoExcludeAppsManager {
    override suspend fun getFlaggedApps(): List<FlaggedApp> {
        return listOf(
            FlaggedApp(appPackage = "com.openai.chatgpt", appName = "ChatGPT"),
            FlaggedApp(appPackage = "com.google.android.projection.gearhead", appName = "Android Auto"),
        )
    }

    override fun markAppAsShown(app: FlaggedApp) {
    }

    override fun isAppMarkedAsNotCompatible(appPackage: String): Boolean {
        return when (appPackage) {
            "com.openai.chatgpt" -> true
            "com.google.android.projection.gearhead" -> true
            else -> false
        }
    }
}
