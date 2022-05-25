package com.josty.qualifying.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import com.google.android.material.R as materialR
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.josty.qualifying.R

private const val DRAWABLE_END = 2

class SearchEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = materialR.attr.editTextStyle,
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private val clearIcon = ContextCompat.getDrawable(context, R.drawable.ic_close)
    private var isEmpty = text.isNullOrEmpty()

    var query: String
        get() = text?.trim()?.toString().orEmpty()
        set(value) {
            if (value != text?.toString()) {
                setText(value)
                setSelection(value.length)
            }
        }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (hasFocus()) {
                clearFocus()
                // return true
            }
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int,
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        val empty = text.isNullOrEmpty()
        if (isEmpty != empty) {
            isEmpty = empty
            updateActionIcon()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        updateActionIcon()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawable = compoundDrawablesRelative[DRAWABLE_END] ?: return super.onTouchEvent(event)
            val isOnDrawable = drawable.isVisible && if (layoutDirection == LAYOUT_DIRECTION_RTL) {
                event.x.toInt() in paddingLeft..(drawable.bounds.width() + paddingLeft)
            } else {
                event.x.toInt() in (width - drawable.bounds.width() - paddingRight)..(width - paddingRight)
            }
            if (isOnDrawable) {
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED)
                playSoundEffect(SoundEffectConstants.CLICK)
                onActionIconClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun clearFocus() {
        super.clearFocus()
        text?.clear()
    }

    private fun onActionIconClick() {
        when {
            !text.isNullOrEmpty() -> text?.clear()
        }
    }

    private fun updateActionIcon() {
        val icon = when {
            !text.isNullOrEmpty() -> clearIcon
            else -> null
        }
        if (icon !== drawableEnd) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, icon, null)
        }
    }
}

var TextView.drawableStart: Drawable?
    inline get() = compoundDrawablesRelative[0]
    set(value) {
        val dr = compoundDrawablesRelative
        setCompoundDrawablesRelativeWithIntrinsicBounds(value, dr[1], dr[2], dr[3])
    }

var TextView.drawableEnd: Drawable?
    inline get() = compoundDrawablesRelative[2]
    set(value) {
        val dr = compoundDrawablesRelative
        setCompoundDrawablesRelativeWithIntrinsicBounds(dr[0], dr[1], value, dr[3])
    }