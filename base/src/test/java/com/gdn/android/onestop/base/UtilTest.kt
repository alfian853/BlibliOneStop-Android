package com.gdn.android.onestop.base

import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import org.junit.Test

import org.junit.Assert.*

class UtilTest {


    @Test
    fun test_string_ToAliasName(){
        assertEquals("alfian liao".toAliasName(),"AL")
        assertEquals("noah".toAliasName(),"NO")
        assertEquals("HE".toAliasName(),"HE")
    }

    @Test
    fun shrinkText(){
        val case_18_char = "123456789012345678"
        println(Util.shrinkText(case_18_char))

        val case_has_newline = "123456\n7890"
        println(Util.shrinkText(case_has_newline))

        val case_more_18_char = "12345678901234567891234567890"
        println(Util.shrinkText(case_more_18_char))

        val case_empty = ""
        println(Util.shrinkText(case_empty))

        val case_more_18_char_newline = "12345678901234567\n891234567890"
        println(Util.shrinkText(case_more_18_char_newline))

    }
}
