package com.example.weatherapp

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.weatherapp.constant.Const
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.Main
import com.example.weatherapp.model.Weather
import com.example.weatherapp.sevices.Api
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#218DBD")
        supportActionBar!!.hide()



        binding.buttonBuscar.setOnClickListener {
            binding.searchProgressbar.visibility = View.VISIBLE
            val cidade = binding.editBuscarCidade.text.toString()
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build()
                .create(Api::class.java)

            retrofit.weatherMap(cidade, Const.API_KEY).enqueue(object : Callback<Main> {
                override fun onResponse(call: Call<Main>, response: Response<Main>) {
                    if (response.isSuccessful) {
                        println(response.body())
                        binding.progressLoadImage.visibility = View.GONE
                        respostaServidor(response)
                    } else {
                        Toast.makeText(applicationContext, "Cidade inválida", Toast.LENGTH_SHORT).show()
                        binding.searchProgressbar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Main>, t: Throwable) {
                    Toast.makeText(applicationContext, "Erro fatal de servidor!", Toast.LENGTH_SHORT)
                        .show()
                    binding.searchProgressbar.visibility = View.INVISIBLE
                }
            })
        }

        binding.switchTrocarTema.setOnCheckedChangeListener {buttonView, isChecked ->
            if (isChecked) { //Tema Escuro - Dark Mode
                binding.constraintPrincipal.setBackgroundColor(Color.parseColor("#FF000000"))
                window.statusBarColor = Color.parseColor("#FF000000")

            } else { //Tema Claro - Light Mode
                binding.constraintPrincipal.setBackgroundColor(Color.parseColor("#218DBD"))
                window.statusBarColor = Color.parseColor("#218DBD")
            }
        }


    }

    override fun onResume() {
        binding.searchProgressbar.visibility = View.VISIBLE
        super.onResume()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(Api::class.java)

        retrofit.weatherMap("Macaé", Const.API_KEY).enqueue(object : Callback<Main> {
            override fun onResponse(call: Call<Main>, response: Response<Main>) {
                if (response.isSuccessful) {
                println(response.body())
                    binding.progressLoadImage.visibility = View.GONE
                    respostaServidor(response)

                }
            }

            override fun onFailure(call: Call<Main>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro fatal de servidor!", Toast.LENGTH_SHORT)
                    .show()
                binding.searchProgressbar.visibility = View.INVISIBLE
            }
        })
    }



    @SuppressLint("SetTextI18n")
    private fun respostaServidor(response: Response<Main>) {
        val main = response.body()!!.main
        val sys = response.body()!!.sys
        val weather:MutableList<Weather> = response.body()!!.weather
        var pais = ""


        val temp = main.get("temp").toString()
        val tempMin = main.get("temp_min").toString()
        val tempMax = main.get("temp_max").toString()
        val humidity = main.get("humidity").toString()

        //Fórmula para converter Kelvin em Celsius
        val temp_c = (temp.toDouble() - 273.15)
        val tempMin_c = (tempMin.toDouble() - 273.15)
        val tempMax_c = (tempMax.toDouble() - 273.15)

        //Formatando os Decimais
        val decimalFormat = DecimalFormat("00")

        val country = sys.get("country").asString
        if(country.equals("BR")){
            pais = "Brasil"
        }

        val mainWeather = weather[0].main
        val description = weather[0].description
        val name = response.body()!!.name

        //Tratativa das imagens
        when {
            mainWeather == "Clouds"  && description == "few clouds"-> {
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
            }
            mainWeather == "Clouds" && description == "scattered clouds" -> {
                binding.imgClima.setBackgroundResource(R.drawable.clouds)
            }
            mainWeather == "Clouds" && description == "broken clouds" -> {
                binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
            }
            mainWeather == "Clouds" && description == "overcast clouds" -> {
                binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
            }
            mainWeather == "Clear" -> {
                binding.imgClima.setBackgroundResource(R.drawable.clearsky)
            }
            mainWeather == "Snow" -> {
                binding.imgClima.setBackgroundResource(R.drawable.snow)
            }
            mainWeather == "Rain"  -> {
                binding.imgClima.setBackgroundResource(R.drawable.rain)
            }
            mainWeather == "Drizzle"  -> {
                binding.imgClima.setBackgroundResource(R.drawable.rain)
            }
            mainWeather == "Thunderstorm"  -> {
                binding.imgClima.setBackgroundResource(R.drawable.thunderstorm)
            }


        }

        val descricaoClima = when(description) {
            "clear sky" -> {
                "Céu limpo"
            }
            "few clouds" -> {
                "Poucas núvens"
            }
            "scattered clouds" -> {
                "Núvens esparsas"
            }
            "broken clouds" -> {
                "Núvens quebradas"
            }
            "overcast clouds" -> {
                "Nublado"
            }
            "shower rain" -> {
                "nuvens com chuva"
            }
            "rain" -> {
                "Chuva"
            }
            "thunderstorm" -> {
                "Tempestade"
            }
            "snow" -> {
                "Neve"
            }
            else -> {
    "Névoa"
            }
        }









        binding.txtTemperatura.text = "${decimalFormat.format(temp_c)} ºC"
        binding.txtPaisCidade.text = "$name - $pais"
        binding.txtInfoClima.text = "Clima \n $descricaoClima \n \n Umidade \n $humidity"
        binding.txtInfoTemperatura.text = "Temp.Min \n ${decimalFormat.format(tempMin_c)} \n \n Temp.Max \n ${decimalFormat.format(tempMax_c)}"

        binding.searchProgressbar.visibility = View.INVISIBLE


    }
}