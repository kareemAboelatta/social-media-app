package com.example.more.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.more.R
import com.example.common.R as CommonR

enum class MoreItems(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val destinationFragmentId: Int
) {
    EDIT_PROFILE(
        title = R.string.edit_profile,
        icon = R.drawable.ic_settings,
        destinationFragmentId = CommonR.id.editProfileFragment
    ),
    CHANGE_LANGUAGE(
        title = R.string.change_language,
        icon = R.drawable.ic_langauge,
        destinationFragmentId = CommonR.id.languageFragment
    ),
/*    ABOUT_US(
        title = R.string.about_us,
        icon = R.drawable.ic_info,
        destinationFragmentId = CommonR.id.aboutUsFragment
    ),
    TERMS_CONDITIONS(
        title = R.string.terms_conditions,
        icon = R.drawable.ic_terms,
        destinationFragmentId = CommonR.id.termsAndConditionsFragment
    ),*/
    LOGOUT(
        title = R.string.logout,
        icon = R.drawable.ic_logout,
        destinationFragmentId = CommonR.id.logoutDialogFragment
    )
}