package gg.traphouse.core.rank

object RankValidator {
    fun isMorePowerful(minRankRequired: Rank, rank: Rank) = rank.isSuperior(minRankRequired)
}
