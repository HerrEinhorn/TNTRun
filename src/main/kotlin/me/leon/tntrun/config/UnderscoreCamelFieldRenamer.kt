/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.config

import com.beust.klaxon.FieldRenamer

object UnderscoreCamelFieldRenamer : FieldRenamer {
    override fun toJson(fieldName: String) = FieldRenamer.camelToUnderscores(fieldName)
    override fun fromJson(fieldName: String) = FieldRenamer.underscoreToCamel(fieldName)
}