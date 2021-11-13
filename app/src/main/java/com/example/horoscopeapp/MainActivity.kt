package com.example.horoscopeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonReader
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    AdapterView.OnItemSelectedListener {
    var sunSign="Aries"
     var resultView:TextView? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

var buttonView: Button=findViewById(R.id.button)
        button.setOnClickListener{
            GlobalScope.async { getPredictions(buttonView) }
        }



        var spinner=findViewById<Spinner>(R.id.spinner)
        var adapter=ArrayAdapter.createFromResource(this,R.array.sunsigns,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapter
        spinner.onItemSelectedListener=this

        resultView = findViewById(R.id.resultView)


    }

    public suspend fun getPredictions(view: View) {

        try {

            val result=GlobalScope.async { callAztroAPI("https://sameer-kumar-aztro-v1.p.rapidapi.com/?sign="
            +sunSign+"&day=today") }.await()
            
            onResponse(result)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun onResponse(result: String?) {

        try {

           val resultJson=JSONObject(result)
            var Prediction="Today's prediction "
            Prediction += this.sunSign+"\t"

            Prediction += resultJson.getString("date_range")+"\n"
            Prediction += resultJson.getString("description")

            setText(this.resultView,Prediction)

        }catch (e:Exception){
            e.printStackTrace()
            this.resultView!!.text = "Oops!! something went wrong, please try again"
        }

    }

    private fun setText(resultView: TextView?, Prediction: String) {
        runOnUiThread { resultView!!.text = Prediction }
    }


    private fun callAztroAPI(apiUrl:String):String?{
        var result: String? = ""
        val url: URL;
        var connection: HttpURLConnection? = null

        try {

            url= URL(apiUrl)
            connection=url.openConnection() as HttpURLConnection
            connection.setRequestProperty("x-rapidapi-host","sameer-kumar-aztro-v1.p.rapidapi.com")
            connection.setRequestProperty("x-rapidapi-key","7b92cfdfa8msh1ece9a767ca7451p1c6070jsn93ae2200bbe7")
            connection.setRequestProperty("content-type","application/x-www-form-urlencoded")

            connection.requestMethod="POST"
            val `in` = connection.inputStream
            val reader=InputStreamReader(`in`)

            var data = reader.read()
            while (data != -1) {
                val current = data.toChar()
                result += current
                data = reader.read()
            }
            return result

        }catch (e:Exception){
            e.printStackTrace()

        }
        return null


    }



    

    override fun onNothingSelected(parent: AdapterView<*>?) {
        var sunSign="Aries"

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!=null){
            sunSign=parent.getItemAtPosition(position).toString()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }


}