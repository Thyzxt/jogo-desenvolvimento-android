package com.example.myapplication.utils

import java.util.Stack

// Deixamos as funções "public" (padrão do Kotlin)
// para que qualquer arquivo no app possa usá-las.

fun precedence(op: Char): Int {
    return when (op) {
        '+', '-' -> 1
        'x', '÷' -> 2
        else -> -1
    }
}

fun applyOp(a: Double, b: Double, op: Char): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        'x' -> a * b
        '÷' -> if (b == 0.0) Double.NaN else a / b // Evita crash
        else -> 0.0
    }
}

fun evaluateExpression(expression: String): String {
    try {
        val values = Stack<Double>()
        val ops = Stack<Char>()
        var i = 0
        while (i < expression.length) {
            if (expression[i].isDigit()) {
                val buffer = StringBuilder()
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    buffer.append(expression[i++])
                }
                values.push(buffer.toString().toDouble())
                i--
            } else if (expression[i] in "+-x÷") {
                while (!ops.empty() && precedence(ops.peek()) >= precedence(expression[i])) {
                    val val2 = values.pop()
                    val val1 = values.pop()
                    val op = ops.pop()
                    values.push(applyOp(val1, val2, op))
                }
                ops.push(expression[i])
            }
            i++
        }

        while (!ops.empty()) {
            val val2 = values.pop()
            val val1 = values.pop()
            val op = ops.pop()
            values.push(applyOp(val1, val2, op))
        }

        val result = values.pop()
        return if (result.isNaN()) {
            "Erro" // Divisão por zero
        } else if (result == result.toLong().toDouble()) {
            result.toLong().toString() // Retorna "10" em vez de "10.0"
        } else {
            String.format(java.util.Locale.US, "%.2f", result)
        }
    } catch (e: Exception) {
        return "Erro" // Expressão mal formada
    }
}