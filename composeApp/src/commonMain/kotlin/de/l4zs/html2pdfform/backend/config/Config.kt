package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Font
import de.l4zs.html2pdfform.backend.data.PageSize
import de.l4zs.html2pdfform.util.Logger
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Config(
    @SerialName("page_type")
    @EncodeDefault
    val pageType: String = PageSize.A4.translationKey,
    @SerialName("page_padding_x")
    @EncodeDefault
    val pagePaddingX: Float = 50f,
    @SerialName("page_padding_y")
    @EncodeDefault
    val pagePaddingY: Float = 50f,
    @SerialName("group_padding_x")
    @EncodeDefault
    val groupPaddingX: Float = 0f,
    @SerialName("group_padding_y")
    @EncodeDefault
    val groupPaddingY: Float = 20f,
    @SerialName("inner_padding_x")
    @EncodeDefault
    val innerPaddingX: Float = 5f,
    @SerialName("inner_padding_y")
    @EncodeDefault
    val innerPaddingY: Float = 5f,
    @SerialName("font")
    @EncodeDefault
    val font: Font = Font.TIMES_ROMAN,
    @SerialName("font_size")
    @EncodeDefault
    val fontSize: Float = 20f,
    @SerialName("select_size")
    @EncodeDefault
    val selectSize: Int = 4,
    @SerialName("textarea_rows")
    @EncodeDefault
    val textareaRows: Int = 3,
    @SerialName("max_radios_per_row")
    @EncodeDefault
    val maxRadiosPerRow: Int = 4,
    @EncodeDefault
    val header: HeaderFooter = HeaderFooter("", "", false),
    @EncodeDefault
    val footer: HeaderFooter = HeaderFooter("Seite", "", true),
    @SerialName("first_page_header_enabled")
    @EncodeDefault
    val firstPageHeaderEnabled: Boolean = true,
    @SerialName("first_page_footer_enabled")
    @EncodeDefault
    val firstPageFooterEnabled: Boolean = true,
    @SerialName("first_page_header")
    @EncodeDefault
    val firstPageHeader: HeaderFooter? = null,
    @SerialName("first_page_footer")
    @EncodeDefault
    val firstPageFooter: HeaderFooter? = null,
    @EncodeDefault
    val metadata: Metadata = Metadata("", "", ""),
    @EncodeDefault
    val intro: Intro = Intro(imageEnabled = false, textEnabled = false, image = null, text = null),
    @EncodeDefault
    val language: Language = Language.GERMAN,
) {
    @Transient
    val pageSize = PageSize.of(pageType) ?: PageSize.A4

    @Transient
    val inputWidth = pageSize.width - 2 * pagePaddingX

    @Transient
    val boxSize = fontSize

    @Transient
    val pageMinX = pagePaddingX

    @Transient
    val pageMaxX = pageSize.width - pagePaddingX

    @Transient
    val pageMinY = pagePaddingY

    @Transient
    val pageMaxY = pageSize.height - pagePaddingY

    @Transient
    val effectivePageWidth = pageMaxX - pageMinX

    @Transient
    val effectivePageHeight = pageMaxY - pageMinY
}

expect fun configFile(): File

expect suspend fun saveConfigToFile(
    config: Config,
    logger: Logger,
)

expect suspend fun loadConfigFromFile(logger: Logger): Config

expect suspend fun loadConfigFromFile(
    logger: Logger,
    path: String,
): Config?
