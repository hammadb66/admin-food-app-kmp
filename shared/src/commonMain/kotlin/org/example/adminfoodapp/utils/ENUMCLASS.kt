package org.example.adminfoodapp.utils

enum class ORDERSTATUS(val id : Long) {
PENDING(0L),COMPLETE(1L)
}

enum class TABLESTATUS(val id :Long) {
    AVAILABLE(0L),OCCUPIED(1L)
}

enum class ORDERTYPE(val id :Long) {
    DINEIN(0L),TAKEAWAY(1)
}