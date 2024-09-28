package de.l4zs.html2pdfform.backend.util

object Actions {
    object Checkbox {
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

    object DateTime {
        fun formatDate(format: String): String =
            """
            AFDate_FormatEx("$format");
            """.trimIndent()

        fun formatTime(format: String): String =
            """
            AFTime_FormatEx("$format");
            """.trimIndent()

        fun keystrokeDate(format: String): String =
            """
            AFDate_KeystrokeEx("$format");
            """.trimIndent()

        fun keystrokeTime(format: String): String =
            """
            AFTime_Keystroke("$format");
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

    object Placeholder {
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

    object RadioGroup {
        fun toggleFields(group: String) =
            """
            toggleFields$group(event.value);
            """.trimIndent()

        fun toggleFields(
            toggles: Map<String, List<String>>,
            group: String,
            groupName: String,
        ) = """
            var toggles$group = {
                ${toggles.entries.joinToString(",\n") { "${it.key}: [${it.value.joinToString(", ") { "\"$it\"" }}]" }}
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

    object Submit {
        fun submitMail(
            to: String,
            cc: String,
            subject: String,
            body: String,
        ) = """
            var cTo = "$to";
            var cCc = "$cc";
            var cSubject = "$subject";
            var cBody = "$body";
            this.mailDoc({
                bUI: false,
                cTo: cTo,
                cCc: cCc,
                cSubject: cSubject,
                cMsg: cBody,
            });
            """.trimIndent()
    }

    object Text {
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

        fun validatePattern(
            pattern: String,
            defaultMessage: String,
            message: String? = null,
        ): String =
            """
            if (event.value && !global.isResettingForm) {
                var regex = new RegExp("$pattern");
                var isValid = regex.test(event.value);
                if (!isValid) {
                    app.alert("${message ?: defaultMessage}");
                    event.rc = false;
                }
            }
            """.trimIndent()
    }
}
