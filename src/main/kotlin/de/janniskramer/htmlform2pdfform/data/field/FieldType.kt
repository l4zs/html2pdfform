package de.janniskramer.htmlform2pdfform.data.field

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
 */
enum class FieldType {
    CHECKBOX, // TODO: test
    DATE, // TODO: test
    DATETIME_LOCAL, // TODO: test
    EMAIL, // TODO: test
    FILE, // TODO: test
    FIELD_WITH_LABEL, // pseudo field type
    HIDDEN, // TODO: test
    MONTH, // TODO: test
    NUMBER, // TODO: test
    PASSWORD, // TODO: test
    RADIO, // TODO: test
    RADIO_GROUP, // pseudo field type
    RESET, // TODO: test
    SELECT, // TODO: test
    SIGNATURE, // TODO: test
    TELEPHONE, // TODO: test
    TEXT, // TODO: test
    TEXTAREA, // TODO: test
    TIME, // TODO: test
    URL, // TODO: test
    WEEK, // TODO: IMPLEMENT
}
