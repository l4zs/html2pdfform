package de.l4zs.html2pdfform.extension

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.CopyContent: ImageVector
    get() {
        if (_copyContent != null) {
            return _copyContent!!
        }
        _copyContent =
            materialIcon(name = "Filled.CopyContent") {
                materialPath {
                    moveTo(16f, 1f)
                    horizontalLineTo(4f)
                    arcTo(
                        2f,
                        2f,
                        0f,
                        isMoreThanHalf = false,
                        false,
                        2f,
                        3f,
                    )
                    verticalLineTo(17f)
                    horizontalLineTo(4f)
                    verticalLineTo(3f)
                    horizontalLineTo(16f)
                    verticalLineTo(1f)

                    moveTo(19f, 5f)
                    horizontalLineTo(8f)
                    arcTo(
                        2f,
                        2f,
                        0f,
                        isMoreThanHalf = false,
                        false,
                        6f,
                        7f,
                    )
                    verticalLineTo(21f)
                    arcTo(
                        2f,
                        2f,
                        0f,
                        isMoreThanHalf = false,
                        false,
                        8f,
                        23f,
                    )
                    horizontalLineTo(19f)
                    arcTo(
                        2f,
                        2f,
                        0f,
                        isMoreThanHalf = false,
                        false,
                        21f,
                        21f,
                    )
                    verticalLineTo(7f)
                    arcTo(
                        2f,
                        2f,
                        0f,
                        isMoreThanHalf = false,
                        false,
                        19f,
                        5f,
                    )

                    moveTo(19f, 21f)
                    horizontalLineTo(8f)
                    verticalLineTo(7f)
                    horizontalLineTo(19f)
                    verticalLineTo(21f)
                }
            }
        return _copyContent!!
    }

private var _copyContent: ImageVector? = null
