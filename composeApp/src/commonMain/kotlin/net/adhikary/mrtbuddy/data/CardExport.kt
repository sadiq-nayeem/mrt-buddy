package net.adhikary.mrtbuddy.data

import kotlinx.serialization.Serializable

@Serializable
data class CardExport(
    val version: String = "1.0",
    val exportDate: Long,
    val cards: List<CardExportData>
)

@Serializable
data class CardExportData(
    val idm: String,
    val name: String?,
    val lastScanTime: Long?,
    val transactions: List<TransactionExportData>
)

@Serializable
data class TransactionExportData(
    val cardIdm: String,
    val fromStation: String,
    val toStation: String,
    val balance: Int,
    val dateTime: Long,
    val fixedHeader: String,
    val order: Int = 0
)