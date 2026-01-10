package org.transactions_task.service

import org.transactions_task.api.dto.TransactionsGetResponseDTO

class GetTransactionsService {

    // TODO not DTO ... another structure and them mapper to DTO in routing
    // TODO rename
    data class Result(val transactions: TransactionsGetResponseDTO)

    fun getTransactions(): Result  {

        // TODO implement

        val responseDTO = TransactionsGetResponseDTO(
            listOf(
                TransactionsGetResponseDTO.TransactionDTO( "2023-01-11T03:00:01Z", 1000, "description", false)
            )
        )

        return Result(responseDTO)
    }
}
