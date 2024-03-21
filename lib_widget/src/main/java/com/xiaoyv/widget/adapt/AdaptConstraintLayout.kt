package com.xiaoyv.widget.adapt

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaoyv.widget.adapt.autoConvertDensity

/**
 * Class: [AdaptConstraintLayout]
 *
 * @author why
 * @since 3/21/24
 */
class AdaptConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        resources.autoConvertDensity()
        return super.generateLayoutParams(attrs)
    }
}