package com.example.socialmediaapp.ui.sign.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.helpers.MyValidation
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.ui.sign.ViewModelSignUser
import com.example.socialmediaapp.utils.Status
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.inputTextLayoutEmail
import kotlinx.android.synthetic.main.fragment_register.inputTextLayoutPassword
import kotlinx.android.synthetic.main.fragment_register.progress
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    var uri : Uri ?=null


    @Inject
    lateinit var mycontext: Context

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<ViewModelSignUser>()





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        reg_btn_register.setOnClickListener {
            val name: String = inputTextLayoutName.editText!!.text.toString()
            val bio: String = inputTextLayoutBio.editText!!.text.toString()
            val email: String = inputTextLayoutEmail.editText!!.text.toString()
            val password: String = inputTextLayoutPassword.editText!!.text.toString()

            if (valid()) {
                var user = User(
                    name,
                    bio,
                    email, "", "", "", "")


                viewModel.createUser(email, password)
                viewModel.createUserLiveData.observe(viewLifecycleOwner){
                    when(it.status){
                        Status.LOADING->{
                            progress.visibility = View.VISIBLE
                        }
                        Status.SUCCESS->{



                            // success to create account in firebase authentication
                            progress.visibility = View.INVISIBLE
                            user.id= auth.currentUser?.uid!!
                            viewModel.uploadUserPictureOnFireStorage(uri!!)
                            viewModel.userPictureLiveData.observe(requireActivity()){
                                when(it.status){
                                    Status.LOADING->{
                                        progress.visibility = View.VISIBLE
                                    }
                                    Status.SUCCESS->{
                                        // now it = image download url
                                        // get image download url and store it with data
                                        user.image= it.data.toString()
                                        progress.visibility = View.INVISIBLE
                                        viewModel.setUserDataInfoOnDatabase(user)
                                        viewModel.setUserDataInfoOnDatabaseLiveData.observe(viewLifecycleOwner) {
                                            when (it.status) {



                                                Status.SUCCESS -> {
                                                    // now success with storing the data in database
                                                    Toast.makeText(context, "Registration success ", Toast.LENGTH_SHORT)
                                                        .show()
                                                    progress.visibility = View.INVISIBLE
                                                    findNavController().navigate(
                                                        R.id.action_registerFragment_to_loginFragment
                                                    )
                                                }
                                                Status.ERROR -> {
                                                    progress.visibility = View.INVISIBLE
                                                    Toast.makeText(activity, "" + it.message, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }

                                    Status.ERROR->{
                                        progress.visibility = View.INVISIBLE
                                        Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }



                        }
                        Status.ERROR->{
                            progress.visibility = View.INVISIBLE
                            Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }



            }



        }



        //for image
        reg_image.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type="image/*"
                startActivityForResult(it, 0)
            }
        }



        reg_backtologin.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment
            )
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK && requestCode == 0){
            uri=data?.data
            reg_image.setImageURI(uri)
        }
    }

    private fun valid():Boolean{
        val bio: String = inputTextLayoutBio.editText!!.text.toString()

        if (!MyValidation.validateName(requireContext(),inputTextLayoutName)){
            return false
        }else if (bio.isEmpty()){
            inputTextLayoutBio.isHelperTextEnabled = true
            inputTextLayoutBio.helperText = "Require*"
            return false
        }else if (!MyValidation.isValidEmail(requireContext(),inputTextLayoutEmail)){
            return false
        }else if (!MyValidation.validatePass(requireContext(),inputTextLayoutPassword)){
            return false
        }else if (uri==null){
            Toast.makeText(activity, "Select image !!", Toast.LENGTH_LONG).show()
            return false
        }else{
            return true
        }
    }


}