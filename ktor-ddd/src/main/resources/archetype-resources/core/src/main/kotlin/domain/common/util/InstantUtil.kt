@file:JvmName("InstantUtil")

package ${package}.domain.common.util

import java.time.Instant
import java.time.Year
import java.time.ZoneOffset
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.IsoFields

/**
 * 获取当前年份
 * @param offset ZoneOffset 默认东八区
 * @return Int
 */
fun currentYear(offset: ZoneOffset = ZoneOffset.ofHours(8)): Int =
	Instant.now().atOffset(offset).get(ChronoField.YEAR)

/**
 * 一年的起止时间
 * @param offset ZoneOffset
 * @param year Int
 * @return Pair<(java.time.Instant), (java.time.Instant)>
 */
fun beginAndEndOfYear(offset: ZoneOffset = ZoneOffset.ofHours(8), year: Int = currentYear(offset)): Pair<Instant, Instant> =
	beginOfYear(offset, year) to beginOfYear(offset, year + 1)

internal inline fun <T> withBeginAndEndOfYear(
	offset: ZoneOffset = ZoneOffset.ofHours(8),
	year: Int = currentYear(offset),
	act: (start: Instant, end: Instant) -> T
) =
	act.invoke(
		beginOfYear(offset, year),
		beginOfYear(offset, year + 1)
	)

/**
 * 一年的起始时间
 * @param offset ZoneOffset
 * @param year Int
 * @return java.time.Instant
 */
fun beginOfYear(offset: ZoneOffset = ZoneOffset.ofHours(8), year: Int = currentYear(offset)): Instant =
	Year.of(year).atMonth(1)
		.atDay(1)
		.atStartOfDay(offset)
		.toInstant()

/**
 * 时间戳所在的周数 1-52|53
 * @param at Instant
 * @param offset ZoneOffset
 * @return Int
 */
fun weekOfInstant(at: Instant, offset: ZoneOffset = ZoneOffset.ofHours(8)): Int =
	at.atOffset(offset).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

/**
 * 时间戳所在的月份 1-12
 * @param at Instant
 * @param offset ZoneOffset
 * @return Int
 */
fun monthOfInstant(at: Instant, offset: ZoneOffset = ZoneOffset.ofHours(8)): Int =
	at.atOffset(offset).monthValue

internal val Instant.weekOfYear get() = weekOfInstant(this)
internal val Instant.monthOfYear get() = monthOfInstant(this)

internal fun Instant.roundToDay() = this.truncatedTo(ChronoUnit.DAYS)
