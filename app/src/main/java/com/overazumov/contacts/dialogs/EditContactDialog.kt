package com.overazumov.contacts.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.overazumov.contacts.Contact
import com.overazumov.contacts.R

class EditContactDialog private constructor(context: Context) : ContactDialog(context) {
    private lateinit var currentContact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.button.text = context.getString(R.string.edit)
    }

    override fun getContact(): Contact {
        binding.apply {
            return Contact(
                nameEditText.text.toString(),
                surnameEditText.text.toString(),
                Contact.PhoneNumber(phoneEditText.text.toString()),
                currentContact.id
            )
        }
    }

    fun show(contact: Contact) {
        currentContact = contact
        fillEditTexts()
        setCursors()
        show()
    }

    private fun fillEditTexts() {
        binding.apply {
            nameEditText.setText(currentContact.name)
            surnameEditText.setText(currentContact.surname)
            phoneEditText.setText(currentContact.phoneNumber.number)
        }
    }

    private fun setCursors() {
        binding.apply {
            nameEditText.setSelection(currentContact.name.length)
            surnameEditText.setSelection(currentContact.surname.length)
            phoneEditText.setSelection(currentContact.phoneNumber.number.length)
        }
    }

    companion object {
        fun newInstance(context: Context, onButtonClick: (Contact) -> Unit): Dialog {
            return EditContactDialog(context).apply { setOnButtonClickListener { contact ->
                onButtonClick(contact)
            } }
        }
    }
}