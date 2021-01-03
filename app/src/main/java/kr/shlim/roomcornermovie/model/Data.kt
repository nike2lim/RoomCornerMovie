package kr.shlim.roomcornermovie.model

data class Data(
    val CollName: String,
    val Count: Int,
    val Result: List<Result>,
    val TotalCount: Int
)