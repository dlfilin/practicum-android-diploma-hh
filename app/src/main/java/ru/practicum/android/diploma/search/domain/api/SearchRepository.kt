package ru.practicum.android.diploma.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.util.NetworkResult
import ru.practicum.android.diploma.filter.domain.models.FilterParameters
import ru.practicum.android.diploma.search.domain.model.VacancyListData

interface SearchRepository {
    fun searchVacancies(text: String, filter: FilterParameters): Flow<NetworkResult<VacancyListData>>
    fun getSimilarVacancies(vacancyId: String): Flow<NetworkResult<VacancyListData>>
    fun getFilterParameters(): FilterParameters
}
