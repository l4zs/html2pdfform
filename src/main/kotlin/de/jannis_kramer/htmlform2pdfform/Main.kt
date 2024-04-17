package de.jannis_kramer.htmlform2pdfform

import de.jannis_kramer.htmlform2pdfform.data.AppearanceDictionary
import de.jannis_kramer.htmlform2pdfform.data.CatalogDictionary
import de.jannis_kramer.htmlform2pdfform.data.DictionaryObject
import de.jannis_kramer.htmlform2pdfform.data.PageDictionary
import de.jannis_kramer.htmlform2pdfform.data.PagesDictionary
import de.jannis_kramer.htmlform2pdfform.data.dictionaryObject
import de.jannis_kramer.htmlform2pdfform.data.form.AnnotationDictionary
import de.jannis_kramer.htmlform2pdfform.data.form.FieldDictionary
import de.jannis_kramer.htmlform2pdfform.data.form.InteractiveFormDictionary
import de.jannis_kramer.htmlform2pdfform.data.form.button.CheckBoxField
import de.jannis_kramer.htmlform2pdfform.data.type.Rectangle
import de.jannis_kramer.htmlform2pdfform.data.type.TextString

fun main() {
    pdf {
        dictionaryObject(::CatalogDictionary, Dictionary.Field.Type.CATALOG) catalog@{
            val checkedCheckbox = streamObject {
                this[Dictionary.Field.TYPE] = Dictionary.Field.Type.X_OBJECT
                this[Dictionary.Field.SUBTYPE] = "/Form"
                this[Dictionary.Field.BBOX] = Rectangle(0, 0, 20, 20)

                setContent(
                    """
                        q
                            0 0 1 rg
                            BT
                                /ZaDb 12 Tf
                                0 0 Td
                                (4) Tj
                            ET
                        Q
                    """.trimIndent()
                )
            }
            val uncheckedCheckbox = streamObject {
                this[Dictionary.Field.TYPE] = Dictionary.Field.Type.X_OBJECT
                this[Dictionary.Field.SUBTYPE] = "/Form"
                this[Dictionary.Field.BBOX] = Rectangle(0, 0, 20, 20)

                setContent(
                    """
                        q
                            0 0 1 rg
                            BT
                                /ZaDb 12 Tf
                                0 0 Td
                                (8) Tj
                            ET
                        Q
                    """.trimIndent()
                )
            }
            val fields = mutableListOf<PdfObject>()

            val acroForm = dictionaryObject(::InteractiveFormDictionary) acroForm@{
                fields.add(
                    dictionaryObject(::CheckBoxField) {
                        this[AnnotationDictionary.Field.RECT] = Rectangle(100, 100, 120, 120)
                        this[FieldDictionary.Field.NAME] = TextString("Urgent")
                        dictionary.setValue(CheckBoxField.State.CHECKED)
                        this[AnnotationDictionary.Field.APPEARANCE_DICTIONARY] = dictionary(::AppearanceDictionary) {
                            setNormalAppearance(dictionary {
                                this[CheckBoxField.State.CHECKED] = checkedCheckbox.getReference()
                                this[CheckBoxField.State.UNCHECKED] = uncheckedCheckbox.getReference()
                            })
                        }
                    }
                )
                this[InteractiveFormDictionary.Field.FIELDS] = fields.map { it.getReference() }
            }
            this[CatalogDictionary.Field.ACRO_FORM] = acroForm.getReference()

            this[Dictionary.Field.Type.PAGES] = dictionaryObject(::PagesDictionary) pages@{
                this[PagesDictionary.Field.MEDIA_BOX] = Rectangle(0, 0, 595, 842)
                val kids = listOf(
                    dictionaryObject(::PageDictionary) {
                        this[PageDictionary.Field.RESOURCES] = dictionary {
                            this[PdfName.FONT] = dictionary {
                                this["F1"] = dictionary {
                                    this[PdfName.TYPE] = "/Font"
                                    this[PdfName.SUBTYPE] = "/Type1"
                                    this[PdfName.BASEFONT] = "/Times-Roman"
                                }
                            }
                        }
                        this[PageDictionary.Field.ANNOTS] = fields.map { it.getReference() }
                        this[PageDictionary.Field.PARENT] = this@pages.getReference()
                    }
                )
                this[PagesDictionary.Field.KIDS] = kids.map { it.getReference() }
                this[PagesDictionary.Field.COUNT] = kids.size
            }.getReference()
        }
    }.build()

//    val pdf = pdf {
//        dictionaryObject(PdfName.CATALOG) catalog@{
//            this[PdfName.ACROFORM] = dictionary {
//                this[PdfName.FIELDS] = listOf("")
//            }
//
//            this[PdfName.PAGES] = dictionaryObject(PdfName.PAGES) pages@{
//                this[PdfName.MEDIA_BOX] = Rectangle(0, 0, 595, 842)
//                val kids = listOf(
//                    dictionaryObject(PdfName.PAGE) {
//                        this[PdfName.RESOURCES] = dictionary {
//                            this[PdfName.FONT] = dictionary {
//                                this["F1"] = dictionary(PdfName.FONT) {
//                                    this[PdfName.SUBTYPE] = "/Type1"
//                                    this[PdfName.BASEFONT] = "/Times-Roman"
//                                }
//                            }
//                        }
//                        this[PdfName.CONTENTS] = streamObject {
//                            setContent(
//                                """
//                                    BT
//                                    /F1 24 Tf
//                                    100 100 Td
//                                    (Hello World) Tj
//                                    ET
//                                    """.trimIndent()
//                            )
//                        }.getReference()
//                        this[PdfName.PARENT] = this@pages.getReference()
//                    }
//                )
//                this[PdfName.KIDS] = kids.map { it.getReference() }
//                this[PdfName.COUNT] = kids.size
//            }.getReference()
//        }
//    }
//    pdf.build()

//    val properties = ConverterProperties().apply {
//        isCreateAcroForm = true
//    }
//
//    val input = File("files/input.html")
//    val output = File("files/output.pdf")
//    output.parentFile.mkdirs()
//
//    HtmlConverter.convertToPdf(input, output, properties)
}
