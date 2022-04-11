package com.example.applux.domain.usecases

import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdatePositionDS {


    operator fun invoke(position: DocumentSnapshot) : Flow<DocumentSnapshot> = flow {
        emit(position)
    }

}