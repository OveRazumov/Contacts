package com.overazumov.contacts

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.overazumov.contacts.databinding.ContactItemBinding
import java.util.Collections

class ContactAdapter(
    private val context: Context,
    private val itemTouchHelper: ItemTouchHelper,
    private val itemClickListener: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), ItemTouchHelperAdapter {
    private var contactList = emptyList<Contact>()
    private val checkedContactsIds = mutableSetOf<Int>()

    var isDeleteModeActivated = false
        private set

    class ContactViewHolder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            ContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.binding.apply {
            fullName.text = "${contact.name} ${contact.surname}"
            phoneNumber.text = contact.phoneNumber.toString()

            item.setOnClickListener {
                if (isDeleteModeActivated) {
                    switchContactCheckState(contact.id, position)
                } else {
                    itemClickListener(contact)
                    itemTouchHelper.startSwipe(holder)
                }
            }

            item.setOnLongClickListener {
                itemTouchHelper.startDrag(holder)
                true
            }

            radioButton.apply {
                if (isDeleteModeActivated) {
                    visibility = View.VISIBLE
                    isChecked = checkedContactsIds.contains(contact.id)
                } else {
                    visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int = contactList.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        moveContact(fromPosition, toPosition)
    }

    override fun onItemRightSwipe(position: Int) {
        deleteContact(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun activateDeleteMode() {
        isDeleteModeActivated = true
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deactivateDeleteMode() {
        isDeleteModeActivated = false
        notifyDataSetChanged()
        checkedContactsIds.clear()
    }

    fun setContactList(newContactList: List<Contact>) {
        val diffUtil = ContactDiffUtil(contactList, newContactList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        contactList = newContactList
        diffResult.dispatchUpdatesTo(this)
    }

    fun addContact(contact: Contact) {
        val newContactList = contactList + contact
        setContactList(newContactList)
        notifyItemChanged(contactList.lastIndex - 1)
        showToast(R.string.contact_is_added)
    }

    fun deleteContacts(): Boolean {
        return if (checkedContactsIds.isEmpty()) {
            showToast(R.string.no_contacts_selected)
            false
        } else {
            val newContactList = getContactListCopy()
            newContactList.removeAll { checkedContactsIds.contains(it.id) }
            setContactList(newContactList)
            showToast(R.string.contacts_are_deleted)
            true
        }
    }

    fun editContact(contact: Contact) {
        val position = contactList.indexOfFirst { it.id == contact.id }
        if (contactList[position] == contact) {
            showToast(R.string.contact_has_not_been_edited)
        } else {
            val newContactList = getContactListCopy()
            newContactList[position] = contact
            setContactList(newContactList)
            showToast(R.string.contact_is_edited)
        }
    }

    private fun deleteContact(position: Int) {
        val newContactList = getContactListCopy()
        newContactList.removeAt(position)
        setContactList(newContactList)
        showToast(R.string.contact_is_deleted)
    }

    private fun moveContact(fromPosition: Int, toPosition: Int) {
        val newContactList = getContactListCopy()
        Collections.swap(newContactList, fromPosition, toPosition)
        setContactList(newContactList)
    }

    private fun switchContactCheckState(id: Int, position: Int) {
        if (checkedContactsIds.contains(id)) checkedContactsIds.remove(id)
        else checkedContactsIds.add(id)
        notifyItemChanged(position)
    }

    private fun getContactListCopy() = contactList.toMutableList()

    private fun showToast(textResId: Int) {
        Toast.makeText(context, context.getString(textResId), Toast.LENGTH_SHORT).show()
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemRightSwipe(position: Int)
}