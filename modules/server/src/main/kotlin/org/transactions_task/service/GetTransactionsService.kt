package org.transactions_task.service

import org.transactions_task.api.dto.TransactionsGetResponseDTO
import org.transactions_task.repository.TransactionsRepository

class GetTransactionsService(
    private val transactionsRepository: TransactionsRepository
) {

    // TODO not DTO ... another structure and them mapper to DTO in routing
    // TODO rename
    data class Result(val transactions: TransactionsGetResponseDTO)

    fun getTransactions(): Result  {

        val sortedTransactionsResult = transactionsRepository.getSortedTransactions()

        val responseDTO = TransactionsGetResponseDTO(
            sortedTransactionsResult.sortedTransactions.map {
                TransactionsGetResponseDTO.TransactionDTO(
                    timestamp = it.timestamp.toString(), // TODO date time format
                    amount = it.amount,
                    description = it.description ?: "",
                    isBiggest = it.amount == sortedTransactionsResult.maxAmount
                )
            }
        )

        return Result(responseDTO)
    }
}
