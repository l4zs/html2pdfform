package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Font
import de.l4zs.html2pdfform.backend.data.PageSize
import de.l4zs.html2pdfform.util.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
    val font: Font = Font.TIMES_ROMAN,
    @SerialName("font_size")
    val fontSize: Float = 20f,
    @SerialName("select_size")
    val selectSize: Int = 4,
    @SerialName("textarea_rows")
    val textareaRows: Int = 3,
    @SerialName("max_radios_per_row")
    val maxRadiosPerRow: Int = 4,
    val header: HeaderFooter = HeaderFooter("", "", false),
    val footer: HeaderFooter = HeaderFooter("Seite", "", true),
    @SerialName("first_page_header_enabled")
    val firstPageHeaderEnabled: Boolean = true,
    @SerialName("first_page_footer_enabled")
    val firstPageFooterEnabled: Boolean = true,
    @SerialName("first_page_header")
    val firstPageHeader: HeaderFooter? = null,
    @SerialName("first_page_footer")
    val firstPageFooter: HeaderFooter? = null,
    val metadata: Metadata = Metadata("", "", ""),
    val intro: Intro = Intro(imageEnabled = false, textEnabled = false, image = null, text = null),
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

expect fun saveConfigToFile(
    config: Config,
    logger: Logger,
)

expect fun loadConfigFromFile(logger: Logger): Config
