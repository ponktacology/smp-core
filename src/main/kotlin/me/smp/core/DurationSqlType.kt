package me.smp.core

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

fun <E : Any> BaseTable<E>.duration(name: String): Column<Duration> = registerColumn(name, DurationSqlType)

object DurationSqlType : SqlType<Duration>(Types.BIGINT, typeName = "duration") {
    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: Duration) {
        ps.setLong(index, parameter.toMillis())
    }

    override fun doGetResult(rs: ResultSet, index: Int): Duration? = Duration(rs.getLong(index))
}