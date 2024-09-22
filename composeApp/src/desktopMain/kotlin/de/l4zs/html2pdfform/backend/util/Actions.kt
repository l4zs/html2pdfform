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
    }

    object Email {
        val validateEmail =
            """
            if (event.value && !global.isResettingForm) {
                event.rc = event.value.match(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/);
                if (!event.rc) {
                    app.alert("Bitte geben Sie eine gültige E-Mail-Adresse ein.");
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

        fun validateMinMaxStep(
            min: Int?,
            max: Int?,
            step: Int?,
            base: Int?,
        ): String {
            val actions = mutableListOf<String>()
            min?.let { actions.add(validateMin(it)) }
            max?.let { actions.add(validateMax(it)) }
            step?.let { actions.add(validateStep(it, base!!)) }
            return """
                if (event.value && !global.isResettingForm) {
                    ${actions.joinToString("\n")}
                }
                """.trimIndent()
        }

        fun validateMin(min: Int): String =
            """
            var isLess = event.value < $min;
            if (isLess) {
                app.alert("Bitte geben Sie einen Wert größer oder gleich $min ein.");
                event.rc = false;
            }
            """.trimIndent()

        fun validateMax(max: Int): String =
            """
            var isMore = event.value > $max;
            if (isMore) {
                app.alert("Bitte geben Sie einen Wert kleiner oder gleich $max ein.");
                event.rc = false;
            }
            """.trimIndent()

        fun validateStep(
            step: Int,
            base: Int,
        ): String =
            """
            var isInvalid = (event.value - $base) % $step > 0;
            if (isInvalid) {
                app.alert("Bitte geben Sie einen Wert ein, der durch $step mit dem Basiswert $base teilbar ist.");
                event.rc = false;
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
        fun validateMinAndPattern(
            minLength: Int?,
            pattern: String?,
        ): String {
            val actions = mutableListOf<String>()
            minLength?.let { actions.add(validateMinLength(it)) }
            pattern?.let { actions.add(validatePattern(it)) }
            return """
                if (event.value && !global.isResettingForm) {
                    ${actions.joinToString("\n")}
                }
                """.trimIndent()
        }

        fun validateMinLength(minLength: Int): String =
            """
            var isLess = event.value.length < $minLength;
            if (isLess) {
                app.alert("Bitte geben Sie mindestens $minLength Zeichen ein.");
                event.rc = false;
            }
            """.trimIndent()

        fun validatePattern(pattern: String): String =
            """
            var regex = new RegExp("$pattern");
            var isValid = regex.test(event.value);
            if (!isValid) {
                app.alert("Bitte geben Sie einen Wert ein, der dem Muster $pattern entspricht.");
                event.rc = false;
            }
            """.trimIndent()
    }
}
