package de.l4zs.html2pdfform.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.config.configFile
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.ui.util.*
import de.l4zs.html2pdfform.util.Logger
import org.jetbrains.compose.resources.stringResource

@Composable
fun HelpPage(
    navController: NavController,
    logger: Logger,
    config: Config,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.back))
            }
            Text(stringResource(Res.string.help_page_name), style = MaterialTheme.typography.h6)
            Spacer(Modifier.width(48.dp)) // For alignment
        }

        Spacer(modifier = Modifier.height(16.dp))

        WhatInputsAreSupported()

        Spacer(modifier = Modifier.height(16.dp))

        OtherSupported()

        Spacer(modifier = Modifier.height(16.dp))

        WhereConfigLocated()

        Spacer(modifier = Modifier.height(16.dp))

        WhyNotExpectedOutput()

        Spacer(modifier = Modifier.height(16.dp))

        Copyright()
    }
}

@Composable
private fun SupportedAttrs() {
    Text(stringResource(Res.string.help_page_section_inputs_supported_attrs))
}

@Composable
private fun NeedScriptAttr() {
    Text(stringResource(Res.string.help_page_need_script_attr))
}

@Composable
private fun NeedScriptValidate() {
    Text(stringResource(Res.string.help_page_need_script_validate))
}

@Composable
private fun CommonAttributes() {
    ExpandableSubSubSection("id") {
        Text(stringResource(Res.string.help_page_attr_id))
    }
    ExpandableSubSubSection("disabled") {
        Text(stringResource(Res.string.help_page_attr_disabled))
    }
    ExpandableSubSubSection("readonly") {
        Text(stringResource(Res.string.help_page_attr_readonly))
    }
    ExpandableSubSubSection("form") {
        Text(stringResource(Res.string.help_page_attr_form))
    }
}

@Composable
private fun Value() {
    ExpandableSubSubSection("value") {
        Text(stringResource(Res.string.help_page_attr_value))
    }
}

@Composable
private fun Required() {
    ExpandableSubSubSection("required") {
        Text(stringResource(Res.string.help_page_attr_required))
    }
}

@Composable
private fun Placeholder() {
    ExpandableSubSubSection("placeholder") {
        Text(stringResource(Res.string.help_page_attr_placeholder))
        NeedScriptAttr()
    }
}

@Composable
private fun Format() {
    ExpandableSubSubSection("format") {
        Text(stringResource(Res.string.help_page_attr_format))
        NeedScriptAttr()
    }
}

@Composable
private fun TextCommonAttributes() {
    Placeholder()
    Required()
    ExpandableSubSubSection("minlength") {
        Text(stringResource(Res.string.help_page_attr_minlength))
        NeedScriptAttr()
    }
    ExpandableSubSubSection("maxlength") {
        Text(stringResource(Res.string.help_page_attr_maxlength))
    }
    ExpandableSubSubSection("pattern") {
        Text(stringResource(Res.string.help_page_attr_pattern))
        NeedScriptAttr()
    }
    ExpandableSubSubSection("patternMessage") {
        Text(stringResource(Res.string.help_page_attr_patternMessage))
        CopyText(
            stringResource(Res.string.help_page_attr_patternMessage_syntax),
            stringResource(Res.string.help_page_attr_patternMessage_copy),
        )
        NeedScriptAttr()
    }
}

@Composable
private fun WhatInputsAreSupported() {
    // explain what html elements are supported
    ExpandableSection(stringResource(Res.string.help_page_section_inputs_title)) {
        Text(stringResource(Res.string.help_page_section_inputs_title))
        ExpandableSubSection("checkbox") {
            Text(stringResource(Res.string.help_page_section_inputs_checkbox_text))
            SupportedAttrs()
            CommonAttributes()
            Required()
            ExpandableSubSubSection("checked") {
                Text(stringResource(Res.string.help_page_attr_checkbox_checked))
            }
            ExpandableSubSubSection("toggles") {
                Text(stringResource(Res.string.help_page_attr_checkbox_toggles))
                CopyText(
                    stringResource(
                        Res.string.help_page_syntax,
                        stringResource(Res.string.help_page_attr_checkbox_toggles_syntax),
                    ),
                    stringResource(Res.string.help_page_attr_checkbox_toggles_copy),
                )
                NeedScriptAttr()
            }
        }
        ExpandableSubSection("date") {
            Text(
                stringResource(
                    Res.string.help_page_section_inputs_date_text,
                    stringResource(Res.string.converter_date_format),
                ),
            )
            NeedScriptValidate()
            SupportedAttrs()
            CommonAttributes()
            Value()
            Required()
            Placeholder()
        }
        ExpandableSubSection("datetime-local") {
            Text(
                stringResource(
                    Res.string.help_page_section_inputs_datetime_text,
                    stringResource(Res.string.converter_datetime_format),
                ),
            )
            NeedScriptValidate()
            SupportedAttrs()
            CommonAttributes()
            Value()
            Required()
            Placeholder()
        }
        ExpandableSubSection("email") {
            Text(stringResource(Res.string.help_page_section_inputs_email_text))
            NeedScriptValidate()
            OpenLink(
                stringResource(Res.string.help_page_test_validate),
                stringResource(Res.string.help_page_section_inputs_email_validate_url),
            )
            SupportedAttrs()
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("file") {
            Text(stringResource(Res.string.help_page_section_inputs_file_text))
            SupportedAttrs()
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("month") {
            Text(
                stringResource(
                    Res.string.help_page_section_inputs_month_text,
                    stringResource(Res.string.converter_month_format),
                ),
            )
            NeedScriptValidate()
            SupportedAttrs()
            CommonAttributes()
            Value()
            Required()
            Placeholder()
            Format()
        }
        ExpandableSubSection("number") {
            Text(stringResource(Res.string.help_page_section_inputs_number_text))
            NeedScriptValidate()
            SupportedAttrs()
            CommonAttributes()
            Value()
            Required()
            Placeholder()
            ExpandableSubSubSection("min") {
                Text(stringResource(Res.string.help_page_attr_number_min))
                NeedScriptAttr()
            }
            ExpandableSubSubSection("max") {
                Text(stringResource(Res.string.help_page_attr_number_max))
                NeedScriptAttr()
            }
            ExpandableSubSubSection("step") {
                Text(stringResource(Res.string.help_page_attr_number_step))
                NeedScriptAttr()
            }
        }
        ExpandableSubSection("password") {
            Text(stringResource(Res.string.help_page_section_inputs_password_text))
            SupportedAttrs()
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("radio") {
            Text(stringResource(Res.string.help_page_section_inputs_radio_text))
            SupportedAttrs()
            CommonAttributes()
            Required()
            ExpandableSubSubSection("name") {
                Text(stringResource(Res.string.help_page_attr_radio_name))
            }
            ExpandableSubSubSection("checked") {
                Text(stringResource(Res.string.help_page_attr_radio_checked))
            }
            ExpandableSubSubSection("toggles") {
                Text(stringResource(Res.string.help_page_attr_radio_toggles))
                CopyText(
                    stringResource(
                        Res.string.help_page_syntax,
                        stringResource(Res.string.help_page_attr_radio_toggles_syntax),
                    ),
                    stringResource(Res.string.help_page_attr_radio_toggles_copy),
                )
                NeedScriptAttr()
            }
        }
        ExpandableSubSection("reset") {
            Text(stringResource(Res.string.help_page_section_inputs_reset_text))
            SupportedAttrs()
            CommonAttributes()
            Value()
        }
        ExpandableSubSection("submit") {
            Text(stringResource(Res.string.help_page_section_inputs_submit_text))
            SupportedAttrs()
            CommonAttributes()
            Value()
            ExpandableSubSubSection("to") {
                Text(stringResource(Res.string.help_page_attr_submit_to))
                CopyText(
                    stringResource(
                        Res.string.help_page_syntax,
                        stringResource(Res.string.help_page_attr_submit_to_syntax),
                    ),
                    stringResource(Res.string.help_page_attr_submit_to_copy),
                )
                NeedScriptAttr()
            }
            ExpandableSubSubSection("cc") {
                Text(stringResource(Res.string.help_page_attr_submit_cc))
                CopyText(
                    stringResource(
                        Res.string.help_page_syntax,
                        stringResource(Res.string.help_page_attr_submit_cc_syntax),
                    ),
                    stringResource(Res.string.help_page_attr_submit_cc_copy),
                )
                NeedScriptAttr()
            }
            ExpandableSubSubSection("subject") {
                Text(stringResource(Res.string.help_page_attr_submit_subject))
                CopyText(
                    stringResource(
                        Res.string.help_page_syntax,
                        stringResource(Res.string.help_page_attr_submit_subject_syntax),
                    ),
                    stringResource(Res.string.help_page_attr_submit_subject_copy),
                )
                NeedScriptAttr()
            }
            ExpandableSubSubSection("body") {
                Text(stringResource(Res.string.help_page_attr_submit_body))
                CopyText(
                    stringResource(
                        Res.string.help_page_syntax,
                        stringResource(Res.string.help_page_attr_submit_body_syntax),
                    ),
                    stringResource(Res.string.help_page_attr_submit_body_copy),
                )
                NeedScriptAttr()
            }
        }
        ExpandableSubSection("tel") {
            Text(stringResource(Res.string.help_page_section_inputs_tel_text))
            SupportedAttrs()
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("text") {
            Text(stringResource(Res.string.help_page_section_inputs_text_text))
            SupportedAttrs()
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("time") {
            Text(
                stringResource(
                    Res.string.help_page_section_inputs_time_text,
                    stringResource(Res.string.converter_time_format),
                ),
            )
            NeedScriptValidate()
            SupportedAttrs()
            CommonAttributes()
            Value()
            Required()
            Placeholder()
            Format()
        }
        ExpandableSubSection("url") {
            Text(stringResource(Res.string.help_page_section_inputs_url_text))
            NeedScriptValidate()
            OpenLink(
                stringResource(Res.string.help_page_test_validate),
                stringResource(Res.string.help_page_section_inputs_url_validate_url),
            )
            SupportedAttrs()
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
    }
}

@Composable
fun OtherSupported() {
    ExpandableSection(stringResource(Res.string.help_page_section_other_title)) {
        ExpandableSubSection("label") {
            Text(stringResource(Res.string.help_page_section_other_label_text))
        }
        ExpandableSubSection("button") {
            Text(stringResource(Res.string.help_page_section_other_button_text))
        }
        ExpandableSubSection("select") {
            Text(stringResource(Res.string.help_page_section_other_select_text))
            SupportedAttrs()
            CommonAttributes()
            ExpandableSubSubSection("multiple") {
                Text(stringResource(Res.string.help_page_attr_select_multiple))
            }
            ExpandableSubSubSection("size") {
                Text(stringResource(Res.string.help_page_attr_select_size))
            }
            ExpandableSubSubSection("editable") {
                Text(stringResource(Res.string.help_page_attr_select_editable))
            }
            ExpandableSubSubSection("sorted") {
                Text(stringResource(Res.string.help_page_attr_select_sorted))
            }
        }
        ExpandableSubSection("textarea") {
            Text(stringResource(Res.string.help_page_section_other_textarea_text))
            SupportedAttrs()
            CommonAttributes()
            Value()
            TextCommonAttributes()
            ExpandableSubSubSection("rows") {
                Text(stringResource(Res.string.help_page_attr_textarea_rows))
            }
        }
        ExpandableSubSection("signature") {
            Text(stringResource(Res.string.help_page_section_other_signature_text))
            SupportedAttrs()
            CommonAttributes()
            Required()
        }
        ExpandableSubSection("fieldset") {
            Text(stringResource(Res.string.help_page_section_other_fieldset_text))
        }
    }
}

@Composable
private fun WhereConfigLocated() {
    ExpandableSection(stringResource(Res.string.help_page_section_config_title)) {
        val configFile = configFile()
        Text(stringResource(Res.string.help_page_section_config_text))
        Row {
            Text(stringResource(Res.string.help_page_section_config_location))
            Spacer(Modifier.width(4.dp))
            Open(configFile.absolutePath, configFile.parentFile)
        }
    }
}

@Composable
private fun WhyNotExpectedOutput() {
    ExpandableSection(stringResource(Res.string.help_page_section_output_title)) {
        Text(stringResource(Res.string.help_page_section_output_text))
    }
}

@Composable
private fun Copyright() {
    Row(
        Modifier.fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterVertically,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            Arrangement.Center,
            Alignment.CenterHorizontally,
        ) {
            Text(
                "${stringResource(Res.string.app_name)} v${stringResource(Res.string.app_version)}",
                color = MaterialTheme.colors.secondary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(Res.string.made_by, stringResource(Res.string.app_author)),
                color = MaterialTheme.colors.secondary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(Res.string.licensed_under, stringResource(Res.string.app_license)),
                color = MaterialTheme.colors.secondary,
            )
            Spacer(Modifier.height(4.dp))
            OpenLink(
                stringResource(Res.string.app_source_code),
                stringResource(Res.string.app_source_code),
                MaterialTheme.colors.secondary,
            )
        }
    }
}
