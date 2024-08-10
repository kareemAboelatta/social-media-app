package com.example.main.data.datasource

import android.net.Uri
import androidx.core.net.toUri
import com.example.common.AppDispatcher
import com.example.common.Dispatcher
import com.example.common.domain.model.Attachment
import com.example.common.domain.model.AttachmentType
import com.example.common.ui.utils.Constants
import com.example.main.domain.model.Post
import com.example.main.domain.model.input.CreatePostInput
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface PostDatasource {

    suspend fun uploadPost(postInput: CreatePostInput): Post
    suspend fun fetchAllPosts(): List<Post>
    suspend fun fetchVideoPosts(): List<Post>

}

class PostDatasourceFirebase @Inject constructor(
    private val refDatabase: DatabaseReference,
    private val refStorage: StorageReference,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @Dispatcher(AppDispatcher.Default) private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : PostDatasource {

    override suspend fun fetchAllPosts(): List<Post> = withContext(ioDispatcher) {
        val snapshot = refDatabase.child(Constants.POSTS).get().await()
        snapshot.children.mapNotNull { it.getValue(Post::class.java) }
    }


    override suspend fun fetchVideoPosts(): List<Post> = withContext(ioDispatcher) {
        val snapshot = refDatabase.child(Constants.POSTS).get().await()
        snapshot.children.mapNotNull { it.getValue(Post::class.java) }
            .filter { it.attachments.size == 1 && it.attachments.first().type == AttachmentType.VIDEO }
    }

    override suspend fun uploadPost(postInput: CreatePostInput): Post =
        withContext(ioDispatcher) {
            val currentTimeMillis = System.currentTimeMillis()

            val attachmentUrls = uploadAttachments(postInput.attachments)

            val post = Post(
                id = currentTimeMillis.toString(),
                attachments = attachmentUrls,
                caption = postInput.caption,
                user = postInput.user,
                postInfo = postInput.postInfo.copy(createAt = currentTimeMillis)
            )

            setPostInfoOnDatabase(post)
        }

    private suspend fun uploadAttachments(attachments: List<Attachment>): List<Attachment> =
        coroutineScope {
            attachments.map { attachment ->
                async { uploadFile(File(attachment.attachment).toUri(), attachment.type) }
            }.map { it.await() }
        }

    private suspend fun uploadFile(uri: Uri, type: AttachmentType): Attachment =
        withContext(defaultDispatcher) {
            val folder = when (type) {
                AttachmentType.IMAGE -> Constants.IMAGES
                AttachmentType.VIDEO -> Constants.VIDEOS
            }
            val uploadTask = refStorage.child(folder).putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            Attachment(downloadUrl, type)
        }


    private suspend fun setPostInfoOnDatabase(post: Post): Post = coroutineScope {
        refDatabase.child(Constants.POSTS).child(post.id).setValue(post).await()
        post
    }
}