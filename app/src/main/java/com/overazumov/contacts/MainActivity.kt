package com.overazumov.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overazumov.contacts.databinding.ActivityMainBinding
import com.overazumov.contacts.dialogs.AddContactDialog
import com.overazumov.contacts.dialogs.EditContactDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val addDialog = AddContactDialog.newInstance(this) { contact ->
            adapter.addContact(contact)
        } as AddContactDialog
        val editDialog = EditContactDialog.newInstance(this) { contact ->
            adapter.editContact(contact)
        } as EditContactDialog

        val itemTouchHelper = getItemTouchHelper()
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        adapter = ContactAdapter(this, itemTouchHelper) { contact -> editDialog.show(contact) }
        adapter.setContactList(Contact.Fabric.createContacts(fullNames))

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MainActivity.adapter
            addItemDecoration(RecyclerViewDecoration(context))
        }

        binding.apply {
            addButton.setOnClickListener {
                addDialog.show()
            }

            deleteModeButton.setOnClickListener {
                if (adapter.isDeleteModeActivated) deactivateDeleteMode()
                else activateDeleteMode()
            }

            deleteButton.setOnClickListener {
                if (adapter.deleteContacts()) deactivateDeleteMode()
            }

            cancelButton.setOnClickListener {
                deactivateDeleteMode()
            }
        }
    }

    private fun activateDeleteMode() {
        adapter.activateDeleteMode()
        binding.apply {
            deleteModeButton.setImageResource(R.drawable.ic_delete)
            addButton.visibility = View.INVISIBLE
            deleteModeButtons.visibility = View.VISIBLE
        }
    }

    private fun deactivateDeleteMode() {
        adapter.deactivateDeleteMode()
        binding.apply {
            deleteModeButton.setImageResource(R.drawable.ic_delete_outline)
            addButton.visibility = View.VISIBLE
            deleteModeButtons.visibility = View.INVISIBLE
        }
    }

    private fun  getItemTouchHelper() = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            (adapter as ItemTouchHelperAdapter).onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            (adapter as ItemTouchHelperAdapter).onItemRightSwipe(viewHolder.adapterPosition)
        }
    })
}