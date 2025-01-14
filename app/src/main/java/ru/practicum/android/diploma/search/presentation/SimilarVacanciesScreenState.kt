package ru.practicum.android.diploma.search.presentation

import ru.practicum.android.diploma.common.util.ErrorType
import ru.practicum.android.diploma.search.domain.model.VacancyListData

sealed interface SimilarVacanciesScreenState {
    data class Content(
        val vacancyData: VacancyListData,
        val isLoading: Boolean
    ) : SimilarVacanciesScreenState

    data object Loading : SimilarVacanciesScreenState
    data class Error(val error: ErrorType) : SimilarVacanciesScreenState
    data object Empty : SimilarVacanciesScreenState
}
