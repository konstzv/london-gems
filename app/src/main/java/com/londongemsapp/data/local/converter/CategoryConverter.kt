package com.londongemsapp.data.local.converter

import androidx.room.TypeConverter
import com.londongemsapp.domain.model.Category

class CategoryConverter {

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)
}
