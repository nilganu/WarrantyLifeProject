package com.example.warrantylifeproject

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SecondActivity : AppCompatActivity() {
    //Variable intiatied later in the code
    lateinit var edtFrequency:TextInputEditText
    lateinit var edtLayoutFrequency:TextInputLayout
    lateinit var btnSubmit:Button
    lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        //views instantiation
        edtFrequency=findViewById(R.id.edtFrequency)
        edtLayoutFrequency=findViewById(R.id.edtLayoutFrequency)
        btnSubmit=findViewById(R.id.btnSubmit)
        lineChart=findViewById(R.id.lineChart)

        //button click
        btnSubmit.setOnClickListener {

            //Retriving frequency data from edittext after checking null or blank
            if(edtFrequency.text.toString()!=null && edtFrequency.text.toString() !="")
            {
                var frequency =edtFrequency.text.toString().toInt() // HZ

                //error handling
                if(frequency < 1 || frequency> 24000)
                {

                    edtLayoutFrequency.helperText="Frequency value must be in range 1 to 240000"
                    edtFrequency.setText("")
                }
                else
                {
                    var duration = 30 // duration of sound
                    val numSamples = duration * frequency
                    val samples = DoubleArray(numSamples)
                    val buffer = ShortArray(numSamples)
                    val arrayList= ArrayList<Entry>()
                    //Sine wave and higher amplitude increase value calculation
                    for (i in 0 until numSamples) {
                        samples[i] = Math.sin(2 * Math.PI * i / (frequency/500)) // Sine wave
                        buffer[i] = (samples[i] * Short.MAX_VALUE).toInt()
                            .toShort() // Higher amplitude increases volume
                        arrayList.add(Entry(i.toFloat(),buffer[i].toFloat()))
                    }
                    // AudioTrack instantiation to play sound
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                    val audioFormat = AudioFormat.Builder()
                        .setSampleRate(frequency)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()

                    val audioTrack = AudioTrack(
                        audioAttributes,
                        audioFormat,
                        buffer.size,
                        AudioTrack.MODE_STREAM,
                        0
                    )
                    audioTrack.write(buffer, 0, buffer.size);
                    audioTrack.play();
                    //Function to plot the frequency graph
                    plotTheGraph(arrayList)
                }

            }
            else
            {
                edtLayoutFrequency.helperText="Frequency value must not be blank or null"
            }
        }

    }

    private fun plotTheGraph(arrayList: ArrayList<Entry>) {
        //data preparation of line graph
        var subArrayList=arrayList.slice(1..50)
        var lineDataSet = LineDataSet(subArrayList,"Data set 1")
        var dataset= ArrayList<ILineDataSet>()
        dataset.add(lineDataSet)

        //attach prepared data with chart
        var lineData = LineData(dataset)
        lineChart.data=lineData
        lineChart.invalidate()
    }
}