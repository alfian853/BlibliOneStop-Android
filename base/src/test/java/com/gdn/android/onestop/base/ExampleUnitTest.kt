package com.gdn.android.onestop.base

import com.gdn.android.onestop.base.util.toAliasName
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_string_ToAliasName(){
        assertEquals("alfian liao".toAliasName(),"AL")
        assertEquals("noah".toAliasName(),"NO")
        assertEquals("HE".toAliasName(),"HE")
    }
}
