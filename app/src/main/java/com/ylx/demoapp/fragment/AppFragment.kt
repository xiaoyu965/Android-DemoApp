package com.ylx.demoapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ylx.demoapp.R
import com.ylx.demoapp.utils.JsonUtils

class AppFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app, container, false)

        // 初始化DrawerLayout
        drawerLayout = view.findViewById(R.id.drawerLayout)
        navRecyclerView = view.findViewById(R.id.navRecyclerView)

        // 从JSON文件加载问题
        val questions: List<Question> = JsonUtils.parseJsonFromRaw(requireContext(), R.raw.app)

        // 绑定主RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = QuestionAdapter(questions)
        recyclerView.adapter = adapter

        // 设置导航RecyclerView
        setupNavigation(questions, recyclerView)

        // 设置菜单按钮点击事件
        view.findViewById<ImageButton>(R.id.menuButton)?.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        return view
    }

    private fun setupNavigation(questions: List<Question>, mainRecyclerView: RecyclerView) {
        navRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val navAdapter = NavAdapter(questions) { position ->
            // 点击导航项后跳转到对应位置
            mainRecyclerView.scrollToPosition(position)
            drawerLayout.closeDrawers()
        }
        navRecyclerView.adapter = navAdapter
    }

    // 数据模型
    data class Question(
        val id: Int,
        val title: String, // 题目
        val answer: String // 答案
    )

    // 主列表适配器
    class QuestionAdapter(private val questions: List<Question>) :
        RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

        // 定义 ViewHolder
        class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idText: TextView = itemView.findViewById(R.id.idText)
            val questionText: TextView = itemView.findViewById(R.id.questionText)
            val answerText: TextView = itemView.findViewById(R.id.answerText)
        }

        // 创建 ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question, parent, false)
            return QuestionViewHolder(view)
        }

        // 绑定数据
        override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
            val question = questions[position]
            holder.idText.text = question.id.toString()
            holder.questionText.text = question.title
            holder.answerText.text = "\t\t" + question.answer

            // 点击题目展开/收起答案
            holder.questionText.setOnClickListener {
                if (holder.answerText.visibility == View.GONE) {
                    holder.answerText.visibility = View.VISIBLE
                } else {
                    holder.answerText.visibility = View.GONE
                }
            }
        }

        override fun getItemCount() = questions.size
    }

    // 导航适配器
    private inner class NavAdapter(
        private val questions: List<Question>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<NavAdapter.NavViewHolder>() {

        inner class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleText: TextView = itemView.findViewById(R.id.navItemText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_nav, parent, false)
            return NavViewHolder(view)
        }

        override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
            holder.titleText.text = questions[position].title
            holder.itemView.setOnClickListener { onItemClick(position) }
        }

        override fun getItemCount() = questions.size
    }
}