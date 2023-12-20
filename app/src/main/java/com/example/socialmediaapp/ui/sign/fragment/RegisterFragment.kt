package com.example.socialmediaapp.ui.sign.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentRegisterBinding
import com.example.socialmediaapp.helpers.MyValidation
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.ui.sign.ViewModelSignUser
import com.example.socialmediaapp.utils.Status
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {



    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!




    var uri : Uri ?=null


    @Inject
    lateinit var mycontext: Context

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<ViewModelSignUser>()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.regBtnRegister.setOnClickListener {
            val name = binding.inputTextLayoutName.editText?.text.toString()
            val bio = binding.inputTextLayoutBio.editText?.text.toString()
            val email = binding.inputTextLayoutEmail.editText?.text.toString()
            val password = binding.inputTextLayoutPassword.editText?.text.toString()

            if (valid()) {
                val user = User(
                    name,
                    bio,
                    email, "", "", "", "")


                viewModel.createUser(email, password)
                viewModel.createUserLiveData.observe(viewLifecycleOwner){
                    when(it.status){
                        Status.LOADING->{
                            binding.progress.visibility = View.VISIBLE
                        }
                        Status.SUCCESS->{



                            // success to create account in firebase authentication
                            binding.progress.visibility = View.INVISIBLE
                            user.id= auth.currentUser?.uid!!
                            viewModel.uploadUserPictureOnFireStorage(uri!!)
                            viewModel.userPictureLiveData.observe(requireActivity()){
                                when(it.status){
                                    Status.LOADING->{
                                        binding.progress.visibility = View.VISIBLE
                                    }
                                    Status.SUCCESS->{
                                        // now it = image download url
                                        // get image download url and store it with data
                                        user.image= it.data.toString()
                                        binding.progress.visibility = View.INVISIBLE
                                        viewModel.setUserDataInfoOnDatabase(user)
                                        viewModel.setUserDataInfoOnDatabaseLiveData.observe(viewLifecycleOwner) {
                                            when (it.status) {



                                                Status.SUCCESS -> {
                                                    // now success with storing the data in database
                                                    Toast.makeText(context, "Registration success ", Toast.LENGTH_SHORT)
                                                        .show()
                                                    binding.progress.visibility = View.INVISIBLE
                                                    findNavController().navigate(
                                                        R.id.action_registerFragment_to_loginFragment
                                                    )
                                                }
                                                Status.ERROR -> {
                                                    binding.progress.visibility = View.INVISIBLE
                                                    Toast.makeText(activity, "" + it.message, Toast.LENGTH_SHORT).show()
                                                }

                                                else -> {}
                                            }
                                        }
                                    }

                                    Status.ERROR->{
                                        binding.progress.visibility = View.INVISIBLE
                                        Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }



                        }
                        Status.ERROR->{
                            binding.progress.visibility = View.INVISIBLE
                            Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }



            }



        }



        //for image
        binding.regImage.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type="image/*"
                startActivityForResult(it, 0)
            }
        }



        binding.regBacktologin.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment
            )
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            uri = data?.data
            binding.regImage.setImageURI(uri)
        }
    }

    private fun valid():Boolean{
        val bio = binding.inputTextLayoutBio.editText?.text.toString()

        if (!MyValidation.validateName(requireContext(),binding.inputTextLayoutName)){
            return false
        }else if (bio.isEmpty()){
            binding.inputTextLayoutBio.isHelperTextEnabled = true
            binding.inputTextLayoutBio.helperText = "Require*"
            return false
        }else if (!MyValidation.isValidEmail(requireContext(),binding.inputTextLayoutEmail)){
            return false
        }else if (!MyValidation.validatePass(requireContext(),binding.inputTextLayoutPassword)){
            return false
        }else if (uri==null){
            Toast.makeText(activity, "Select image !!", Toast.LENGTH_LONG).show()
            return false
        }else{
            return true
        }
    }


}