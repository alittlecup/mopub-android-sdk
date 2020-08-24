package mobi.idealabs.editor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView

class TestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContentView(textView)
        textView.text = this.toString()
        textView.gravity = Gravity.CENTER
        textView.textSize = 30f
        fragmentManager
        textView.setOnClickListener {
            startActivity(Intent(this@TestActivity, TestActivity::class.java))
            textView.postDelayed({
                finish()
            }, 1000)
        }
    }
}