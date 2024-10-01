package de.l4zs.html2pdfform.backend.util

/** Actions are JavaScript snippets that can be executed in a PDF form. */
object Actions {
    /** JavaScript snippets for Checkbox fields. */
    object Checkbox {
        /**
         * Toggles the readonly state of the given fields.
         *
         * @param toggles List of field names to toggle.
         * @return JavaScript snippet.
         */
        fun toggleFields(toggles: List<String>): String =
            """
            var toggles = [${toggles.joinToString(", ") { "\"$it\"" }}];
            for (var i = 0; i < toggles.length; i++) {
                var field = this.getField(toggles[i]);
                field.readonly = !field.readonly;
                if (field.readonly) {
                    this.resetForm([toggles[i]]);
                }
            }
            """.trimIndent()
    }

    /** JavaScript snippets for DateTime fields. */
    object DateTime {
        /**
         * Formats the date in the given format.
         *
         * @param format Date format.
         * @return JavaScript snippet.
         */
        fun formatDate(format: String): String =
            """
            AFDate_FormatEx("$format");
            """.trimIndent()

        /**
         * Formats the time in the given format.
         *
         * @param format Time format.
         * @return JavaScript snippet.
         */
        fun formatTime(format: String): String =
            """
            AFTime_FormatEx("$format");
            """.trimIndent()

        /**
         * Keystroke action for date fields.
         *
         * @param format Date format.
         * @return JavaScript snippet.
         */
        fun keystrokeDate(format: String): String =
            """
            AFDate_KeystrokeEx("$format");
            """.trimIndent()

        /**
         * Keystroke action for time fields.
         *
         * @param format Time format.
         * @return JavaScript snippet.
         */
        fun keystrokeTime(format: String): String =
            """
            AFTime_Keystroke("$format");
            """.trimIndent()
    }

    /** JavaScript snippets for Number fields. */
    object Number {
        /** Ensures that the field only allows to enter numbers. */
        val keystrokeNumber =
            """
            var numberRegex = new RegExp(/^-?[0-9]*$/);
            if (!event.willCommit && event.change && !global.isResettingForm) {
                event.rc = numberRegex.test(event.change);
            }
            """.trimIndent()

        /**
         * Validates that the value is greater than the given minimum.
         *
         * @param min Minimum value.
         * @param message Error message.
         * @return JavaScript snippet.
         */
        fun validateMin(
            min: Int,
            message: String,
        ): String =
            """
            if (event.value && !global.isResettingForm) {
                var isLess = event.value < $min;
                if (isLess) {
                    app.alert("$message");
                    event.rc = false;
                }
            }
            """.trimIndent()

        /**
         * Validates that the value is less than the given maximum.
         *
         * @param max Maximum value.
         * @param message Error message.
         * @return JavaScript snippet.
         */
        fun validateMax(
            max: Int,
            message: String,
        ): String =
            """
            if (event.value && !global.isResettingForm) {
                var isMore = event.value > $max;
                if (isMore) {
                    app.alert("$message");
                    event.rc = false;
                }
            }
            """.trimIndent()

        /**
         * Validates that the value is a multiple of the given step.
         *
         * @param step Step value.
         * @param base Base value.
         * @param message Error message.
         * @return JavaScript snippet.
         */
        fun validateStep(
            step: Int,
            base: Int,
            message: String,
        ): String =
            """
            if (event.value && !global.isResettingForm) {
                var isInvalid = Math.abs(event.value - $base) % $step > 0;
                if (isInvalid) {
                    app.alert("$message");
                    event.rc = false;
                }
            }
            """.trimIndent()
    }

    /** JavaScript snippets for Placeholder fields. */
    object Placeholder {
        /**
         * Formats the placeholder text.
         *
         * @param placeholder Placeholder text.
         * @return JavaScript snippet.
         */
        fun formatPlaceholder(placeholder: String) =
            """
            if (!event.value) {
                event.value = "$placeholder";
                event.target.display = display.noPrint;
                event.target.textColor = color.ltGray;
            } else {
                event.target.display = display.visible;
                event.target.textColor = color.black;
            }
            """.trimIndent()
    }

    /** JavaScript snippets for RadioGroup fields. */
    object RadioGroup {
        /**
         * Calls the toggleFields function with the selected value.
         *
         * @param group Group name.
         * @return JavaScript snippet.
         */
        fun toggleFields(group: String) =
            """
            toggleFields$group(event.value);
            """.trimIndent()

        /**
         * Toggles the readonly state of the given fields based on the selected
         * value.
         *
         * @param toggles Map of field names to toggle.
         * @param group Group name.
         * @param groupName Group field name.
         */
        fun toggleFields(
            toggles: Map<String, List<String>>,
            group: String,
            groupName: String,
        ) = """
            var toggles$group = {
                ${toggles.entries.joinToString(",\n") { "'${it.key}': [${it.value.joinToString(", ") { "\"$it\"" }}]" }}
            };
            
            var previousValue$group = this.getField("$groupName").valueAsString;
            
            function toggleFields$group(selectedValue) {
                if (toggles$group[selectedValue] && selectedValue !== previousValue$group) {
                    var fields = toggles$group[previousValue$group];
                    if (fields) {
                        for (var i = 0; i < fields.length; i++) {
                            var field = this.getField(fields[i]);
                            field.readonly = !field.readonly;
                            if (field.readonly) {
                                this.resetForm([fields[i]]);
                            }
                        }
                    }
                    previousValue$group = selectedValue;
                    fields = toggles$group[selectedValue];
                    if (fields) {
                        for (var i = 0; i < fields.length; i++) {
                            var field = this.getField(fields[i]);
                            field.readonly = !field.readonly;
                            if (field.readonly) {
                                this.resetForm([fields[i]]);
                            }
                        }
                    }
                }
            }
            """.trimIndent()
    }

    /** JavaScript snippets for Reset button. */
    object Reset {
        /**
         * Sets the global variable isResettingForm to true. This is used to
         * prevent validation errors when resetting the form.
         */
        val buttonDown =
            """
            global.isResettingForm = true;
            """.trimIndent()

        /**
         * Sets the global variable isResettingForm to false. This is used to
         * prevent validation errors when resetting the form.
         */
        val buttonUp =
            """
            global.isResettingForm = false;
            """.trimIndent()
    }

    /** JavaScript snippets for Submit button. */
    object Submit {
        /**
         * Submits the form data via email.
         *
         * @param to Email address to send the form to.
         * @param cc Email address to send a copy to.
         * @param subject Email subject.
         * @param body Email body.
         * @return JavaScript snippet.
         */
        fun submitMail(
            to: String,
            cc: String,
            subject: String,
            body: String,
            message: String,
        ) = """
            var count = 0;
            for (var i = 0; i < this.numFields; i++) {
            	var f = this.getField(this.getNthFieldName(i));
                if (f && f.type != "button" && f.required && !f.value) {
                    count++;
                }
            }
            
            if (count > 0) {
                app.alert("$message");
            } else {
                var cTo = "$to";
                var cCc = "$cc";
                var cSubject = "$subject";
                var cBody = "$body";
                this.mailDoc({
                    bUI: true,
                    cTo: cTo,
                    cCc: cCc,
                    cSubject: cSubject,
                    cMsg: cBody,
                });
            }
            """.trimIndent()
    }

    /** JavaScript snippets for Text fields. */
    object Text {
        /**
         * Validates that the value is at least the given minimum length.
         *
         * @param minLength Minimum length.
         * @param message Error message.
         * @return JavaScript snippet.
         */
        fun validateMinLength(
            minLength: Int,
            message: String,
        ): String =
            """
            if (event.value && !global.isResettingForm) {
                var isLess = event.value.length < $minLength;
                if (isLess) {
                    app.alert("$message");
                    event.rc = false;
                }
            }
            """.trimIndent()

        /**
         * Validates that the value matches the given pattern.
         *
         * @param pattern Regular expression pattern.
         * @param defaultMessage Default error message.
         * @param message Custom error message.
         * @return JavaScript snippet.
         */
        fun validatePattern(
            pattern: String,
            defaultMessage: String,
            message: String? = null,
        ): String =
            """
            var regex = new RegExp(/$pattern/);
            if (event.value && !global.isResettingForm) {
                var isValid = regex.test(event.value);
                if (!isValid) {
                    app.alert("${message ?: defaultMessage}");
                    event.rc = false;
                }
            }
            """.trimIndent()
    }
}
