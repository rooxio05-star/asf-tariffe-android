package it.asf.tariffe

data class Solution(
    val origin: String,
    val destination: String,
    val tariff: Int?,
    val lines: List<String>,
    val via: List<String>,
    val changes: Int?,
    val isPrimary: Boolean,
    val source: String?
)
