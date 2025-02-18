package com.lkpc.android.app.glory.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lkpc.android.app.glory.entity.Qt

@Dao
interface QtDao {
    @Query("SELECT * FROM qt_table")
    fun getAll(): LiveData<List<Qt>>

    @Query("SELECT * FROM qt_table WHERE id==(:id) LIMIT 1")
    fun loadById(id: Int): LiveData<Qt>

    @Query("SELECT * FROM qt_table WHERE content_id==(:contentId) LIMIT 1")
    fun loadByContentId(contentId: String): LiveData<Qt>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qt: Qt): Long

    @Delete
    suspend fun delete(qt: Qt)

    @Query("DELETE from qt_table where id in (:idList)")
    suspend fun deleteAll(idList: List<Int>)
}