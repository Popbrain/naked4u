package io.popbrain.app.naked4u

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import io.popbrain.naked4u.ViewableLogger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.d_btn).setOnClickListener {
            Toast.makeText(this, "[DEBUG] Naked for you!", Toast.LENGTH_SHORT).show()
            Log.d("naked4u", "Naked for you!")
        }
        findViewById<Button>(R.id.i_btn).setOnClickListener {
            Toast.makeText(this, "[INFO] Naked for you!", Toast.LENGTH_SHORT).show()
            Log.i("naked4u", "Naked for you!")
        }
        findViewById<Button>(R.id.w_btn).setOnClickListener {
            Toast.makeText(this, "[WARN] Naked for you!", Toast.LENGTH_SHORT).show()
            Log.w("naked4u", "Naked for you!")
        }
        findViewById<Button>(R.id.e_btn).setOnClickListener {
            Toast.makeText(this, "[ERROR] Naked for you!", Toast.LENGTH_SHORT).show()
            Log.e("naked4u", "Naked for you!")
        }

        findViewById<Button>(R.id.clear_btn).setOnClickListener {
            findViewById<ViewableLogger>(R.id.viewable_logger)
                .clear()
        }
        findViewById<Button>(R.id.clear_filter_btn).setOnClickListener {
            findViewById<ViewableLogger>(R.id.viewable_logger)
            .clearFilter()
        }
        findViewById<Button>(R.id.add_filter_btn).setOnClickListener {
            findViewById<ViewableLogger>(R.id.viewable_logger)
                .addFilter("Naked")
        }
        findViewById<ViewableLogger>(R.id.viewable_logger)
            .start()
    }
}
