package com.github.freshmorsikov.fake_trading.ai

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.structure.executeStructured
import com.github.freshmorsikov.fake_trading.api.model.StockRow
import fake_trading.composeApp.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TradingAnalyticsResponse")
data class TradingAnalyticsResponse(
    @property:LLMDescription("Список аналитических прогнозов, каждый из которых соответствует одной компании")
    val tradingAnalytics: List<TradingAnalytics>,
)

@Serializable
@SerialName("TradingAnalytics")
class TradingAnalytics(
    @property:LLMDescription("Уникальный идентификатор (id) компании")
    val companyId: Int,

    @property:LLMDescription("Влияние на компанию в процентах")
    val percentChange: Int,

    @property:LLMDescription("Небольшое объяснение влияния")
    val shortNote: String,
)

class TradingAnalyticsGenerator {

    private val tradingAnalyticsExamples by lazy {
        listOf(
            TradingAnalyticsResponse(
                tradingAnalytics = listOf(
                    TradingAnalytics(
                        companyId = 0,
                        percentChange = 8,
                        shortNote = "Повышенный интерес к ИИ и эмоциональным системам управления усиливает спрос на автономные аппараты.",
                    ),
                    TradingAnalytics(
                        companyId = 1,
                        percentChange = -5,
                        shortNote = "Открытие энерговырабатывающих микроорганизмов в Исландии создает риск появления новых конкурентов.",
                    ),
                    TradingAnalytics(
                        companyId = 2,
                        percentChange = 15,
                        shortNote = "Новость о микроорганизмах, генерирующих энергию из тумана, вызывает всплеск интереса к новым нетрадиционным источникам энергии.",
                    ),
                    TradingAnalytics(
                        companyId = 3,
                        percentChange = 21,
                        shortNote = "Тестирование «эмоциональных светофоров» в Японии напрямую связано с их сферой. И компания может интегрировать такие решения в свои проекты.",
                    ),
                    TradingAnalytics(
                        companyId = 4,
                        percentChange = 10,
                        shortNote = "Эмоциональный ИИ усиливает интерес к анализу человеческого поведения, что расширяет рынок для поведенческих риск-моделей.",
                    ),
                    TradingAnalytics(
                        companyId = 5,
                        percentChange = 0,
                        shortNote = "Новости не затрагивают напрямую медицину.",
                    ),
                    TradingAnalytics(
                        companyId = 6,
                        percentChange = 7,
                        shortNote = "Улучшение ИИ-технологий косвенно повышает эффективность автономных доставок.",
                    ),
                    TradingAnalytics(
                        companyId = 7,
                        percentChange = 12,
                        shortNote = "Сервис «аренды снов» усиливает тренд на персонализированные впечатления.",
                    ),
                    TradingAnalytics(
                        companyId = 8,
                        percentChange = -6,
                        shortNote = "Появление новых источников энергии может снизить спрос на редкие металлы.",
                    ),
                    TradingAnalytics(
                        companyId = 9,
                        percentChange = 20,
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
//        val newsStructure = JsonStructuredData.createJsonStructure<TradingAnalyticsResponse>(
//            schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
//            examples = tradingAnalyticsExamples,
//            schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
//        )

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
                            Анализировать предоставленные новости.
                            Будьте креативны — иногда новости влияют на рынок не самым очевидным образом.
                            Строить причинно-следственные выводы о возможном влиянии новостей на котировки акций вымышленных компаний.
                            Давать структурированный аналитический ответ: id компании, влияние в процентах (например: +18%, -5%, 0%), небольшое объяснение влияния.
                            Опираться исключительно на данные, присланные пользователем, не дополняя их реальными компаниями или реальными новостями.
                        """.trimIndent(),
                )
                user("Вот новости: $newsList. Проанализируй ситуацию на рынке")
            },
            model = OpenAIModels.Chat.GPT4_1,
            examples = tradingAnalyticsExamples,
            //structure = newsStructure,
        )

        return generatedAnalytics.getOrNull()?.structure?.tradingAnalytics
    }

}