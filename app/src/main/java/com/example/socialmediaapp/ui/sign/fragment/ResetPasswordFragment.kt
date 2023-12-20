package com.example.socialmediaapp.ui.sign.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialmediaapp.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth



    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetBtnReset.setOnClickListener {
            if (binding.resetEmail.text.toString().isNotEmpty()){
                auth.sendPasswordResetEmail(binding.resetEmail.text.toString()).addOnSuccessListener {
                    Toast.makeText(context, "Check your email now ..", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Write Your Email", Toast.LENGTH_SHORT).show()
            }

        }

    }
}