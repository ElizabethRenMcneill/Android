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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckduckgo.anvil.annotations.ContributesViewModel
import com.duckduckgo.common.utils.DispatcherProvider
import com.duckduckgo.di.scopes.FragmentScope
import com.duckduckgo.networkprotection.impl.autoexclude.AutoExcludeAppsManager.IncompatibleApp
import com.duckduckgo.networkprotection.impl.autoexclude.VpnAutoExcludePromptFragment.Companion.Trigger
import com.duckduckgo.networkprotection.impl.autoexclude.VpnAutoExcludePromptFragment.Companion.Trigger.NEW_FLAGGED_APP
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@ContributesViewModel(FragmentScope::class)
class VpnAutoExcludePromptViewModel @Inject constructor(
    private val autoExcludeAppsManager: AutoExcludeAppsManager,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    private val _viewState = MutableStateFlow<ViewState>(ViewState(emptyList()))

    fun onPromptShown(trigger: Trigger) {
        viewModelScope.launch(dispatcherProvider.io()) {
            _viewState.emit(
                ViewState(
                    incompatibleApps = if (trigger == NEW_FLAGGED_APP) {
                        autoExcludeAppsManager.getFlaggedApps()
                    } else {
                        autoExcludeAppsManager.getAllInstalledProtectedIncompatibleApps()
                    },
                ),
            )
        }
    }

    fun viewState(): Flow<ViewState> = _viewState.asStateFlow()

    fun onAddExclusionsSelected(shouldEnableAutoExclude: Boolean) {
        if (shouldEnableAutoExclude) {
            // Enable auto exclude
        }
    }

    data class ViewState(
        val incompatibleApps: List<IncompatibleApp>,
    )
}
