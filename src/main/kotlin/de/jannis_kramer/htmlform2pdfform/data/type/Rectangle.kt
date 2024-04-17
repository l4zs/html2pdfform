package de.jannis_kramer.htmlform2pdfform.data.type

data class Rectangle(
    val lowerLeftX: Int,
    val lowerLeftY: Int,
    val upperRightX: Int,
    val upperRightY: Int,
) {

    override fun toString(): String {
        return "[$lowerLeftX $lowerLeftY $upperRightX $upperRightY]"
    }
}
