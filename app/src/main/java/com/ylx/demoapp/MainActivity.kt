package com.ylx.demoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.ylx.ability.LogUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.i("MainActivity", "onCreate")
        enableEdgeToEdge()
        setContentView(R.layout.act_main)
        val button = findViewById<Button>(R.id.bt_jump_fir)
        button.setOnClickListener {
            // 创建一个Intent来启动SecondActivity
            val intent = Intent(this, FirstActivity::class.java)
            // 如果需要，你可以向Intent中添加额外的数据
            // intent.putExtra("key", "value")
            // 启动SecondActivity
            startActivity(intent)
        }
    }
}