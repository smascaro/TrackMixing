package com.smascaro.trackmixing.base.data.repository

interface Repository<T> {
    suspend fun get(videoId: String): T
    suspend fun getAll(): List<T>
    suspend fun update(entity: T)
    suspend fun insert(entity: T): Long
    suspend fun delete(videoId: String)
}