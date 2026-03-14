package ru.mishlak.rksmp_pr1_12
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.random.Random

class FactAnimalViewModel : ViewModel() {

	private val facts = listOf(
		"Венерин пеньчатый кит: Это самое большое существо на Земле. Длина до 30 м, вес до 200 т.",
		"Креветка-мантис: Удар со скоростью 23 м/с — может разбить стекло аквариума.",
		"Большая панда: Ест до 14 часов в день, потребляя более 12 кг бамбука.",
		"Гигантский кальмар: Длина до 13 м, глаза диаметром более 25 см.",
		"Слон: Обладает самым развитым мозгом, понимает человеческую речь, испытывает чувства.",
		"Медуза Turritopsis nutricula: Биологически бессмертна — может омолаживаться.",
		"Щитоносный жук: Выпускает горячую кислотную жидкость для защиты.",
		"Попугай какаду: Некоторые виды доживают до 100 лет.",
		"Сова: Может поворачивать голову на 270° без повреждения сосудов.",
		"Мурена: Имеет вторую пару челюстей в глотке для заглатывания пищи.",
		"Колибри: Единственная птица, умеющая летать задом наперёд.",
		"Осьминог: Имеет три сердца и голубую кровь.",
		"Африканский слон: Уши помогают охлаждаться — через них проходит до 100 литров крови в день.",
		"Ленивец: Может задерживать дыхание под водой дольше, чем дельфин (до 40 мин)."
	)

	private val _fact = MutableStateFlow<String?>(null)
	val fact: StateFlow<String?> = _fact.asStateFlow()
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
	fun getRandomFact(): Flow<String> = flow {
		val randomIndex = Random.nextInt(facts.size)
		val randomFact = facts[randomIndex]
		val delayMs = Random.nextLong(1500, 3000)
		delay(delayMs)
		emit(randomFact)
	}

	fun loadNewFact() {
		if (_isLoading.value) return
		viewModelScope.launch {
			_isLoading.value = true
			try {
				getRandomFact().collect { newFact ->
					_fact.value = newFact
				}
			} finally {
				_isLoading.value = false
			}
		}
	}
}