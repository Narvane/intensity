package br.com.narvane.intensity.shared.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class ApiException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message)

data class ApiErrorResponse(
    val message: String
)

@RestControllerAdvice(basePackages = ["br.com.narvane.intensity"])
class IntensityApiExceptionHandler {
    @ExceptionHandler(ApiException::class)
    fun handleApiException(exception: ApiException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(exception.status).body(ApiErrorResponse(exception.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val message = exception.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Dados invalidos"
        return ResponseEntity.badRequest().body(ApiErrorResponse(message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.badRequest().body(ApiErrorResponse(exception.message ?: "Requisicao invalida"))
    }
}
