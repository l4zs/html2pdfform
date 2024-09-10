package de.l4zs.html2pdfform.config

import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.BaseFont
import de.l4zs.html2pdfform.data.HeaderFooter
import de.l4zs.html2pdfform.data.Intro
import de.l4zs.html2pdfform.data.Metadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Config(
    @SerialName("page_type")
    val pageType: String = "A4",
    @SerialName("page_padding_x")
    val pagePaddingX: Float = 50f,
    @SerialName("page_padding_y")
    val pagePaddingY: Float = 50f,
    @SerialName("group_padding_x")
    val groupPaddingX: Float = 0f,
    @SerialName("group_padding_y")
    val groupPaddingY: Float = 20f,
    @SerialName("inner_padding_x")
    val innerPaddingX: Float = 5f,
    @SerialName("inner_padding_y")
    val innerPaddingY: Float = 5f,
    @SerialName("font")
    val font: String = BaseFont.TIMES_ROMAN,
    @SerialName("font_size")
    val fontSize: Float = 20f,
    @SerialName("select_size")
    val selectSize: Int = 4,
    @SerialName("textarea_rows")
    val textareaRows: Int = 3,
    @SerialName("max_radios_per_row")
    val maxRadiosPerRow: Int = 4,
    @SerialName("first_page_header")
    val firstPageHeader: HeaderFooter? = null,
    @SerialName("first_page_footer")
    val firstPageFooter: HeaderFooter? = null,
    val header: HeaderFooter = HeaderFooter("", "", false),
    val footer: HeaderFooter = HeaderFooter("Seite", "", true),
    val metadata: Metadata = Metadata("", "", ""),
    val intro: Intro = Intro(null, null),
)

val pageSizes = listOf(
    PageSize.A3 to "A3",
    PageSize.A4 to "A4",
    PageSize.A5 to "A5",
)

val Rectangle.pageType
get() = pageSizes.firstOrNull { it.first.width == width && it.first.height == height }?.second ?: "A4"

val Config.pageSize: Rectangle
    get() = pageSizes.firstOrNull { it.second == pageType }?.first ?: PageSize.A4

val Config.inputWidth: Float
get() = pageSize.width - 2 * pagePaddingX
val Config.textRectPadding: Float
    get() = 0.01f
val Config.boxSize: Float
    get() = fontSize
val Config.pageMinX
    get() = pagePaddingX
val Config.pageMaxX
    get() = pageSize.width - pagePaddingX
val Config.pageMinY
    get() = pagePaddingY
val Config.pageMaxY
    get() = pageSize.height - pagePaddingY
val Config.effectivePageWidth
    get() = pageMaxX - pageMinX
val Config.effectivePageHeight
    get() = pageMaxY - pageMinY

val Config.baseFont: BaseFont
    get() = BaseFont.createFont(font, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
val Config.introFont: BaseFont
    get() = BaseFont.createFont(
    intro.text?.font ?: font,
    BaseFont.CP1252,
    BaseFont.NOT_EMBEDDED
)
val Config.defaultFont
    get() = Font(baseFont, fontSize)

lateinit var config: Config
    internal set

lateinit var configFile: File
    private set

private val filepath = System.getenv("APPDATA") + "\\html2pdfform\\config.json"

fun Config.Companion.loadConfig() {
    configFile = File(filepath)
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
        config = Config()
        configFile.writeText(Json.encodeToString(config))
    } else {
        config = Json.decodeFromString<Config>(configFile.readText())
    }
}
fun Config.Companion.saveConfig() {
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
    }
    configFile.writeText(Json.encodeToString(config))
}
