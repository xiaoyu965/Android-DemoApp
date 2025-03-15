package com.ylx.demoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ylx.ability.LogUtils
import com.ylx.demoapp.fragment.AlgFragment
import com.ylx.demoapp.fragment.AppFragment
import com.ylx.demoapp.fragment.FwkFragment
import com.ylx.demoapp.fragment.MeFragment
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.i("MainActivity", "onCreate")
        enableEdgeToEdge()
        setContentView(R.layout.act_main)

        // 设置状态栏颜色
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        // 设置 Toolbar 标题颜色
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // 设置Toolbar标题文字颜色为黑色，但需注意如果使用自定义标题视图，此设置可能无效。
//        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.wechat_green))

        // 加载 XML 布局
        val titleView = layoutInflater.inflate(R.layout.toolbar_title, toolbar, false)
        toolbar.addView(titleView)

        // 隐藏系统默认标题
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 调整 Toolbar padding
        toolbar.setPadding(
            0,
            resources.getDimensionPixelSize(R.dimen.status_bar_padding),
            0,
            toolbar.paddingBottom
        )

        // 初始化组件
        viewPager2 = findViewById(R.id.viewPager2)
        bottomNav = findViewById(R.id.bottomNav)
        val button = findViewById<Button>(R.id.bt_jump_fir)


        setupViewPager()
        setupBottomNav()

        button.setOnClickListener {
            // 创建一个Intent来启动SecondActivity
            val intent = Intent(this, FirstActivity::class.java)
            // 如果需要，你可以向Intent中添加额外的数据
            // intent.putExtra("key", "value")
            // 启动SecondActivity
            startActivity(intent)
        }
    }

    private fun setupViewPager() {
        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> AppFragment()
                1 -> FwkFragment()
                2 -> AlgFragment()
                3 -> MeFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }

        // 是否禁止手动滑动切换
        viewPager2.isUserInputEnabled = true
        // 动画
        viewPager2.setPageTransformer(DepthPageTransformer())  // 方案1
//        viewPager2.setPageTransformer(ZoomOutPageTransformer()) // 方案2
    }

    private fun setupBottomNav() {
        // 点击底部导航切换页面
        bottomNav.setOnItemSelectedListener { item ->
            val position = when (item.itemId) {
                R.id.nav_app -> 0
                R.id.nav_fwk -> 1
                R.id.nav_alg -> 2
                R.id.nav_me -> 3
                else -> 0
            }
            viewPager2.setCurrentItem(position, false)
            true
        }

        // 滑动页面同步底部导航
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNav.selectedItemId = when (position) {
                    0 -> R.id.nav_app
                    1 -> R.id.nav_fwk
                    2 -> R.id.nav_alg
                    3 -> R.id.nav_me
                    else -> throw IllegalArgumentException()
                }
            }
        })
    }

    class DepthPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            when {
                position <= 0 -> {
                    page.alpha = 1f
                    page.translationX = 0f
                    page.scaleX = 1f
                    page.scaleY = 1f
                }

                position <= 1 -> {
                    page.alpha = 1 - position
                    page.translationX = page.width * -position
                    page.scaleX = 0.8f + 0.2f * (1 - abs(position))
                    page.scaleY = 0.8f + 0.2f * (1 - abs(position))
                }
            }
        }
    }

    class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        private val MIN_SCALE = 0.85f
        private val MIN_ALPHA = 0.5f

        override fun transformPage(page: View, position: Float) {
            when {
                position < -1 -> {
                    page.alpha = 0f
                }

                position <= 1 -> {
                    val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                    page.scaleX = scaleFactor
                    page.scaleY = scaleFactor
                    page.alpha =
                        (MIN_ALPHA + ((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA))
                }

                else -> {
                    page.alpha = 0f
                }
            }
        }
    }
}