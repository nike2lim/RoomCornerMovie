package kr.shlim.roomcornermovie.data.dto.kmdb

import kr.shlim.roomcornermovie.data.dto.kmdb.Data

data class KmdbMovieListDTO(
    val Data: List<Data>,
    val KMAQuery: String,
    val Query: String,
    val TotalCount: Int
)