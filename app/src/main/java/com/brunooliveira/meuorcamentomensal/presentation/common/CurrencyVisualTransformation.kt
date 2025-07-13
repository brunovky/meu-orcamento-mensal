package com.brunooliveira.meuorcamentomensal.presentation.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CurrencyVisualTransformation: VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val input = text.text

        if (input.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Companion.Identity)
        }

        val formatted = "R$ $input"

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = (offset + 3).coerceAtMost(formatted.length)
            override fun transformedToOriginal(offset: Int): Int = (offset - 3).coerceAtLeast(0)
        }

        return TransformedText(AnnotatedString(formatted), offsetTranslator)
    }

}