package com.example.socialmediaapp.ui.main.fragment

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.adapter.AdapterPost
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.auth.presentation.AuthActivity
import com.example.common.ui.utils.Status
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


import com.example.socialmediaapp.databinding.FragmentProfileBinding


import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var adapterPosts: AdapterPost


    var uriImage: Uri? = null
    var uriImageCover: Uri? = null

    lateinit var prog: ProgressDialog
    lateinit var postList: ArrayList<Post>

    private lateinit var profileImageLauncher: ActivityResultLauncher<String>
    private lateinit var coverImageLauncher: ActivityResultLauncher<String>


    private val viewModel by viewModels<ViewModelMain>()


    var name: String? = null
    var bio: String? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postList = ArrayList()
        prog = ProgressDialog(activity)
        prog.setMessage("Wait a minute...")


        profileImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    uriImage = it
                    viewModel.changePhotoOrCover(it, "images", "image")
                    prog.show()
                    observeChangePhotoOrCover()
                }
            }

        coverImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    uriImageCover = it
                    viewModel.changePhotoOrCover(it, "covers", "cover")
                    prog.show()
                    observeChangePhotoOrCover()
                }
            }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerViewSetUp()

        binding.profBtnChangeProfile.setOnClickListener {
            showAlertDialogForChangePhotos("profile")
        }
        binding.profBtnChangeCover.setOnClickListener {
            showAlertDialogForChangePhotos("cover")
        }
        binding.profBtnEditPen.setOnClickListener {
            var popupMenu = PopupMenu(activity, binding.profBtnEditPen)
            popupMenu.menuInflater.inflate(R.menu.pop_menu, popupMenu.menu)
            popupMenu.menu.removeItem(R.id.logout)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.change_name ->
                        showUpdateNameBioDialog("name")

                    R.id.change_bio ->
                        showUpdateNameBioDialog("bio")
                }
                true
            }
            popupMenu.show()
        }
        binding.profBtnSetting.setOnClickListener {
            var popupMenu = PopupMenu(activity, binding.profBtnSetting)
            popupMenu.menuInflater.inflate(R.menu.pop_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.change_name ->
                        showUpdateNameBioDialog("name")

                    R.id.change_bio ->
                        showUpdateNameBioDialog("bio")

                    R.id.logout -> {
                        auth.signOut()
                        startActivity(Intent(activity, AuthActivity::class.java))
                    }
                }
                true
            })
            popupMenu.show()
        }



        viewModel.getPostsForSpecificUser(auth.currentUser?.uid!!)
        viewModel.getSpecificUserData(auth.currentUser?.uid!!)

        //observe Posts from viewModel
        viewModel.postsForSpecificUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.profProgressBar.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {
                    binding.profProgressBar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    adapterPosts.setList(postList)
                }

                Status.ERROR -> {
                    binding.profProgressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
        adapterPosts.setonItemClickListenerForLike {
            viewModel.setLike(it)
        }
        //observe data for this user from viewModel
        viewModel.specificUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val user = it.data
                    binding.profName.text = user?.name
                    binding.profBio.text = user?.bio

                    name = user?.name
                    bio = user?.bio
                    glide.load(user?.image).error(R.drawable.ic_profile)
                        .into(binding.profImageProfile)


                    glide.load(user?.cover).error(R.drawable.ic_image_default)
                        .into(binding.profImageCover)
                    binding.profImageCover.scaleType = ImageView.ScaleType.CENTER_CROP

                    prog.dismiss()

                }

                Status.ERROR -> {

                }

                else -> {}
            }


        }


        viewModel.changePhotoOrCoverLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    prog.dismiss()
                    Toast.makeText(activity, "Photo changed", Toast.LENGTH_SHORT).show()
                }

                Status.ERROR -> {
                    prog.dismiss()
                    Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }

    }

    fun recyclerViewSetUp() {
        //put linearLayout in recycle
        val linearLayout = LinearLayoutManager(activity)
        linearLayout.stackFromEnd = true
        linearLayout.reverseLayout = true
        binding.profRec.layoutManager = linearLayout
        adapterPosts.setList(postList)
        binding.profRec.adapter = adapterPosts
    }

    private fun showUpdateNameBioDialog(key: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Update $key")
        builder.setCancelable(false)
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(10, 10, 10, 10)


        val editText = EditText(activity)
        if (key == "name") {
            editText.hint = "" + name
        } else if (key == "bio") {
            editText.hint = "" + bio
        }
        editText.setHintTextColor(resources.getColor(R.color.colorGray))
        linearLayout.addView(editText)
        builder.setView(linearLayout)


        builder.setPositiveButton("Update") { _, _ ->
            val value = editText.text.toString()
            if (value.isEmpty()) {
                Toast.makeText(activity, "Where's Your new $key", Toast.LENGTH_SHORT).show()
            } else {
                //update key
                viewModel.changeNameOrBio(value, key)
                prog.show()
                viewModel.changeNameOrBioLiveData.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            prog.dismiss()
                            Toast.makeText(activity, "$key changed", Toast.LENGTH_SHORT).show()
                        }

                        Status.ERROR -> {
                            prog.dismiss()
                            Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }


        }

        builder.setNegativeButton("Cancel") { _, _ ->


        }

        val myAlertDialog: AlertDialog = builder.create()
        myAlertDialog.setOnShowListener {
            myAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.colorGreen));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.red));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setTextColor(resources.getColor(R.color.red));
        }
        myAlertDialog.show()

    }

    private fun showAlertDialogForChangePhotos(type: String) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        // Setting Alert Dialog Title
        alertDialog.setTitle("Are you sure,You want change $type photo?")
        // Icon Of Alert Dialog
        alertDialog.setIcon(R.drawable.ic_warning)
        alertDialog.setCancelable(false)

        if (type == "profile") {
            alertDialog.setPositiveButton("Yes") { dialogInterface, which ->


                changeProfilePhoto()


            }
            alertDialog.setNegativeButton("Close") { dialogInterface, which -> }
        } else {
            alertDialog.setPositiveButton("Yes") { dialogInterface, which ->

                changeCoverPhoto()

            }
            alertDialog.setNegativeButton("Close") { dialogInterface, which -> }

        }
        alertDialog.setNeutralButton("Cancel") { dialogInterface, which -> }


        val myAlertDialog: AlertDialog = alertDialog.create()
        myAlertDialog.setOnShowListener {
            myAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.colorGreen));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.red));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setTextColor(resources.getColor(R.color.red));
        }
        myAlertDialog.show()
    }

    private fun changeProfilePhoto() {
        profileImageLauncher.launch("image/*")
    }


    private fun changeCoverPhoto() {
        coverImageLauncher.launch("image/*")
    }

    private fun hasReadExternalStoragePermission() = activity?.let {
        ActivityCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)
    } == PackageManager.PERMISSION_GRANTED


    private fun requestPermission() {
        var permissionsToRequest = mutableListOf<String>()
        if (!hasReadExternalStoragePermission()) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    permissionsToRequest.toTypedArray(),
                    0
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionRequest", "${permissions[i]} granted.")
                }
            }

        }
    }


    private fun observeChangePhotoOrCover() {
        viewModel.changePhotoOrCoverLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    prog.dismiss()
                    Toast.makeText(activity, "Photo changed", Toast.LENGTH_SHORT).show()
                }

                Status.ERROR -> {
                    prog.dismiss()
                    Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }


}



