package com.example.more.ui.lanugue

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.fragment.findNavController
import com.example.common.ui.utils.Constants
import com.example.core.BaseFragment
import com.example.more.R
import com.example.more.databinding.FragmentLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {


    override fun onViewCreated() {
        initToolbar(binding.layoutToolbar.layoutToolbar,getString(R.string.change_language))
    }

    override fun onClicks() {
        binding.btnArabic.setOnClickListener { changeLanguageAndNavigate(Constants.Language.ARABIC) }
        binding.btnEnglish.setOnClickListener { changeLanguageAndNavigate(Constants.Language.ENGLISH) }
    }

    private fun changeLanguageAndNavigate(lang: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(appLocale)
        findNavController().popBackStack()

    }



}