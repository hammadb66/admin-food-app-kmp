package org.example.adminfoodapp.data.remote

import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.data.model.OrderDetails
import org.example.adminfoodapp.utils.CancelledBy
import org.example.adminfoodapp.utils.Resource
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File

class FirebaseMenuDataSource(
    private val db: DatabaseReference,
    private val auth: FirebaseAuth,
    private val cloudinaryUploader: CloudinaryUploader
) {
    private val menuRef = db.child("menu")

    fun getMenuItems(): Flow<Resource<MutableList<AllMenu>>> = callbackFlow {
        trySend(Resource.Loading)
        val ref = db.child("menu")

        val job = launch {
            try {
                ref.valueEvents.collect { snapshot ->
                    val menuList = mutableListOf<AllMenu>()
                    for (child in snapshot.children) {
                        child.value<AllMenu?>()?.let { menuList.add(it) }
                    }
                    trySend(Resource.Success(menuList))
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
        awaitClose { job.cancel() }
    }

    fun getOrderHistory(): Flow<Resource<MutableList<OrderDetails>>> = callbackFlow {
        trySend(Resource.Loading)
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val ref = db.child("orderDetails").orderByChild("currentTime")
        val job = launch {
            try {
                ref.valueEvents.collect { snapshot ->
                    val orderList = mutableListOf<OrderDetails>()
                    for (orderSnapshot in snapshot.children) {
                        try {
                            orderSnapshot.value<OrderDetails?>()?.let { orderList.add(it) }
                        } catch (e: Exception) {
                            println("CRITICAL: Node '${orderSnapshot.key}' has invalid data.")
                        }
                    }
                    orderList.reverse()
                    trySend(Resource.Success(orderList))
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
        awaitClose { job.cancel() }
    }

    fun getCompleteOrder(): Flow<Resource<MutableList<OrderDetails>>> = callbackFlow {
        trySend(Resource.Loading)
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val ref = db.child("completeOrder").orderByChild("currentTime")
        val job = launch {
            try {
                ref.valueEvents.collect { snapshot ->
                    val orderList = mutableListOf<OrderDetails>()
                    for (orderSnapshot in snapshot.children) {
                        try {
                            orderSnapshot.value<OrderDetails?>()?.let { orderList.add(it) }
                        } catch (e: Exception) {
                            println("CRITICAL: Node '${orderSnapshot.key}' has invalid data.")
                        }
                    }
                    orderList.reverse()
                    trySend(Resource.Success(orderList))
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
        awaitClose { job.cancel() }
    }

    fun orderAccepted(order: OrderDetails): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val orderKey = order.itemPushKey ?: return@flow emit(Resource.Error("Key is missing"))

            db.child("orderDetails").child(orderKey).child("orderAccepted").setValue(true)

            val userHistoryRef = db.child("user").child(order.userId ?: "").child("buyHistory").child(orderKey)

            val updatedOrder = order.copy(orderAccepted = true)
            userHistoryRef.setValue(updatedOrder)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun orderDispatch(order: OrderDetails): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val key = order.itemPushKey ?: ""
            db.child("completeOrder").child(key).setValue(order)
            db.child("orderDetails").child(key).removeValue()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun cancelOrder(order: OrderDetails): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val pushKey = order.itemPushKey
        if (pushKey.isNullOrBlank()) {
            emit(Resource.Error("Invalid order reference"))
            return@flow
        }
        try {
            val updatedOrder = order.copy(
                orderCancel = true, cancelBy = CancelledBy.ADMIN.label, cancelConfirm = true
            )
            val updates = mapOf<String, Any?>(
                "orderDetails/$pushKey" to null,
                "completeOrder/$pushKey" to updatedOrder,
                "user/${updatedOrder.userId}/buyHistory/$pushKey/orderCancel" to true,
                "user/${updatedOrder.userId}/buyHistory/$pushKey/cancelBy" to CancelledBy.ADMIN.label,
                "user/${updatedOrder.userId}/buyHistory/$pushKey/cancelConfirm" to true
            )
            db.updateChildren(updates)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun cancelConfirm(order: OrderDetails): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val pushKey = order.itemPushKey
        if (pushKey.isNullOrBlank()) {
            emit(Resource.Error("Invalid order reference"))
            return@flow
        }
        try {
            val updatedOrder = order.copy(cancelConfirm = true)
            val updates = mapOf<String, Any?>(
                "orderDetails/$pushKey" to null,
                "completeOrder/$pushKey" to updatedOrder,
                "user/${updatedOrder.userId}/buyHistory/$pushKey/cancelConfirm" to true
            )
            db.updateChildren(updates)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun menuItemDelete(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            db.child("menu").child(id).removeValue()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    suspend fun addMenu(item: AllMenu, file: File): Result<Unit> {
        val key = menuRef.push().key ?: return Result.failure(Exception("Firebase key null"))

        println("Firebase key: $key , file name ${file.name} , file path ${file.absolutePath}")

        val uploadResult = cloudinaryUploader.upload(file, key)

        return uploadResult.fold(onSuccess = { imageUrl ->
            try {
                menuRef.child(key).setValue(item.copy(key = key, foodImage = imageUrl))
                if (file.exists()) file.delete()
                Result.success(Unit)
            } catch (e: Exception) {
                if (file.exists()) file.delete()
                Result.failure(e)
            }
        }, onFailure = {
            if (file.exists()) file.delete()
            Result.failure(it)
        })
    }

    suspend fun updateMenu(item: AllMenu, newFile: File?): Result<Unit> {
        val key = item.key ?: return Result.failure(Exception("Cannot update item: Missing Firebase key"))

        // Scenario 1: image bhi update ho rahi hai
        if (newFile != null && newFile.exists()) {
            val uniqueImageId = "${key}_${System.currentTimeMillis()}"
            val uploadResult = cloudinaryUploader.upload(newFile, uniqueImageId)

            return uploadResult.fold(onSuccess = { imageUrl ->
                val updatedItem = item.copy(foodImage = imageUrl)
                updateFirebaseData(key, updatedItem, newFile)
            }, onFailure = {
                if (newFile.exists()) newFile.delete()
                Result.failure(it)
            })
        } else {
            // Scenario 2: sirf text data change hua hai, image nahi
            return updateFirebaseData(key, item, null)
        }
    }

    private suspend fun updateFirebaseData(key: String, item: AllMenu, fileToCleanup: File?): Result<Unit> {
        return try {
            menuRef.child(key).setValue(item)
            if (fileToCleanup != null && fileToCleanup.exists()) fileToCleanup.delete()
            Result.success(Unit)
        } catch (e: Exception) {
            if (fileToCleanup != null && fileToCleanup.exists()) fileToCleanup.delete()
            Result.failure(e)
        }
    }
}