package net.adhikary.mrtbuddy.ui.screens.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.changeLang
import net.adhikary.mrtbuddy.repository.CardExportRepository
import net.adhikary.mrtbuddy.repository.ImportResult
import net.adhikary.mrtbuddy.repository.SettingsRepository
import net.adhikary.mrtbuddy.service.ClipboardService

class MoreScreenViewModel(
    private val settingsRepository: SettingsRepository,
    private val cardExportRepository: CardExportRepository,
    private val clipboardService: ClipboardService
) : ViewModel() {

    private val _state = MutableStateFlow(MoreScreenState())
    val state: StateFlow<MoreScreenState> get() = _state.asStateFlow()

    private val _events = Channel<MoreScreenEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        onAction(MoreScreenAction.OnInit)
    }

    fun onAction(action: MoreScreenAction) {
        when (action) {
            is MoreScreenAction.OnInit -> {
                viewModelScope.launch {
                    try {
                        val autoSaveEnabled = settingsRepository.autoSaveEnabled.value
                        val currentLanguage = settingsRepository.currentLanguage.value
                        _state.value = _state.value.copy(
                            autoSaveEnabled = autoSaveEnabled,
                            currentLanguage = currentLanguage
                        )
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(error = e.message)
                        _events.send(MoreScreenEvent.Error(e.message ?: "Unknown error"))
                    }
                }
            }

            is MoreScreenAction.SetAutoSave -> {
                viewModelScope.launch {
                    try {
                        settingsRepository.setAutoSave(action.enabled)
                        _state.value = _state.value.copy(autoSaveEnabled = action.enabled)
                    } catch (e: Exception) {
                        _events.send(MoreScreenEvent.Error(e.message ?: "Failed to update setting"))
                    }
                }
            }

            is MoreScreenAction.OpenLicenses -> {
                viewModelScope.launch {
                    _events.send(MoreScreenEvent.NavigateToLicenses)
                }
            }

            is MoreScreenAction.SetLanguage -> {
                viewModelScope.launch {
                    try {
                        changeLang(action.language)
                        settingsRepository.setLanguage(action.language)
                        _state.value = _state.value.copy(currentLanguage = action.language)
                    } catch (e: Exception) {
                        _events.send(MoreScreenEvent.Error(e.message ?: "Failed to change language"))
                    }
                }
            }

            is MoreScreenAction.StationMap -> {
                viewModelScope.launch {
                    _events.send(MoreScreenEvent.NavigateTooStationMap)
                }
            }

            is MoreScreenAction.ExportCards -> {
                viewModelScope.launch {
                    try {
                        val jsonData = cardExportRepository.exportAllCards()
                        _events.send(MoreScreenEvent.CardsExported(jsonData))
                    } catch (e: Exception) {
                        _events.send(MoreScreenEvent.Error(e.message ?: "Failed to export cards"))
                    }
                }
            }

            is MoreScreenAction.ImportCards -> {
                viewModelScope.launch {
                    try {
                        val result = cardExportRepository.importCards(action.jsonData)
                        _events.send(MoreScreenEvent.CardsImported(result))
                    } catch (e: Exception) {
                        _events.send(MoreScreenEvent.Error(e.message ?: "Failed to import cards"))
                    }
                }
            }
        }
    }

    suspend fun copyToClipboard(text: String): Boolean {
        return clipboardService.copyToClipboard(text)
    }
}
