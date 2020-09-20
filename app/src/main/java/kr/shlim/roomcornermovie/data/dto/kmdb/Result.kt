package kr.shlim.roomcornermovie.data.dto.kmdb

import kr.shlim.roomcornermovie.data.dto.kmdb.Actors
import kr.shlim.roomcornermovie.data.dto.kmdb.Directors
import kr.shlim.roomcornermovie.data.dto.kmdb.Plots

data class Result(
    val DOCID: String,
    val actors: Actors,
    val company: String,
    val directors: Directors,
    val genre: String,
    val kmdbUrl: String,
    val movieId: String,
    val movieSeq: String,
    val nation: String,
    val plots: Plots,
    val prodYear: String,
    val rating: String,
    val runtime: String,
    val title: String,
    val titleEng: String,
    val titleEtc: String,
    val titleOrg: String
)