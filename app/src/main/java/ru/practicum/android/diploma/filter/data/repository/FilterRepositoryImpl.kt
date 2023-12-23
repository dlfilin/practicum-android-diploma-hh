package ru.practicum.android.diploma.filter.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.storage.FilterStorage
import ru.practicum.android.diploma.common.mappers.FilterMapper
import ru.practicum.android.diploma.common.util.NetworkResult
import ru.practicum.android.diploma.filter.data.dto.AreaRequest
import ru.practicum.android.diploma.filter.data.dto.AreaResponse
import ru.practicum.android.diploma.filter.data.dto.AreasByIdRequest
import ru.practicum.android.diploma.filter.data.dto.CountryRequest
import ru.practicum.android.diploma.filter.data.dto.CountryResponse
import ru.practicum.android.diploma.filter.data.dto.IndustryRequest
import ru.practicum.android.diploma.filter.data.dto.IndustryResponse
import ru.practicum.android.diploma.filter.domain.api.FilterRepository
import ru.practicum.android.diploma.filter.domain.models.Area
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.FilterParameters
import ru.practicum.android.diploma.filter.domain.models.Industry

class FilterRepositoryImpl(
    private val networkClient: NetworkClient,
    private val filterStorage: FilterStorage,
    private val filterMapper: FilterMapper
) : FilterRepository {
    override fun getAreas(): Flow<NetworkResult<List<Area>>> = flow {
        when (val result = networkClient.doRequest(AreaRequest())) {
            is NetworkResult.Success -> {
                val list = (result.data as AreaResponse).areas
                val flat = filterMapper.flattenAreas(list, emptyList())
                val data = flat
                    .asSequence()
                    .filterNot { it.parentId == null }
                    .map { filterMapper.mapToDomain(it) }
                    .sortedBy { it.name }
                    .toList()
                emit(NetworkResult.Success(data))
            }

            is NetworkResult.Error -> {
                emit(NetworkResult.Error(result.errorType!!))
            }
        }
    }

    override fun getAreasForId(id: String): Flow<NetworkResult<List<Area>>> = flow {
        when (val result = networkClient.doRequest(AreasByIdRequest(id))) {
            is NetworkResult.Success -> {
                val list = (result.data as AreaResponse).areas
                val flat = filterMapper.flattenAreas(list, emptyList())
                val data = flat
                    .asSequence()
                    .filterNot { it.parentId == null }
                    .map { filterMapper.mapToDomain(it) }
                    .sortedBy { it.name }
                    .toList()
                emit(NetworkResult.Success(data))
            }

            is NetworkResult.Error -> {
                emit(NetworkResult.Error(result.errorType!!))
            }
        }
    }

    override fun getCountries(): Flow<NetworkResult<List<Country>>> = flow {
        when (val result = networkClient.doRequest(CountryRequest())) {
            is NetworkResult.Success -> {
                val data = (result.data as CountryResponse).areas.map {
                    filterMapper.mapToDomain(it)
                }
                emit(NetworkResult.Success(data))
            }

            is NetworkResult.Error -> {
                emit(NetworkResult.Error(result.errorType!!))
            }
        }
    }

    override fun getIndustries(): Flow<NetworkResult<List<Industry>>> = flow {
        when (val result = networkClient.doRequest(IndustryRequest())) {
            is NetworkResult.Success -> {
                val list = (result.data as IndustryResponse).industry
                val data = filterMapper.flattenIndustries(list)
                emit(NetworkResult.Success(data))
            }

            is NetworkResult.Error -> {
                emit(NetworkResult.Error(result.errorType!!))
            }
        }
    }

    override fun getCurrentFilter(): FilterParameters {
        return filterMapper.mapToDomain(filterStorage.getFilterParameters())
    }

    override fun updateFilter(filter: FilterParameters) {
        filterStorage.saveFilterParameters(filterMapper.mapToDto(filter))
    }
}



