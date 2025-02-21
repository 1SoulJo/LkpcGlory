package com.lkpc.android.app.glory.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qt_table")
data class Qt (
    @PrimaryKey
    val contentId: String,

    val date: String?,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "refText1")
    val refText1: String?,
    @ColumnInfo(name = "refText2")
    val refText2: String?,
    @ColumnInfo(name = "refText3")
    val refText3: String?,
    @ColumnInfo(name = "refText4")
    val refText4: String?,
    @ColumnInfo(name = "refText5")
    val refText5: String?,

    @ColumnInfo(name = "refEdit1")
    val refEdit1: String?,
    @ColumnInfo(name = "refEdit2")
    val refEdit2: String?,
    @ColumnInfo(name = "refEdit3")
    val refEdit3: String?,
    @ColumnInfo(name = "refEdit4")
    val refEdit4: String?,
    @ColumnInfo(name = "refEdit5")
    val refEdit5: String?,

    @ColumnInfo(name = "appText1")
    val appText1: String?,
    @ColumnInfo(name = "appText2")
    val appText2: String?,
    @ColumnInfo(name = "appText3")
    val appText3: String?,
    @ColumnInfo(name = "appText4")
    val appText4: String?,
    @ColumnInfo(name = "appText5")
    val appText5: String?,

    @ColumnInfo(name = "appEdit1")
    val appEdit1: String?,
    @ColumnInfo(name = "appEdit2")
    val appEdit2: String?,
    @ColumnInfo(name = "appEdit3")
    val appEdit3: String?,
    @ColumnInfo(name = "appEdit4")
    val appEdit4: String?,
    @ColumnInfo(name = "appEdit5")
    val appEdit5: String?,
    )