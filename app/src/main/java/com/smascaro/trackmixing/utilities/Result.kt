package com.smascaro.trackmixing.utilities

sealed class Result<S, F> {
    data class Failure<S, F>(val error: F) : Result<S, F>()
    data class Success<S, F>(val result: S) : Result<S, F>()

    suspend fun <T> mapOnFailure(mapperFunction: suspend (F) -> T): Result<S, T> {
        return when (this) {
            is Failure -> failure(mapperFunction(error))
            is Success -> success(result)
        }
    }

    suspend fun <T> mapOnSuccess(mapperFunction: suspend (S) -> T): Result<T, F> {
        return when (this) {
            is Failure -> failure(error)
            is Success -> success(mapperFunction(result))
        }
    }
}

fun <S, F> success(result: S) = Result.Success<S, F>(result)

fun <S, F> failure(error: F) = Result.Failure<S, F>(error)

/**
 * Returns the success result value or [default] if it is a [Result.Failure].
 */
fun <S, F> Result<S, F>.successValueOrDefault(default: S): S {
    return when (this) {
        is Result.Success -> result
        is Result.Failure -> default
    }
}