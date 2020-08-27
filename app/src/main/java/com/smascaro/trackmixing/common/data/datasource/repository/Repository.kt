package com.smascaro.trackmixing.common.data.datasource.repository

interface Repository<T> {
    suspend fun get(videoId: String): T
    suspend fun getAll(): List<T>
    suspend fun update(entity: T)
    suspend fun insert(entity: T): Long
}