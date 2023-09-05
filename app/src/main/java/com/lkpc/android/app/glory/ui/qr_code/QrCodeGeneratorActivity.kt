package com.lkpc.android.app.glory.ui.qr_code

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.constants.SharedPreference
import com.lkpc.android.app.glory.databinding.ActivityQrCodeGeneratorBinding
import net.glxn.qrgen.android.QRCode
import java.io.ByteArrayOutputStream


class QrCodeGeneratorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeGeneratorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar)

        // get data from shared preference
        val sp = getSharedPreferences(SharedPreference.QR_PREFERENCE, Context.MODE_PRIVATE)
        val name = sp.getString(SharedPreference.QR_KEY_NAME, "")
        val phone = sp.getString(SharedPreference.QR_KEY_PHONE, "")
        val imgStr = sp.getString(SharedPreference.QR_KEY_IMAGE, "")
        binding.qrName.setText(name)
        binding.qrPhone.setText(phone)
        if (imgStr != null && imgStr.isNotEmpty()) {
            val imageAsBytes: ByteArray = Base64.decode(imgStr.encodeToByteArray(), Base64.DEFAULT)
            binding.imgQrCode.setImageBitmap(
                BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
        }

        findViewById<TextView>(R.id.ab_title).text = getString(R.string.qr_code_gen_title)
        findViewById<ImageView>(R.id.ab_btn_back).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.ab_btn_back).setOnClickListener {
            finish()
        }

        binding.btnQrCodeGenerate.setOnClickListener {
            if (binding.qrName.text.isEmpty() || binding.qrPhone.text.isEmpty()) {
                Toast.makeText(this, "이름 및 휴대폰 번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bm: Bitmap = QRCode.from("${binding.qrName.text} ${binding.qrPhone.text}")
                .withCharset("UTF-8").withSize(250, 250).bitmap()
            binding.imgQrCode.setImageBitmap(bm)

            // update sharedPreferences
            val outputStream = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val b: ByteArray = outputStream.toByteArray()
            sp.edit().putString(
                SharedPreference.QR_KEY_IMAGE,
                Base64.encodeToString(b, Base64.DEFAULT)
            ).apply()

            sp.edit().putString(SharedPreference.QR_KEY_NAME, "${binding.qrName.text}").apply()
            sp.edit().putString(SharedPreference.QR_KEY_PHONE, "${binding.qrPhone.text}").apply()
        }
    }
}