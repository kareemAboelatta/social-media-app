package com.example.socialmediaapp.ui.sign.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.helpers.MyValidation
import com.example.socialmediaapp.ui.main.MainActivity
import com.example.socialmediaapp.ui.sign.ViewModelSignUser
import com.example.socialmediaapp.utils.Status

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private  val viewModel by  viewModels<ViewModelSignUser>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_btn_LogIn.setOnClickListener {
            val email: String = inputTextLayoutEmail.editText!!.text.toString()
            val password: String = inputTextLayoutPassword.editText!!.text.toString()



            if (MyValidation.isValidEmail(requireContext(),inputTextLayoutEmail)
                && MyValidation.validatePass(requireContext(),inputTextLayoutPassword)) {

                viewModel.signInWithEmailAndPassword(email, password)
                viewModel.successToLoginLiveData.observe(viewLifecycleOwner){
                    when(it.status){
                        Status.LOADING -> {
                            progress.visibility=View.VISIBLE
                        }
                        Status.SUCCESS -> {
                            progress.visibility=View.GONE
                            activity?.startActivity(Intent(activity, MainActivity::class.java))
                            activity?.finish()
                        }
                        Status.ERROR -> {
                            Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            }



        }




        login_btn_Register.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
            )
        }

        login_forget.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_resetPasswordFragment
            )
        }


    }


}