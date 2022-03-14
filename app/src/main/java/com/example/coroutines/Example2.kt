package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_example2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class Example2 : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000
    private lateinit var job : CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example2)

        job_btn.setOnClickListener {
            if (!::job.isInitialized){
                initJob()
            }
            progressBar.startJobOrCancel(job)
        }
    }

    fun ProgressBar.startJobOrCancel(job:Job){
        if (this.progress > 0){
            println("${job} this job is already active")
            restJob()
        }else {
            job_btn.text = "Cancel Job #1"
            CoroutineScope(IO + job).launch {
                println("Coroutine ${this} is activiated with job  ${job}")
                for (i in PROGRESS_START .. PROGRESS_MAX){
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("JOB IS COMPLETE")
            }
        }
    }

    private fun updateJobCompleteTextView(Text : String){
        GlobalScope.launch(Main) {
            job_complete.text = Text
        }
    }

    private fun restJob() {
        if (job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()
    }

    fun initJob(){
        job_btn.text = "Start Job #1"
        updateJobCompleteTextView("")
        job = Job()

        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()){
                    msg = "Unknown cancellation error."
                }
                println("${job} was cancelled . Reason $msg")
                showToast(msg)
            }
        }

        progressBar.max = PROGRESS_MAX
        progressBar.progress = PROGRESS_START
    }

    fun showToast(text : String){
        GlobalScope.launch(Main) {
            Toast.makeText(this@Example2, text, Toast.LENGTH_SHORT).show()
        }
    }


}