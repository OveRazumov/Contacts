package com.overazumov.contacts.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.overazumov.contacts.Contact
import com.overazumov.contacts.R

class AddContactDialog private constructor(context: Context) : ContactDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.button.text = context.getString(R.string.add)
    }

    override fun getContact(): Contact {
        binding.apply {
            return Contact(
                nameEditText.text.toString(),
                surnameEditText.text.toString(),
                Contact.PhoneNumber(phoneEditText.text.toString())
            )
        }
    }

    companion object {
        fun newInstance(context: Context, onButtonClick: (Contact) -> Unit): Dialog {
            return AddContactDialog(context).apply { setOnButtonClickListener { contact ->
                onButtonClick(contact)
            } }
        }
    }
}