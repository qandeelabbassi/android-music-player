package com.qandeelabbassi.musicplayer.ui

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.qandeelabbassi.musicplayer.R
import kotlinx.android.synthetic.main.layout_search_bar.view.*


class SearchView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : MaterialCardView(context, attrs, defStyleAttr), View.OnFocusChangeListener, TextView.OnEditorActionListener {

    interface SearchListener {
        fun onQueryChanged(query: String)
        fun onActionClicked(query: String)
    }

    private var DEFAULT_SEARCH_DELAY = 300L
    private var shouldExpand: Boolean = false
    private var listener: SearchListener? = null
    private val searchHandler = Handler(Looper.getMainLooper())
    private val searchTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit
        override fun afterTextChanged(editable: Editable) {
            searchHandler.removeCallbacksAndMessages(null)
            searchHandler.postDelayed({
                listener?.onQueryChanged(editable.toString())
            }, DEFAULT_SEARCH_DELAY)
        }
    }

    init {
        // Add body layout
        val searchBody = LayoutInflater.from(context).inflate(
                R.layout.layout_search_bar,
                this,
                false
        ) as LinearLayout
        addView(searchBody)

        // get attrs
        val svAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SearchView,
                0, 0
        )
        setStyles(svAttrs)
        svAttrs.recycle()

        edt_search_query.addTextChangedListener(searchTextWatcher)
        edt_search_query.setOnEditorActionListener(this)
        edt_search_query.onFocusChangeListener = this
        img_close.setOnClickListener {
            edt_search_query.setText("")
            edt_search_query.clearFocus()
        }
    }

    private fun setStyles(attrs: TypedArray) {
        val resources = context.resources
        val svElevation = attrs.getDimension(R.styleable.SearchView_searchElevation, 0.0f)
        val strokeSelector = attrs.getColorStateList(R.styleable.SearchView_searchBorderSelector)
        val hintText = attrs.getString(R.styleable.SearchView_searchHintText)
        val inputType: Int = attrs.getInt(R.styleable.SearchView_android_inputType, -1)
        val imeAction: Int = attrs.getInt(R.styleable.SearchView_android_imeOptions, -1)
        val padding = resources.getDimension(R.dimen.sv_padding).toInt()

        // card
        setContentPadding(padding, padding, padding, padding)
        if (strokeSelector != null)
            setStrokeColor(strokeSelector)
        else
            setStrokeColor(ContextCompat.getColorStateList(context, R.color.card_stroke_selector_white))
        radius = resources.getDimension(R.dimen.sv_corner_radius)
        strokeWidth = resources.getDimension(R.dimen.sv_stroke_width).toInt()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            elevation = svElevation

        // edit text
        edt_search_query.hint = hintText ?: context.getString(R.string.sv_hint)

        if (inputType != -1)
            edt_search_query.inputType = inputType
        if (imeAction != -1)
            edt_search_query.imeOptions = imeAction
    }

    override fun onFocusChange(view: View?, focused: Boolean) {
        isSelected = focused
        img_close.visibility = if (focused) View.VISIBLE else View.GONE
        edt_search_query.isCursorVisible = focused
        if (!focused) {
            val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        edt_search_query.clearFocus()
        listener?.onActionClicked(edt_search_query.text.toString())
        return true
    }

    fun setSearchQueryListener(listener: SearchListener) {
        this.listener = listener
    }

    fun setText(value: String?) {
        edt_search_query.setText(value)
    }
}