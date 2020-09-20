package kr.shlim.roomcornermovie.data.dto.kmdb

data class Data(
    val CollName: String,
    val Count: Int,
    val Result: List<Result>,
    val TotalCount: Int
)