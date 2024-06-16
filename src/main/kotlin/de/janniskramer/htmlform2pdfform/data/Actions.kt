package de.janniskramer.htmlform2pdfform.data

object Actions {
    object DateTime {
        fun formatDateTime(format: String): String =
            """
            AFDate_FormatEx("$format");
            """.trimIndent()

        fun keystrokeDateTime(format: String): String =
            """
            AFDate_KeystrokeEx("$format");
            """.trimIndent()
    }

    object Email {
        val validateEmail =
            """
            if (event.value && !global.isResettingForm) {
                event.rc = event.value.match(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/);
                if (!event.rc) {
                    app.alert("Please enter a valid email address.");
                }
            }
            """.trimIndent()
    }

    object Number {
        val keystrokeNumber =
            """
            var numberRegex = new RegExp("^-?[0-9]*$");
            if (!event.willCommit && event.change && !global.isResettingForm) {
                event.rc = numberRegex.test(event.change);
            }
            """.trimIndent()

        fun validateMin(min: Int): String =
            """
            if (event.value && !global.isResettingForm) {
                event.rc = event.value >= $min;
                if (!event.rc) {
                    app.alert("Please enter a value greater than or equal to $min.");
                }
            }
            """.trimIndent()

        fun validateMax(max: Int): String =
            """
            if (event.value && !global.isResettingForm) {
                event.rc = event.value <= $max;
                if (!event.rc) {
                    app.alert("Please enter a value less than or equal to $max.");
                }
            }
            """.trimIndent()

        fun validateStep(
            step: Int,
            base: Int,
        ): String =
            """
            if (event.value && !global.isResettingForm) {
                event.rc = (event.value - $base) % $step === 0;
                if (!event.rc) {
                    app.alert("Please enter a value that is a multiple of $step with a base of $base.");
                }
            }
            """.trimIndent()
    }

    object Reset {
        val buttonDown =
            """
            global.isResettingForm = true;
            """.trimIndent()
        val buttonUp =
            """
            global.isResettingForm = false;
            """.trimIndent()
    }

    object Text {
        fun validateMinLength(minLength: Int): String =
            """
            if (event.value && !global.isResettingForm) {
                event.rc = event.value.length >= $minLength;
                if (!event.rc) {
                    app.alert("Please enter at least $minLength characters.");
                }
            }
            """.trimIndent()

        fun validatePattern(pattern: String): String =
            """
            var regex = new RegExp("$pattern");
            if (event.value && !global.isResettingForm) {
                event.rc = regex.test(event.value);
                if (!event.rc) {
                    app.alert("Please enter a valid value that matches the pattern $pattern.");
                }
            }
            """.trimIndent()
    }
}
