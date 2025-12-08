package com.main.gtaradio.ui.theme

import androidx.compose.foundation.shape.GenericShape


val SkewedRectangleShapeRight = GenericShape { size, _ ->
    moveTo(0f, 0f)                           // верхний левый угол
    lineTo(size.width, 0f)                   // верхний правый угол

    // Скошенная линия:
    lineTo(size.width * 0.75f, size.height)     // 3/4 нижнего края

    lineTo(0f, size.height)                  // нижний левый
    close()
}

val SkewedRectangleShapeLeft = GenericShape { size, _ ->
    moveTo(size.width * 0.25f, 0f)     // 3/4 верхнего края (конец скоса)
    lineTo(size.width, 0f)             // верхний правый
    lineTo(size.width, size.height)    // нижний правый
    lineTo(0f, size.height)            // нижний левый (старт скоса)
    close()
}