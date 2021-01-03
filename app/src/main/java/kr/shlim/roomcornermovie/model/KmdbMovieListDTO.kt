package kr.shlim.roomcornermovie.model

data class KmdbMovieListDTO(
    val Data: List<Data>,
    val KMAQuery: String,
    val Query: String,
    val TotalCount: Int
)