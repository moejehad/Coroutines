package com.example.coroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result 1"
    private val RESULT_2 = "Result 2"
    val JOB_TIMEOUT = 2100L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_coro.setOnClickListener {
            //IO , MAIN , DEFAULT

            CoroutineScope(IO).launch {
                //fakeApiRequest()
                fakeApi()
            }
        }
    }


    private suspend fun fakeApi(){
        withContext(IO){
            val job = withTimeoutOrNull(JOB_TIMEOUT){
                val result = getResult1FromApi()
                println("result 1 : ${result}")
                setTxtOnMainThread(result)

                val result2 = getResult2FromApi()
                setTxtOnMainThread(result2)
            }

            if (job == null){
                val cancle = "Cancelling msg ... jon took longer than ${JOB_TIMEOUT}"
                println(cancle)
                setTxtOnMainThread(cancle)
            }
        }
    }

    private fun setNewText(input : String){
        val newText = txt_coro.text.toString() + "\n$input"
        txt_coro.text = newText
    }

    private suspend fun setTxtOnMainThread(input : String){
        withContext(Main){
            setNewText(input)
        }
    }

    /*private suspend fun fakeApiRequest(){
        val result1 = getResult1FromApi()
        println("debug : ${result1}")
        setTxtOnMainThread(result1)

        val result2 = getResult2FromApi()
        setTxtOnMainThread(result2)
    }*/

    private suspend fun getResult1FromApi() : String{
        logThread("getResult1FromApi")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi() : String{
        logThread("getResult2FromApi")
        delay(1000)
        return RESULT_2
    }

    private fun logThread(methodName:String){
        println("debug : ${methodName} : ${Thread.currentThread().name}")
    }
}