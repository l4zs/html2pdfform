package de.janniskramer.htmlform2pdfform.util

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

    object RadioGroup {
        fun toggleFields(group: String) =
            """
            toggleFields$group(event.target.value);
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
