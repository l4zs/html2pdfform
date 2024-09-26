package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

fun email(
    element: Element,
    context: Context,
): FieldWithLabel<Text> {
    element.attr("pattern", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    val email = Text(element, context)
    return FieldWithLabel(email, email.label(), context)
}
