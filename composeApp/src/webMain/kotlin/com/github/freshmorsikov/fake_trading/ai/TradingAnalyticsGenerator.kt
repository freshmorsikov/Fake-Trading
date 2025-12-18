package com.github.freshmorsikov.fake_trading.ai

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.structure.executeStructured
import com.github.freshmorsikov.fake_trading.api.model.StockRow
import fake_trading.composeApp.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
data class TradingAnalyticsResponse(
    @property:LLMDescription("Список аналитических прогнозов, каждый из которых соответствует одной компании")
    val tradingAnalytics: List<TradingAnalytics>,
)

@Serializable
class TradingAnalytics(
    @property:LLMDescription("Уникальный идентификатор (id) компании")
    val companyId: Int,

    @property:LLMDescription("Влияние на компанию")
    val priceImpact: PriceImpact,

    @property:LLMDescription("Небольшое объяснение анализа влияния")
    val shortNote: String,
)

@Serializable
enum class PriceImpact {
    @LLMDescription("Сильное негативное влияние")
    StronglyNegative,

    @LLMDescription("Среднее негативное влияние")
    ModeratelyNegative,

    @LLMDescription("Слабое негативное влияние")
    SlightlyNegative,

    @LLMDescription("Нет никакого влияния")
    None,

    @LLMDescription("Слабое позитивное влияние")
    SlightlyPositive,

    @LLMDescription("Среднее позитивное влияние")
    ModeratelyPositive,

    @LLMDescription("Сильное позитивное влияние")
    StronglyPositive,
}

class TradingAnalyticsGenerator {

    private val tradingAnalyticsExamples by lazy {
        listOf(
            TradingAnalyticsResponse(
                tradingAnalytics = listOf(
                    TradingAnalytics(
                        companyId = 0,
                        priceImpact = PriceImpact.ModeratelyPositive,
                        shortNote = "Повышенный интерес к ИИ и эмоциональным системам управления усиливает спрос на автономные аппараты.",
                    ),
                    TradingAnalytics(
                        companyId = 1,
                        priceImpact = PriceImpact.SlightlyNegative,
                        shortNote = "Открытие энерговырабатывающих микроорганизмов в Исландии создает риск появления новых конкурентов.",
                    ),
                    TradingAnalytics(
                        companyId = 2,
                        priceImpact = PriceImpact.StronglyPositive,
                        shortNote = "Новость о микроорганизмах, генерирующих энергию из тумана, вызывает всплеск интереса к новым нетрадиционным источникам энергии.",
                    ),
                    TradingAnalytics(
                        companyId = 3,
                        priceImpact = PriceImpact.StronglyPositive,
                        shortNote = "Тестирование «эмоциональных светофоров» в Японии напрямую связано с их сферой. И компания может интегрировать такие решения в свои проекты.",
                    ),
                    TradingAnalytics(
                        companyId = 4,
                        priceImpact = PriceImpact.ModeratelyPositive,
                        shortNote = "Эмоциональный ИИ усиливает интерес к анализу человеческого поведения, что расширяет рынок для поведенческих риск-моделей.",
                    ),
                    TradingAnalytics(
                        companyId = 5,
                        priceImpact = PriceImpact.None,
                        shortNote = "Новости не затрагивают напрямую медицину.",
                    ),
                    TradingAnalytics(
                        companyId = 6,
                        priceImpact = PriceImpact.SlightlyPositive,
                        shortNote = "Улучшение ИИ-технологий косвенно повышает эффективность автономных доставок.",
                    ),
                    TradingAnalytics(
                        companyId = 7,
                        priceImpact = PriceImpact.ModeratelyPositive,
                        shortNote = "Сервис «аренды снов» усиливает тренд на персонализированные впечатления.",
                    ),
                    TradingAnalytics(
                        companyId = 8,
                        priceImpact = PriceImpact.ModeratelyNegative,
                        shortNote = "Появление новых источников энергии снижает спрос на редкие металлы.",
                    ),
                    TradingAnalytics(
                        companyId = 9,
                        priceImpact = PriceImpact.SlightlyPositive,
                        shortNote = "Сервис «аренды снов» вызывает бум в индустрии сюжетного опыта.",
                    ),
                )
            )
        )
    }

    suspend fun generateTradingAnalytics(
        stocks: List<StockRow>,
        news: List<String>,
    ): List<TradingAnalytics>? {
        val promtExecutor = simpleOpenAIExecutor(BuildConfig.OPENAI_API_KEY)

        val companiesList = StringBuilder()
        stocks.forEachIndexed { index, company ->
            companiesList.append("(id = ${index}) ${company.name} — ${company.description}. ")
        }

        val newsList = news.joinToString(separator = " / ") { it }

        val generatedAnalytics = promtExecutor.executeStructured(
            prompt = prompt("structured-data") {
                system(
                    """
                            Вы — аналитический агент с фантазией, специализирующийся на креативной оценке ситуации на рынке акций. 
                            На рынке представлены компании: $companiesList
            
                            Пользователь присылает вам выдуманные новости.
            
                            Ваши задачи:
                            Анализировать предоставленные новости, не обращая внимание на их саркастичность/ироничность/камичность и тд. 
                            Будьте креативны — иногда новости влияют на рынок не самым очевидным образом, как в минус, так и в плюс.
                            Строить причинно-следственные выводы о возможном влиянии новостей на котировки акций вымышленных компаний.
                            Выбрать не больше 3-5 компаний, которые наиболее вероятно будут восприимчивы к данным новостям.
                            Давать структурированный аналитический ответ: id компании, влияние на компанию, небольшое объяснение анализа влияния.
                            Опираться исключительно на данные, присланные пользователем, не дополняя их реальными компаниями или реальными новостями.
                        """.trimIndent(),
                )
                user("Вот новости: $newsList. Проанализируй ситуацию на рынке")
            },
            model = OpenAIModels.Chat.GPT5_1,
            examples = tradingAnalyticsExamples,
        )

        return generatedAnalytics.getOrNull()?.structure?.tradingAnalytics
    }

}