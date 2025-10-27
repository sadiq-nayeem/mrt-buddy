package net.adhikary.mrtbuddy.repository

import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.CardExport
import net.adhikary.mrtbuddy.data.CardExportData
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.data.TransactionExportData

class CardExportRepository(
    private val cardDao: net.adhikary.mrtbuddy.dao.CardDao,
    private val transactionDao: net.adhikary.mrtbuddy.dao.TransactionDao
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend fun exportAllCards(): String {
        val cards = cardDao.getAllCards()
        val cardExportData = cards.map { card ->
            val transactions = transactionDao.getTransactionsByCardIdm(card.idm)
            CardExportData(
                idm = card.idm,
                name = card.name,
                lastScanTime = card.lastScanTime,
                transactions = transactions.map { transaction ->
                    TransactionExportData(
                        cardIdm = transaction.cardIdm,
                        fromStation = transaction.fromStation,
                        toStation = transaction.toStation,
                        balance = transaction.balance,
                        dateTime = transaction.dateTime,
                        fixedHeader = transaction.fixedHeader,
                        order = transaction.order
                    )
                }
            )
        }

        val exportData = CardExport(
            version = "1.0",
            exportDate = Clock.System.now().epochSeconds,
            cards = cardExportData
        )

        return json.encodeToString(exportData)
    }

    suspend fun importCards(jsonData: String): ImportResult {
        return try {
            val exportData = json.decodeFromString<CardExport>(jsonData)
            val importedCards = mutableListOf<String>()
            val skippedCards = mutableListOf<String>()
            val errors = mutableListOf<String>()

            for (cardData in exportData.cards) {
                try {
                    // Check if card already exists
                    val existingCard = cardDao.getCardByIdm(cardData.idm)

                    if (existingCard != null) {
                        skippedCards.add(cardData.name ?: cardData.idm)
                        continue
                    }

                    // Insert card
                    val cardEntity = CardEntity(
                        idm = cardData.idm,
                        name = cardData.name,
                        lastScanTime = cardData.lastScanTime
                    )
                    cardDao.insertCard(cardEntity)

                    // Insert transactions
                    val transactionEntities = cardData.transactions.map { transactionData ->
                        TransactionEntity(
                            cardIdm = transactionData.cardIdm,
                            scanId = 0, // We'll use 0 for imported transactions
                            fromStation = transactionData.fromStation,
                            toStation = transactionData.toStation,
                            balance = transactionData.balance,
                            dateTime = transactionData.dateTime,
                            fixedHeader = transactionData.fixedHeader,
                            order = transactionData.order
                        )
                    }

                    if (transactionEntities.isNotEmpty()) {
                        transactionDao.insertTransactions(transactionEntities)
                    }

                    importedCards.add(cardData.name ?: cardData.idm)
                } catch (e: Exception) {
                    errors.add("Failed to import card ${cardData.name ?: cardData.idm}: ${e.message}")
                }
            }

            ImportResult(
                success = errors.isEmpty(),
                importedCards = importedCards,
                skippedCards = skippedCards,
                errors = errors
            )
        } catch (e: Exception) {
            ImportResult(
                success = false,
                importedCards = emptyList(),
                skippedCards = emptyList(),
                errors = listOf("Failed to parse import data: ${e.message}")
            )
        }
    }

    suspend fun exportSingleCard(cardIdm: String): String? {
        return try {
            val card = cardDao.getCardByIdm(cardIdm) ?: return null
            val transactions = transactionDao.getTransactionsByCardIdm(cardIdm)

            val cardExportData = CardExportData(
                idm = card.idm,
                name = card.name,
                lastScanTime = card.lastScanTime,
                transactions = transactions.map { transaction ->
                    TransactionExportData(
                        cardIdm = transaction.cardIdm,
                        fromStation = transaction.fromStation,
                        toStation = transaction.toStation,
                        balance = transaction.balance,
                        dateTime = transaction.dateTime,
                        fixedHeader = transaction.fixedHeader,
                        order = transaction.order
                    )
                }
            )

            val exportData = CardExport(
                version = "1.0",
                exportDate = Clock.System.now().epochSeconds,
                cards = listOf(cardExportData)
            )

            json.encodeToString(exportData)
        } catch (e: Exception) {
            null
        }
    }
}

data class ImportResult(
    val success: Boolean,
    val importedCards: List<String>,
    val skippedCards: List<String>,
    val errors: List<String>
)