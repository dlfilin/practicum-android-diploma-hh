package ru.practicum.android.diploma.favorites.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.util.Formatter
import ru.practicum.android.diploma.databinding.VacancyViewItemBinding
import ru.practicum.android.diploma.vacancy.domain.models.Vacancy

class VacancyViewHolder(
    private val binding: VacancyViewItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(vacancy: Vacancy, onClickListener: VacancyAdapter.OnClickListener) {
        with(binding) {
            Glide.with(itemView)
                .load(vacancy.employerLogoUrl)
                .placeholder(R.drawable.vacancy_item_placeholder)
                .transform(
                    CenterInside(),
                    RoundedCorners(
                        itemView.resources.getDimensionPixelSize(R.dimen.size_12)
                    ),
                )
                .into(binding.employerLogoIv)

            vacancyNameAndCity.text = buildString {
                append(vacancy.vacancyName)
                vacancy.area.takeIf { it.isNotEmpty() }?.let {
                    append(", $it")
                }
            }

            companyName.text = vacancy.employerName

            salary.text = Formatter.formatSalary(
                context = itemView.context,
                from = vacancy.salaryFrom,
                to = vacancy.salaryTo,
                currencyCode = vacancy.salaryCurrency
            )
        }

        itemView.setOnClickListener {
            onClickListener.onVacancyClick(vacancy)
        }
    }
}
