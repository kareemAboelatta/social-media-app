package com.example.socialmediaapp.main.ui.puplish

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.common.ui.ProgressDialogUtil
import com.example.common.ui.pickers.pickCompressedImage
import com.example.common.ui.pickers.pickCompressedVideo
import com.example.common.ui.utils.Status
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityPublishBinding
import com.example.socialmediaapp.databinding.FragmentPostDetailsBinding
import com.example.socialmediaapp.databinding.FragmentPublishBinding
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.ui.main.MainActivity
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PublishFragment  : Fragment() {

    private var _binding: FragmentPublishBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth: FirebaseAuth


    var imageUri: Uri? = null
    var videoUri: Uri? = null


    private val progressDialogUtil = ProgressDialogUtil()



    private val viewModel by viewModels<ViewModelMain>()

    lateinit var thisUser : User

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var myContext: Context



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPublishBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDataForCurrentUser()
        viewModel.currentUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    thisUser = it.data!!
                    glide.load(thisUser.image).into(binding.publishMyImage)
                    binding.publishMyName.text = thisUser.name
                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }

        onClicks()
    }

    //fun onClicks
    fun onClicks(){
        binding.publishBtnPublish.setOnClickListener {
            val caption=binding.publishCaption.text.toString()
            val fans= binding.publishTextAnyone.text.toString()
            if (caption.isEmpty()){
                Toast.makeText(requireActivity(), "Enter caption", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            var postType: String = "article"
            var postAttachment: String = ""
            if (imageUri == null && videoUri == null) {
                //just article
                postType = "article"
                postAttachment = ""

            } else if (imageUri != null && videoUri == null) {
                //image
                postType = "image"
                postAttachment = imageUri.toString()

            } else if (imageUri == null ) {
                //video
                postType = "video"
                postAttachment = videoUri.toString()

            }
            val post = Post(
                thisUser.id, thisUser.name, thisUser.email, thisUser.image, caption,
                "", "" + fans, postType, postAttachment
            )

            viewModel.identifyLanguage(caption)
            viewModel.languageIdentifierLiveData.observe(viewLifecycleOwner){
                when(it.status){
                    Status.SUCCESS->{
                        post.languageCode = it.data.toString()
                        viewModel.uploadPost(post)
                        viewModel.postLiveData.observe(viewLifecycleOwner){
                            when(it.status){
                                Status.SUCCESS->{
                                    findNavController().navigate(R.id.homeFragment)
                                    Toast.makeText(myContext, "Post Published", Toast.LENGTH_SHORT).show()
                                }
                                Status.ERROR->{
                                    Toast.makeText(myContext, "${it.message}", Toast.LENGTH_SHORT).show()

                                }

                                else -> {}
                            }
                        }
                    }
                    Status.ERROR->{
                        Toast.makeText(myContext, "${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }


        }


        binding.publishBtnAnyone.setOnClickListener {
            var popupMenu= PopupMenu(requireActivity(),  binding.publishBtnAnyone)
            popupMenu.menuInflater.inflate(R.menu.anyone_menu, popupMenu.menu)
            // popupMenu.menu.removeItem(R.id.logout)
            popupMenu.setOnMenuItemClickListener{ item ->
                when (item.itemId) {
                    R.id.menu_anyone ->
                        binding.publishTextAnyone.text="Anyone"
                    R.id.menu_friends ->
                        binding.publishTextAnyone.text="Friends"
                    R.id.menu_only_me ->
                        binding.publishTextAnyone.text="Only me"
                }
                true
            }
            popupMenu.show()
        }
        binding.publishBtnBottom.setOnClickListener {
            val bottomSheetDialog= BottomSheetDialog(requireActivity(), R.style.BottomSheetStyle)

            val sheetView= LayoutInflater.from(requireActivity())
                .inflate(
                    R.layout.bottom_dialog,
                    requireActivity().findViewById(R.id.dialog_container))

            sheetView.findViewById<LinearLayout>(R.id.bottom_image).setOnClickListener {
//                imagePickDialog()
                pickCompressedImage(
                    progressUtil = progressDialogUtil,
                    onSaveFile = { compressedFile, uri ->
                        imageUri = uri
                        binding.publishImage.visibility = View.VISIBLE
                        binding.publishImage.setImageURI(imageUri)
                        // Reset video URI if an image is picked
                        videoUri = null
                        binding.publishVideo.visibility = View.GONE
                    }
                )

                binding.publishCaption.setLines(3)
                bottomSheetDialog.dismiss()
            }
            sheetView.findViewById<LinearLayout>(R.id.bottom_video).setOnClickListener {
//                videoPickDialog()
                pickCompressedVideo(
                    progressUtil = progressDialogUtil,
                    onSaveFile = { compressedFile, uri ->
                        videoUri = uri
                        binding.publishVideo.visibility = View.VISIBLE
                        setVideoToVideoView()
                        // Reset image URI if a video is picked
                        imageUri = null
                        binding.publishImage.visibility = View.GONE
                    }
                )

                binding.publishCaption.setLines(3)
                bottomSheetDialog.dismiss()
            }
            sheetView.findViewById<LinearLayout>(R.id.bottom_article).setOnClickListener {
                Toast.makeText(requireActivity(), "article", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
                binding.publishCaption.setLines(9)
                binding.publishVideo.visibility=View.GONE
                binding.publishImage.visibility=View.GONE
                imageUri=null
                videoUri=null
            }
            sheetView.findViewById<LinearLayout>(R.id.bottom_attache).setOnClickListener {
                binding.publishVideo.visibility=View.GONE
                binding.publishImage.visibility=View.GONE
                imageUri=null
                videoUri=null

                Toast.makeText(requireActivity(), "attache", Toast.LENGTH_SHORT).show()
            }

            sheetView.findViewById<ImageView>(R.id.bottom_close).setOnClickListener {
                bottomSheetDialog.dismiss()
            }


            bottomSheetDialog.setContentView(sheetView)
            bottomSheetDialog.show()




        }
    }

    private fun setVideoToVideoView() {
        val mediaController = MediaController(requireActivity())
        mediaController.setAnchorView(binding.publishVideo)
        // set media controller to Video view
        binding.publishVideo.setMediaController(mediaController)
        binding.publishVideo.setVideoURI(videoUri)

        binding.publishVideo.setOnPreparedListener{
            binding.publishVideo.start()
            mediaController.show()
            mediaController.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
        }
    }

}