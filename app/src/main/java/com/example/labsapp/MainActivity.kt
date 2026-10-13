package com.example.labsapp

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.labsapp.network.NotesServer
import com.example.labsapp.web.CompareBridge
import com.example.labsapp.web.DivisionBridge
import com.example.labsapp.web.LogicBridge
import com.example.labsapp.web.NetworkBridge
import com.example.labsapp.web.PhysicsBridge
import com.example.labsapp.web.RoofingBridge
import android.speech.RecognizerIntent
import android.content.ActivityNotFoundException
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
//Single Responsibility
//Dependency Inversion
//Liskov Substitution
//Open/Closed
class MainActivity : AppCompatActivity() {

    private var isLessonSelected = false
    private var currentLabNumber = 1 // Зберігаємо номер поточної лаби
    private var currentFileId = 1

    private lateinit var layoutLabs: LinearLayout
    private lateinit var layoutLesson: LinearLayout
    private lateinit var logoImage: ImageView
    private lateinit var webView: WebView
    private lateinit var tvVariantValue1: TextView
    private lateinit var tvVariantNumber1: TextView

    private lateinit var navHeader: LinearLayout
    private lateinit var headerSeparator: View
    private lateinit var tvLabHeader: TextView
    private lateinit var btnBack: ImageButton

    private val labButtons = mutableListOf<LinearLayout>()

    private val REQUEST_CODE_SPEECH_INPUT = 1002 // Unique request code for speech input

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Вмикаємо інтерфейс на весь екран (прибирає чорні смуги)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Додаємо відступи, щоб інтерфейс не залазив під статус-бар (годинник, батарея)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupWebView()
        setupListeners()
        setupLabs()

        // Запуск сервера для лаб
        Thread(NotesServer(5000)).start()
    }

    private fun initViews() {
        webView = findViewById(R.id.webView)
        layoutLesson = findViewById(R.id.layoutLesson)
        layoutLabs = findViewById(R.id.layoutLabs)
        logoImage = findViewById(R.id.logoImage)
        tvVariantValue1 = findViewById(R.id.tvVariantValue1)
        tvVariantNumber1 = findViewById(R.id.tvVariantNumber1)
        navHeader = findViewById(R.id.navHeader)
        headerSeparator = findViewById(R.id.headerSeparator)
        tvLabHeader = findViewById(R.id.tvLabHeader)
        btnBack = findViewById(R.id.btnBack)

        webView.visibility = View.GONE
        layoutLabs.visibility = View.GONE
        navHeader.visibility = View.GONE
        headerSeparator.visibility = View.GONE
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.clearCache(true)
        webView.clearHistory()

        // Підключаємо мости для JS
        webView.addJavascriptInterface(DivisionBridge(), "AndroidDivision")
        webView.addJavascriptInterface(PhysicsBridge(), "AndroidPhysics")
        webView.addJavascriptInterface(RoofingBridge(), "AndroidRoofing")
        webView.addJavascriptInterface(NetworkBridge(), "AndroidNetwork")
        webView.addJavascriptInterface(LogicBridge(this), "AndroidLogic") // Pass context to LogicBridge
        webView.addJavascriptInterface(CompareBridge(this), "AndroidCompare")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val urlStr = request?.url.toString()
                if (urlStr.startsWith("report://open")) {
                    val uri = Uri.parse(urlStr)
                    val reportLink = uri.getQueryParameter("link")
                    if (reportLink != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reportLink))
                        startActivity(intent)
                    }
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // Викликаємо JS функцію після завантаження сторінки з ПРАВИЛЬНИМ номером
                view?.evaluateJavascript("showLab($currentLabNumber)", null)
            }
        }
    }

    private fun setupListeners() {
        // Кнопка назад у верхньому меню (стрілочка)
        btnBack.setOnClickListener {
            handleBackNavigation()
        }

        // Обробка системного жесту "Назад" (або фізичної кнопки)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.visibility == View.VISIBLE) {
                    handleBackNavigation()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        layoutLesson.setOnClickListener {
            isLessonSelected = !isLessonSelected

            if (isLessonSelected) {
                layoutLesson.background = ContextCompat.getDrawable(this, R.drawable.shape_rounded_containers_green)
                tvVariantNumber1.background = ContextCompat.getDrawable(this, R.drawable.shape_rounded_green)
                tvVariantNumber1.setTextColor(Color.WHITE)
                tvVariantValue1.setTextColor(ContextCompat.getColor(this, R.color.correctAnswerColor))
                layoutLabs.visibility = View.VISIBLE
            } else {
                layoutLesson.background = ContextCompat.getDrawable(this, R.drawable.shape_rounded_containers)
                tvVariantNumber1.background = ContextCompat.getDrawable(this, R.drawable.shape_rounded_variants)
                tvVariantNumber1.setTextColor(Color.parseColor("#888992"))
                tvVariantValue1.setTextColor(Color.BLACK)
                layoutLabs.visibility = View.GONE
            }

            webView.visibility = View.GONE
            navHeader.visibility = View.GONE
            headerSeparator.visibility = View.GONE
            logoImage.visibility = View.VISIBLE
        }

    }

    private fun setupLabs() {
        setupLabClick(findViewById(R.id.lab1), 1)
        setupLabClick(findViewById(R.id.lab2), 2)
        setupLabClick(findViewById(R.id.lab3), 3)
        setupLabClick(findViewById(R.id.lab4), 4)
        setupLabClick(findViewById(R.id.lab5), 5)
        setupLabClick(findViewById(R.id.lab6), 6)
        setupLabClick(findViewById(R.id.lab7), 7)
    }

    private fun setupLabClick(labView: LinearLayout, labNumber: Int) {
        labButtons.add(labView)

        labView.setOnClickListener {
            resetLabsColors()

            currentLabNumber = labNumber // Оновлюємо поточний номер лаби

            labView.background = ContextCompat.getDrawable(this, R.drawable.shape_rounded_containers_green)
            (labView.getChildAt(0) as TextView).apply {
                background = ContextCompat.getDrawable(this@MainActivity, R.drawable.shape_rounded_green)
                setTextColor(Color.WHITE)
            }
            (labView.getChildAt(1) as TextView).setTextColor(ContextCompat.getColor(this, R.color.correctAnswerColor))

            // Перемикаємо екран
            logoImage.visibility = View.GONE
            layoutLesson.visibility = View.GONE
            layoutLabs.visibility = View.GONE

            navHeader.visibility = View.VISIBLE
            headerSeparator.visibility = View.VISIBLE
            tvLabHeader.text = "Лабораторна робота №$labNumber"

            webView.visibility = View.VISIBLE
            webView.loadUrl("file:///android_asset/html/labs.html")
        }
    }

    fun startSpeechToTextFromJs() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uk-UA")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажіть щось...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(this, "Розпізнавання мови не підтримується на цьому пристрої", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleBackNavigation() {
        // Повертаємо видимість головного меню
        webView.visibility = View.GONE
        navHeader.visibility = View.GONE
        headerSeparator.visibility = View.GONE

        logoImage.visibility = View.VISIBLE
        layoutLesson.visibility = View.VISIBLE
        layoutLabs.visibility = View.VISIBLE
    }

    private fun resetLabsColors() {
        for (labLayout in labButtons) {
            labLayout.background = ContextCompat.getDrawable(this, R.drawable.shape_rounded_containers)
            val numberText = labLayout.getChildAt(0) as? TextView
            numberText?.background = ContextCompat.getDrawable(this, R.drawable.shape_rounded_variants)
            numberText?.setTextColor(Color.parseColor("#888992"))
            val nameText = labLayout.getChildAt(1) as? TextView
            nameText?.setTextColor(Color.BLACK)
        }
    }

    fun updateCurrentFileId(id: Int) {
        currentFileId = id
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uk-UA") // Ukrainian language
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажіть щось...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(this, "Розпізнавання мови не підтримується на цьому пристрої", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                val spokenText = result[0]
                // Pass spokenText to JavaScript in WebView
                webView.evaluateJavascript("handleVoiceInputLab5('$spokenText')", null)
            }
        } else if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                CompareBridge.setFileUri(currentFileId, uri)

                var name = "Файл $currentFileId"
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            name = it.getString(nameIndex)
                        }
                    }
                }

                if (name == "Файл $currentFileId") {
                    name = uri.lastPathSegment ?: name
                }

                webView.evaluateJavascript("setFileName($currentFileId, '$name')", null)
            }
        }
    }
}
