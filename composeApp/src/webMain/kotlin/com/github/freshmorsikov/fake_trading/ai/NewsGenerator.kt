package com.github.freshmorsikov.fake_trading.ai

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.structure.executeStructured
import fake_trading.composeApp.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NewsResponse")
data class NewsResponse(
    @property:LLMDescription("Заголовки новостей")
    val newsTitles: List<String>,
)

class NewsGenerator {

    private val newsExamples by lazy {
        listOf(
            NewsResponse(
                newsTitles = listOf(
                    "Министерство Тишины отменило все споры — теперь граждане шепчут только по расписанию",
                    "Армия выдала солдатам невидимую форму — теперь командиры ищут их по звуку унылых вздохов",
                    "Валюту “хрумбль” признали самой стабильной: она хрустит, даже когда дешевеет",
                    "Футбольная команда “Сонные Ежи” выиграла матч, не проснувшись ни разу",
                    "Новый смартфон сам пишет владельцу напоминания о том, как сильно он его переоценивает",
                    "Стартап по доставке вдохновения приносит клиентам пустые коробки — и это работает",
                    "Боевик “Удар в Никуда” собрал рекордные сборы среди тех, кто перепутал кинотеатр с библиотекой",
                    "Бегун установил рекорд, случайно заблудившись на трассе ультрамарафона",
                    "Город внедрил “умные” лавочки, которые спорят с прохожими о погоде",
                    "Учёные представили батарею, заряжающуюся от вибраций человеческого голоса",
                    "Робот-сапёр нового поколения обезвреживает мины… комплиментами",
                    "Биржа Тераполиса заморозила торги из-за слишком оптимистичных прогнозов",
                    "Команда “Штормовые Ястребы” выиграла матч, использовав стратегию, разработанную искусственным интеллектом во сне",
                    "Стартап представил смартфон, который отключает владельца от соцсетей при плохом настроении",
                    "Кулинары создали суп, вкус которого меняется в зависимости от музыки в помещении",
                    "Сеть магазинов ввела скидки за умение шутить с кассиром",
                    "Музыкальная группа “Корни Ветра” выпустила альбом без единого звука — и стала хитом",
                    "Город Неоротта запускает летающие велодорожки между небоскрёбами",
                    "Инженеры создали складной дом, который можно упаковать в чемодан и собрать за пять минут",
                )
            )
        )
    }

    suspend fun generateNews(count: Int): List<String>? {
        val promptExecutor = simpleOpenAIExecutor(BuildConfig.OPENAI_API_KEY)
        val generatedNews = promptExecutor.executeStructured(
            prompt = prompt("structured-data") {
                system(
                    """
                            Вы — агент, который генерирует выдуманные заголовки новостей в самых разных сферах: 
                            политика, наука, энергетика, военная, финансы, спорт, технологии, питание, бизнес, развлекательная, урбанистика и другие.
                
                            Ваши задачи:
                            Создавать полностью вымышленные заголовки новостей, не основанные на реальных людях/компаниях/событиях.
                            Делать новости смешными, оригинальными и неожиданными.
                            Каждая новость должна выглядеть как короткий, броский заголовок в СМИ.
                        """.trimIndent(),
                )
                user("Сгенерируй $count новостей")
            },
            model = OpenAIModels.Chat.GPT5_1,
            examples = newsExamples,
        )

        return generatedNews.getOrNull()?.structure?.newsTitles
    }

}