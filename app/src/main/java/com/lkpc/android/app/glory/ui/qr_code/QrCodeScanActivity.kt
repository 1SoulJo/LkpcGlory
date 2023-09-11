package com.lkpc.android.app.glory.ui.qr_code

import android.Manifest
import android.R.id.message
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.ActivityQrCodeBinding
import com.lkpc.android.app.glory.entity.QrInfo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class QrCodeScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeBinding

    val requestCameraPermission = 201
    private val filename = "qrCodeScanList"

    private lateinit var barcodeDetector: BarcodeDetector
    lateinit var cameraSource: CameraSource

    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar)

        findViewById<TextView>(R.id.ab_title).text = getString(R.string.qr_code_scan_title)
        findViewById<ImageView>(R.id.ab_btn_back).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.ab_btn_back).setOnClickListener {
            finish()
        }

        binding.rvQrList.layoutManager = LinearLayoutManager(this)
        binding.rvQrList.adapter = QrCodeAdapter()

        binding.btnQrCodeClear.setOnClickListener {
            showListClearConfirmPopup()
        }

        binding.btnQrCodeSendMail.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_SUBJECT, "QR Code 데이터")

            var content = ""
            for (qrInfo in (binding.rvQrList.adapter as QrCodeAdapter).qrInfoList) {
                content += "${qrInfo.info},${qrInfo.date}\n\r"
            }

            email.putExtra(Intent.EXTRA_TEXT, content)

            //need this to prompts email client only
            email.type = "message/rfc822"

            startActivity(Intent.createChooser(email, "Choose an Email client :"))
        }

        readFile()
    }

    override fun onResume() {
        super.onResume()
        initializeDetectorsAndSources()
    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    private fun initializeDetectorsAndSources() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(300, 300).setAutoFocusEnabled(true).build()

        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@QrCodeScanActivity,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(binding.surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@QrCodeScanActivity,
                            arrayOf(Manifest.permission.CAMERA), requestCameraPermission)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int) { }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detections<Barcode>) {
                if (this@QrCodeScanActivity::dialog.isInitialized && dialog.isShowing) {
                    return
                }

                val barCodes = detections.detectedItems
                if (barCodes.size() > 0) {
                    if (barCodes.valueAt(0) != null) {
                        Log.d("QrCodeScanActivity", barCodes.valueAt(0).displayValue)
                        MainScope().launch {
                            showQrConfirmPopup(barCodes.valueAt(0).displayValue)
                        }
                    }
                }
            }
        })
    }

    private fun showQrConfirmPopup(info: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("QR Code 스캔 완료")

        val infoTextView = TextView(this)
        infoTextView.text = info
        builder.setView(infoTextView)

        builder.setPositiveButton("저장") { dialog, _ ->
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            (binding.rvQrList.adapter as QrCodeAdapter).addInfo(
                QrInfo(info, sdf.format(Date()))
            )
            binding.rvQrList.smoothScrollToPosition(0)
            writeFile()
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        dialog = builder.show()
    }

    private fun showListClearConfirmPopup() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("리스트를 삭제합니다")

        builder.setPositiveButton("확인") { dialog, _ ->
            (binding.rvQrList.adapter as QrCodeAdapter).clearList()
            binding.rvQrList.smoothScrollToPosition(0)

            writeFile()
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        dialog = builder.show()
    }

    private fun writeFile() {
        val gson = Gson()
        val fileContents = gson.toJson((binding.rvQrList.adapter as QrCodeAdapter).qrInfoList)
        Log.d("QrCode", fileContents)
        openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    private fun readFile() {
        try {
            openFileInput(filename).bufferedReader().useLines { lines ->
                val gson = Gson()
                for (line in lines) {
                    val myType = object : TypeToken<ArrayList<QrInfo>>() {}.type
                    val info = gson.fromJson<ArrayList<QrInfo>>(line, myType)
                    if (info.isNotEmpty()) {
                        (binding.rvQrList.adapter as QrCodeAdapter).qrInfoList = info
                        (binding.rvQrList.adapter as QrCodeAdapter).notifyDataSetChanged()
                    }
                }
                binding.rvQrList.smoothScrollToPosition(0)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}