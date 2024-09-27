package de.l4zs.html2pdfform.backend.data.field

/**
 * Contains all supported field types
 *
 * Note: Unsupported input field types:
 * - button
 * - color
 * - image
 * - range
 * - search
 * - submit
 * - week
 */
enum class FieldType {
    CHECKBOX,
    DATE,
    DATETIME_LOCAL,
    EMAIL,
    FILE,
    FIELD_WITH_LABEL, // pseudo field type
    FIELDSET,
    MONTH,
    NUMBER,
    PASSWORD,
    RADIO_GROUP, // pseudo field type
    RADIO,
    RESET,
    SELECT,
    SIGNATURE,
    SUBMIT,
    TELEPHONE,
    TEXT,
    TEXTAREA,
    TIME,
    URL,
    LABEL, // pseudo field type
}
