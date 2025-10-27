package net.adhikary.mrtbuddy.ui.screens.more

sealed interface MoreScreenEvent {
    data class Error(val message: String) : MoreScreenEvent
    object NavigateTooStationMap : MoreScreenEvent
    object NavigateToLicenses : MoreScreenEvent
    data class CardsExported(val jsonData: String) : MoreScreenEvent
    data class CardsImported(val result: net.adhikary.mrtbuddy.repository.ImportResult) : MoreScreenEvent
    object ShowImportDialog : MoreScreenEvent
}
