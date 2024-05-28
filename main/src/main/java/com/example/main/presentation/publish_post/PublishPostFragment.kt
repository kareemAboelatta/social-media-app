package com.example.main.presentation.publish_post

import android.net.Uri
import android.view.SoundEffectConstants
import android.widget.MediaController
import androidx.fragment.app.viewModels
import com.example.core.BaseFragment
import com.example.core.ui.ProgressDialogUtil
import com.example.core.ui.adapter.AddDeleteImagesAdapter
import com.example.core.ui.pickers.pickCompressedVideo
import com.example.core.ui.utils.loadImageFromUrl
import com.example.main.databinding.FragmentPublishPostBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PublishPostFragment : BaseFragment<FragmentPublishPostBinding>(FragmentPublishPostBinding::inflate) {


    private val viewModel: PublishPostViewModel by viewModels()
    private lateinit var attachmentAdapter: AddDeleteImagesAdapter
    override fun onViewCreated() {



    }

    override fun onClicks(){
        with(binding){
         fab.setOnClickListener {
             pickCompressedVideo(progressUtil = ProgressDialogUtil(requireActivity())){ path, uri ->
                 requireActivity().getVideoThumbnailUri(uri)?.let {
                     photo.loadImageFromUrl(it)
                     setVideoToVideoView(uri)
                 }
             }
         }
        }
    }



    private fun setVideoToVideoView(videoUri : Uri) {
        val mediaController = MediaController(requireActivity())
        mediaController.setAnchorView(binding.publishVideo)
        // set media controller to Video view
        binding.publishVideo.setMediaController(mediaController)
        binding.publishVideo.setVideoURI(videoUri)

        binding.publishVideo.setOnPreparedListener {
            binding.publishVideo.start()
            mediaController.show()
            mediaController.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
        }
    }




}
