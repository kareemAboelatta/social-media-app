package com.example.common.data.repository

import com.example.common.CustomDataException
import com.example.common.data.datasource.UserDatasource
import com.example.common.domain.model.User
import com.example.common.domain.repository.UserRepository
import com.example.core.ui.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private var userDatasource: UserDatasource,
) : UserRepository {
    override suspend fun getUserInfo(userId: String): Flow<DataState<User>> = safeApiCall {
        userDatasource.getUserInfo(userId = userId)
    }

    override suspend fun getCurrentUserInfo(): Flow<DataState<User>> = safeApiCall {
        userDatasource.getCurrentUserInfo()

    }
}


suspend fun <T> safeApiCall(
    call: suspend () -> T
): Flow<DataState<T>> = flow<DataState<T>> {
    withTimeout(5000) {
        val response = call.invoke()
        handleSuccess(response)//handleSuccess
    }
}.onStart {
    emit(DataState.Loading)
}.catch {
    emit(DataState.Error(it)) // handle error
}.flowOn(Dispatchers.IO)

fun <T> handleSuccess(response: T): DataState<T> {
    if (response != null) return DataState.Success(response)
    return DataState.Error(CustomDataException("An unexpected error occurred"))
}