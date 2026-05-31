package com.mercedesbenz.ecommerce.order_service.dto


data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> =
            ApiResponse(success = true, message = message, data = data)

        fun <T> error(error: String, message: String = "Failed"): ApiResponse<T> =
            ApiResponse(success = false, message = message, error = error)
    }
}