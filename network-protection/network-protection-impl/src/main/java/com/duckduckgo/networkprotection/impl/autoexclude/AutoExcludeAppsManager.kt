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
import com.duckduckgo.networkprotection.impl.autoexclude.AutoExcludeAppsManager.IncompatibleApp
import com.duckduckgo.networkprotection.store.NetPExclusionListRepository
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface AutoExcludeAppsManager {
    /**
     * Returns a list of apps that has been flagged for auto exclude.
     * An app can only be flagged once.
     * An installed app will be flagged if it is part of the auto exclude list and is not part of the user's exclusion list.
     */
    suspend fun getFlaggedApps(): List<IncompatibleApp>

    /**
     * Marks an app that has been flagged by auto exclude as shown.
     * An app can only be flagged / shown in auto-exclude prompt ONLY once.
     */
    fun markAppAsFlagged(app: IncompatibleApp)

    /**
     * Returns a list of apps that is installed on this device and is part of the auto exclude list
     */
    suspend fun getAllInstalledIncompatibleApps(): List<IncompatibleApp>

    /**
     * Returns a list of apps that is installed on this device and is part of the auto exclude list
     * And is currently NOT part of the user's exclusion list.
     */
    suspend fun getAllInstalledProtectedIncompatibleApps(): List<IncompatibleApp>

    /**
     * Returns if the app is part of the auto exclude list
     */
    fun isAppMarkedAsNotCompatible(appPackage: String): Boolean

    data class IncompatibleApp(
        val appPackage: String,
        val appName: String,
    )
}

@ContributesBinding(AppScope::class)
class RealAutoExcludeAppsManager @Inject constructor(
    private val netPExclusionListRepository: NetPExclusionListRepository,
) : AutoExcludeAppsManager {
    private val mockAutoExcludeMap = mapOf(
        IncompatibleApp(appPackage = "com.google.android.projection.gearhead", appName = "Android Auto") to false,
        IncompatibleApp(appPackage = "com.ivuu", appName = "AlfredCamera") to true,
        IncompatibleApp(appPackage = "com.openai.chatgpt", appName = "ChatGPT") to false,
    )

    override suspend fun getFlaggedApps(): List<IncompatibleApp> {
        return mockAutoExcludeMap.filter { !it.value }.map { it.key }
    }

    override fun markAppAsFlagged(app: IncompatibleApp) {
    }

    override fun isAppMarkedAsNotCompatible(appPackage: String): Boolean {
        return mockAutoExcludeMap.keys.any { it.appPackage == appPackage }
    }

    override suspend fun getAllInstalledIncompatibleApps(): List<IncompatibleApp> {
        return mockAutoExcludeMap.keys.toList()
    }

    override suspend fun getAllInstalledProtectedIncompatibleApps(): List<IncompatibleApp> {
        return mockAutoExcludeMap.filter {
            !netPExclusionListRepository.getExcludedAppPackages().contains(it.key.appPackage)
        }.map {
            it.key
        }
    }
}
