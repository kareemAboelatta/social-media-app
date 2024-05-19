package com.example.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.core.ui.ProgressDialogUtil
import com.example.core.ui.utils.UIState
import com.example.core.ui.utils.hideKeyboard
import javax.inject.Inject


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T


abstract class BaseFragment<VBinding : ViewBinding>(private val inflate: Inflate<VBinding>) :
    Fragment() {

    @Inject
    lateinit var progressDialogUtil: ProgressDialogUtil


    private var _binding: VBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (_binding == null) {
            _binding = inflate.invoke(inflater, container, false)

            binding.root.layoutDirection =
                if (resources.configuration.locale.language == "ar")
                    View.LAYOUT_DIRECTION_RTL
                else View.LAYOUT_DIRECTION_LTR
        }
        return binding.root

    }


    protected fun initToolbar(toolbar: Toolbar, title: String) {
        view?.hideKeyboard()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = title
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_back)
    }

    protected fun initToolbarHome(toolbar: Toolbar, title: String) {
        view?.hideKeyboard()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = title
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewCreated()
    }


    abstract fun onViewCreated()


    override fun onDestroyView() {
        super.onDestroyView()
        requireView().hideKeyboard()
        _binding = null
    }

    protected fun<T> UIState<T>.handleState(
        onError : (String) -> Unit = {},
        onSuccess: (T) -> Unit ,
        ) {
        when (this) {
            UIState.Empty -> {
                progressDialogUtil.hideProgress()
            }
            is UIState.Error -> {
                progressDialogUtil.hideProgress()
                Toast.makeText(context, this.error, Toast.LENGTH_SHORT).show()
                onError(this.error)
            }
            UIState.Loading -> {
                progressDialogUtil.showProgress()
            }
            is UIState.Success -> {
                progressDialogUtil.hideProgress()
                onSuccess(this.data)
            }
        }
    }

}



fun Activity.openAuthActivity() {
    val intent = Intent().setClassName(
        this,
        "com.auth.presentation.AuthActivity"
    )
    startActivity(intent)
    finish()
}

fun Activity.openMainActivity() {
    val intent = Intent().setClassName(
        this,
        "com.example.socialmediaapp.ui.main.MainActivity"
    )
    startActivity(intent)
    finish()
}

