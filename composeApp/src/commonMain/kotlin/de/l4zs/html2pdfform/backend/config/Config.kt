package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Font
import de.l4zs.html2pdfform.backend.data.Language
import de.l4zs.html2pdfform.backend.data.PageSize
import de.l4zs.html2pdfform.util.Logger
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File

/**
 * The Config stores all configuration options for the PDF generation and the application in general.
 * The class is serializable to JSON and can be saved and loaded from a file.
 * The class also contains some transient properties that are calculated based on the other properties.
 *
 * @constructor Create empty Config
 * @property pageType The page type of the PDF
 * @property pagePaddingX The padding on the left and right side of the page
 * @property pagePaddingY The padding on the top and bottom side of the page
 * @property groupPaddingX The horizontal padding between groups
 * @property groupPaddingY The vertical padding between groups
 * @property innerPaddingX The horizontal padding between elements in a group
 * @property innerPaddingY The vertical padding between elements in a group
 * @property font The font used for the PDF
 * @property fontSize The font size used for the PDF
 * @property selectSize The default size of select lists
 * @property textareaRows The default number of rows for the textarea elements
 * @property maxRadiosPerRow The maximum number of radio buttons per row
 * @property header The header of the PDF
 * @property footer The footer of the PDF
 * @property firstPageHeaderEnabled Whether the first page header is enabled
 * @property firstPageFooterEnabled Whether the first page footer is enabled
 * @property firstPageHeader The header of the first page
 * @property firstPageFooter The footer of the first page
 * @property metadata The metadata of the PDF
 * @property intro The intro of the PDF
 * @property language The language of the application
 * @property logLevel The log level of the application
 */
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
    @EncodeDefault
    @SerialName("log_level")
    val logLevel: Logger.LogLevel = Logger.LogLevel.INFO,
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

/**
 * Serializes the config to a JSON string. This is used to export the config and save it to a file.
 *
 * @param config The config to serialize
 * @return The JSON string representing the config
 */
fun exportConfig(config: Config): String = Json.encodeToString(Config.serializer(), config)

/**
 * Deserializes a JSON string to a config object. This is used to import a config from a file.
 *
 * @param string The JSON string to deserialize
 * @return The config object
 */
fun importConfig(string: String): Config = Json.decodeFromString(Config.serializer(), string)

/**
 * Returns the default config file. This is platform specific and should be
 * implemented for each platform.
 *
 * @return The default config file
 */
expect fun configFile(): File

/**
 * Saves the config to a file. This is platform specific and should be implemented for each platform.
 *
 * @param config The config to save
 * @param logger The logger to use
 * @param file The file to save the config to
 */
expect suspend fun saveConfigToFile(
    config: Config,
    logger: Logger,
    file: File = configFile(),
)

/**
 * Loads the config from a file. This is platform specific and should be implemented for each platform.
 *
 * @param logger The logger to use
 * @param file The file to load the config from
 * @return The loaded config
 */
expect suspend fun loadConfigFromFile(
    logger: Logger,
    file: File = configFile(),
): Config
