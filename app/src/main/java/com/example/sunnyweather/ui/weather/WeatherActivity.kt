package com.example.sunnyweather.ui.weather

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navBtn = findViewById<Button>(R.id.navBtn)
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })

        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.locationLnt.isEmpty()) {
            viewModel.locationLnt = intent.getStringExtra("location_lnt") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        val swiperefresh = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather == null) {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            } else {
                showWeatherInfo(weather)
            }
            swiperefresh.isRefreshing = false
        }

        swiperefresh.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                com.google.android.material.R.color.design_default_color_primary
            )
        )
        swiperefresh.setOnRefreshListener {
            refreshWeather()
        }

        refreshWeather()
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLnt, viewModel.locationLat)
        val swiperefresh = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        swiperefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        val placeName = findViewById<TextView>(R.id.placeName)
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toDouble().toInt()} ℃"

        val currentTemp = findViewById<TextView>(R.id.currentTemp)
        currentTemp.text = currentTempText

        val currentSky = findViewById<TextView>(R.id.currentSky)
        currentSky.text = getSky(realtime.skycon).info

        val currentAQI = findViewById<TextView>(R.id.currentAQI)
        val currentPM25Text = "空气指数 ${realtime.air_quality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text

        val nowLayout = findViewById<RelativeLayout>(R.id.nowLayout)
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        // 填充forecast.xml布局中的数据
        val forecastLayout = findViewById<LinearLayout>(R.id.forecastLayout)
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                forecastLayout, false
            )

            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)

            dateInfo.text = skycon.date.substring(0, skycon.date.indexOf('T'))

            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info

            val tempText =
                "${temperature.min.toDouble().toInt()} ~ ${temperature.max.toDouble().toInt()} ℃"
            temperatureInfo.text = tempText

            forecastLayout.addView(view)
        }

        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex

        val coldRiskText = findViewById<TextView>(R.id.coldRiskText)
        coldRiskText.text = lifeIndex.coldRisk[0].desc

        val dressingText = findViewById<TextView>(R.id.dressingText)
        dressingText.text = lifeIndex.dressing[0].desc

        val ultravioletText = findViewById<TextView>(R.id.ultravioletText)
        ultravioletText.text = lifeIndex.ultraviolet[0].desc

        val carWashingText = findViewById<TextView>(R.id.carWashingText)
        carWashingText.text = lifeIndex.carWashing[0].desc

        val weatherLayout = findViewById<ScrollView>(R.id.weatherLayout)
        weatherLayout.visibility = View.VISIBLE
    }
}