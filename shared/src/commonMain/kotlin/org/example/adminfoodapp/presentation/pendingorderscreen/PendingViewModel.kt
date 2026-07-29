package org.example.adminfoodapp.presentation.pendingorderscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.domain.usecase.CancelConfirmOrderUseCase
import org.example.adminfoodapp.domain.usecase.CancelOrderUseCase
import org.example.adminfoodapp.domain.usecase.GetPendingOrderUseCase
import org.example.adminfoodapp.domain.usecase.OrderAcceptedUseCase
import org.example.adminfoodapp.domain.usecase.OrderDispatchUseCase
import org.example.adminfoodapp.domain.usecase.SendOrderNotificationUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PendingViewModel  (
    val getPendingOrderUseCase: GetPendingOrderUseCase,
    val orderAcceptedUseCase: OrderAcceptedUseCase,
    val orderDispatchUseCase: OrderDispatchUseCase,
    val cancelOrderUseCase: CancelOrderUseCase,
    val cancelConfirmOrderUseCase: CancelConfirmOrderUseCase,
    private val sendOrderNotificationUseCase: SendOrderNotificationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<Resource<MutableList<OrderDetails>>>(Resource.Loading)
    val state = _state
    private val _stateOrder = MutableStateFlow<Resource<Unit>?>(null)
    val stateOrder = _stateOrder

    private val _cancelOrderOrCancelConfirm = MutableSharedFlow<Resource<Unit>>(replay = 0)
    val cancelOrderOrCancelConfirm = _cancelOrderOrCancelConfirm.asSharedFlow()

    init {
        getOrderHistory()
    }


    fun getOrderHistory() {
        viewModelScope.launch {
            getPendingOrderUseCase.invoke().collect { state ->
                _state.value = state
            }
        }
    }

    fun orderAccepted(orderDetails: OrderDetails) {
        viewModelScope.launch {
            orderAcceptedUseCase.invoke(orderDetails).collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        _stateOrder.value = Resource.Loading
                    }

                    is Resource.Success -> {
                        _stateOrder.value = Resource.Success(Unit)
                        // ✅ Success hone par notification bhejhein
                        /*    orderDetails.userId?.let { uid ->
                                sendOrderNotificationUseCase(uid, OrderStatus.ACCEPTED)
                            }*/

                    }

                    is Resource.Error -> {
                        _stateOrder.value = Resource.Error(state.message)

                    }
                }
            }
        }
    }

    fun orderDispatch(orderDetails: OrderDetails) {
        viewModelScope.launch {
            orderDispatchUseCase.invoke(orderDetails).collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        _stateOrder.value = Resource.Loading

                    }

                    is Resource.Success -> {
                        _stateOrder.value = Resource.Success(Unit)
                        // ✅ Notification trigger here
                        /*    orderDetails.userId?.let { uid ->
                                sendOrderNotificationUseCase(uid, OrderStatus.DISPATCHED)
                            }*/

                    }

                    is Resource.Error -> {
                        _stateOrder.value = Resource.Error(state.message)

                    }
                }
            }
        }
    }

    fun cancelOrder(orderDetails: OrderDetails) {
        viewModelScope.launch {
            cancelOrderUseCase.invoke(orderDetails).collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        _stateOrder.value = Resource.Loading
                    }

                    is Resource.Success -> {
                        _stateOrder.value = Resource.Success(Unit)
                        // ✅ Notification trigger here
                        /*     orderDetails.userId?.let { uid ->
                                 sendOrderNotificationUseCase(uid, OrderStatus.CANCELLED)
                             }*/
                    }

                    is Resource.Error -> {
                        _stateOrder.value = Resource.Error(state.message)

                    }
                }
            }
        }

    }

    fun cancelConfirm(orderDetails: OrderDetails) {
        viewModelScope.launch {
            cancelConfirmOrderUseCase.invoke(orderDetails).collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        _stateOrder.value = Resource.Loading
                    }

                    is Resource.Success -> {
                        _stateOrder.value = Resource.Success(Unit)
                        // ✅ Notification trigger here
                        /*  orderDetails.userId?.let { uid ->
                              sendOrderNotificationUseCase(uid, OrderStatus.CANCELLED)
                          }*/
                    }

                    is Resource.Error -> {
                        _stateOrder.value = Resource.Error(state.message)

                    }
                }
            }
        }
    }
}