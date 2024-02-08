package com.overazumov.contacts.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import com.overazumov.contacts.Contact
import com.overazumov.contacts.R
import com.overazumov.contacts.databinding.BottomDialogBinding

abstract class ContactDialog(context: Context) : Dialog(context) {
    protected var binding = BottomDialogBinding.inflate(layoutInflater)

    abstract fun getContact(): Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDialog()
        initEditTexts()
    }

    override fun dismiss() {
        super.dismiss()
        clearEditTexts()
        clearErrors()
        clearFocus()
    }

    fun setOnButtonClickListener(onButtonClick: (Contact) -> Unit) {
        binding.button.setOnClickListener {
            if (isDataCorrect()) {
                onButtonClick(getContact())
                dismiss()
            }
        }
    }

    private fun initDialog() {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setGravity(Gravity.BOTTOM)
    }

    private fun initEditTexts() {
        binding.apply {
            nameEditText.addTextChangedListener { clearErrors() }
            surnameEditText.addTextChangedListener { clearErrors() }
            phoneEditText.addTextChangedListener { clearErrors() }

            phoneEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    phoneEditText.clearFocus()
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(phoneEditText.windowToken, 0)
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun isDataCorrect(): Boolean {
        var result = true
        binding.apply {
            if (nameEditText.text.toString().isEmpty()) {
                nameTextLayout.error = context.getString(R.string.enter_name)
                result = false
            }
            if (surnameEditText.text.toString().isEmpty()) {
                surnameTextLayout.error = context.getString(R.string.enter_surname)
                result = false
            }
            if (phoneEditText.text.toString().length != 10) {
                phoneTextLayout.error = context.getString(R.string.incorrect_phone_number)
                result = false
            }
            return result
        }
    }

    private fun clearEditTexts() {
        binding.apply {
            nameEditText.text = null
            surnameEditText.text = null
            phoneEditText.text = null
        }
    }

    private fun clearErrors() {
        binding.apply {
            nameTextLayout.error = null
            surnameTextLayout.error = null
            phoneTextLayout.error = null
        }
    }

    private fun clearFocus() {
        binding.apply {
            nameEditText.clearFocus()
            surnameEditText.clearFocus()
            phoneEditText.clearFocus()
        }
    }
}