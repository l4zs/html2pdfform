package de.l4zs.html2pdfform.backend.extension

import com.lowagie.text.Font
import com.lowagie.text.pdf.BaseFont
import de.l4zs.html2pdfform.backend.config.Config

val Config.baseFont: BaseFont
    get() = BaseFont.createFont(font.translationKey, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
val Config.introBaseFont: BaseFont
    get() =
        BaseFont.createFont(
            intro.text?.font?.displayName ?: font.displayName,
            BaseFont.CP1252,
            BaseFont.NOT_EMBEDDED,
        )
val Config.defaultFont
    get() = Font(baseFont, fontSize)
val Config.introFont
    get() = Font(introBaseFont, intro.text?.fontSize ?: fontSize)
