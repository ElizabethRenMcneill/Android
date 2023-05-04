/*
 * Copyright (c) 2021 DuckDuckGo
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

package com.duckduckgo.mobile.android.vpn.ui.tracker_activity.model

import com.duckduckgo.mobile.android.vpn.apps.ui.TrackingProtectionExclusionListActivity
import com.duckduckgo.mobile.android.vpn.model.TrackingApp

sealed class TrackerFeedItem(open val id: Int) {
    data class TrackerFeedData(
        override val id: Int,
        val bucket: String,
        val trackingApp: TrackingApp,
        val trackingCompanyBadges: List<TrackerCompanyBadge>,
        val trackersTotalCount: Int,
        val timestamp: String,
        val displayTimestamp: String,
    ) : TrackerFeedItem(id)

    object TrackerLoadingSkeleton : TrackerFeedItem(0)

    object TrackerDescriptionFeed : TrackerFeedItem(0)

    data class TrackerTrackerAppsProtection(
        val appsData: AppsProtectionData,
        val selectedFilter: TrackingProtectionExclusionListActivity.Companion.AppsFilter? = null,
    ) : TrackerFeedItem(0)

    data class TrackerFeedItemHeader(val timestamp: String) : TrackerFeedItem(timestamp.hashCode())
}

sealed class TrackerCompanyBadge {
    data class Company(
        val companyName: String,
        val companyDisplayName: String,
    ) : TrackerCompanyBadge()

    data class Extra(
        val amount: Int,
    ) : TrackerCompanyBadge()

    class PrivacyWarningIcon : TrackerCompanyBadge()

    class PrivacyWarningText : TrackerCompanyBadge()
}

data class AppsData(
    val appsCount: Int,
    val isProtected: Boolean,
    val packageNames: List<String>,
)

data class AppsProtectionData(
    val protectedAppsData: AppsData,
    val unprotectedAppsData: AppsData,
)
