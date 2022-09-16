package me.smp.core

import org.bukkit.Bukkit
import org.bukkit.Location
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

fun <E : Any> BaseTable<E>.location(name: String): Column<Location> = registerColumn(name, LocationSqlType)

object LocationSqlType : SqlType<Location>(Types.VARCHAR, typeName = "location") {
    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: Location) {
        val string =
            "${parameter.world.name}@${parameter.x}@${parameter.y}@${parameter.z}@${parameter.yaw}@${parameter.pitch}"
        ps.setString(index, string)
    }

    override fun doGetResult(rs: ResultSet, index: Int): Location? = rs.getString(index)?.let {
        val args = it.split("@")
        val world = args[0]
        val x = args[1].toDouble()
        val y = args[2].toDouble()
        val z = args[3].toDouble()
        val yaw = args[4].toFloat()
        val pitch = args[5].toFloat()
        return@let Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }
}