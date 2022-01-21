package com.example.a10contact

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_READ_CONTACTS = 1
    private var READ_CONTACTS_GRANTED = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // получаем разрешения
        val hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        // если устройство до API 23, устанавливаем разрешение
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true
        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            loadContacts()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true
            }
        }
        if (READ_CONTACTS_GRANTED) {
            loadContacts()
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadContacts() {
        var contact: String? = null
        var phone: String? = null
        var id: Int? = null
        val contentResolver = contentResolver
        var cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (contact == null) {
                    contact = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID))
                    break
                }
            }
            cursor.close()
        }

        cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                    val p_id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                    if (id == p_id) {
                        phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        Log.d("ID", id.toString())
                        break
                    }
            }
            cursor.close()
        }

        val name_text : TextView = findViewById(R.id.name)
        val phone_text : TextView = findViewById(R.id.phone)

        name_text.text = contact
        phone_text.text = phone
    }



}