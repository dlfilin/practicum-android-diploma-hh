package ru.practicum.android.diploma.common.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.common.data.network.dto.Response
import ru.practicum.android.diploma.common.util.ErrorType
import ru.practicum.android.diploma.common.util.Result
import ru.practicum.android.diploma.filter.data.dto.AreaRequest
import ru.practicum.android.diploma.filter.data.dto.CountryRequest
import ru.practicum.android.diploma.filter.data.dto.IndustryRequest
import ru.practicum.android.diploma.search.data.dto.VacancySearchRequest
import ru.practicum.android.diploma.vacancy.data.dto.SimilarVacancyRequest
import ru.practicum.android.diploma.vacancy.data.dto.VacancyDetailRequest
import java.io.IOException

class RetrofitNetworkClient(
    private val context: Context,
    private val hhApiService: HhApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Result<Response> {
        if (!isConnected()) return Result.Error(ErrorType.NO_INTERNET)

        return withContext(Dispatchers.IO) {
            try {
                delay(3000) // some delay to test loading state

                val response = when (dto) {
                    is VacancySearchRequest -> hhApiService.searchVacancies(dto.options)
                    is VacancyDetailRequest -> hhApiService.getVacancyDetails(dto.vacancyId)
                    is SimilarVacancyRequest -> hhApiService.searchSimilarVacancies(dto.vacancyId)
                    is AreaRequest -> hhApiService.getAllAreas()
                    is CountryRequest -> hhApiService.getCountries()
                    is IndustryRequest -> hhApiService.getAllIndustries()
                    else -> throw Exception("BAD_REQUEST")
                }
                Result.Success(response)
            } catch (e: IOException) {
                Result.Error(ErrorType.SERVER_THROWABLE)
            } catch (exception: HttpException) {
                Result.Error(ErrorType.NON_200_RESPONSE)
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        return false
    }
}
