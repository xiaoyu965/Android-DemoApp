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

// 数据模型
data class QuestionData(
    val modules: Map<String, String>,
    val questions: List<Question>
)

data class Question(
    val module: Int,
    val number: Int,
    val title: String,
    val answer: String
)

class AppFragment : Fragment() {
    // 密封类定义在Fragment类内部，作为成员
    sealed class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class ModuleViewHolder(itemView: View) : NavViewHolder(itemView) {
            val titleText: TextView = itemView.findViewById(R.id.moduleTitle)
            val expandIcon: TextView = itemView.findViewById(R.id.expandIcon)
        }

        class QuestionViewHolder(itemView: View) : NavViewHolder(itemView) {
            val titleText: TextView = itemView.findViewById(R.id.questionTitle)
        }
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navRecyclerView: RecyclerView
    private val expandedModules = mutableSetOf<Int>()
    private lateinit var moduleNames: Map<Int, String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app, container, false)

        // 初始化DrawerLayout
        drawerLayout = view.findViewById(R.id.drawerLayout)
        navRecyclerView = view.findViewById(R.id.navRecyclerView)

        // 从JSON文件加载数据
        val questionData = JsonUtils.parseJsonFromRaw<QuestionData>(requireContext(), R.raw.app)
        moduleNames = questionData.modules.mapKeys { it.key.toInt() }
        val questions = questionData.questions.sortedBy { it.module }

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

        val groupedQuestions = questions.groupBy { it.module }
        val navItems = mutableListOf<NavItem>()

        groupedQuestions.forEach { (module, questions) ->
            // 使用moduleNames获取模块名称
            val moduleName = moduleNames[module] ?: "Module $module"
            navItems.add(NavItem.ModuleItem(module, moduleName))

            if (module in expandedModules) {
                questions.forEach { question ->
                    navItems.add(NavItem.QuestionItem(question))
                }
            }
        }

        val navAdapter = NavAdapter(navItems) { position ->
            val item = navItems[position]
            when (item) {
                is NavItem.ModuleItem -> {
                    if (item.module in expandedModules) {
                        expandedModules.remove(item.module)
                    } else {
                        expandedModules.add(item.module)
                    }
                    setupNavigation(questions, mainRecyclerView)
                }
                is NavItem.QuestionItem -> {
                    val questionPosition = questions.indexOfFirst { it.number == item.question.number }
                    if (questionPosition != -1) {
                        mainRecyclerView.scrollToPosition(questionPosition)
                    }
                    drawerLayout.closeDrawers()
                }
            }
        }
        navRecyclerView.adapter = navAdapter
    }

    // 导航项密封类
    sealed class NavItem {
        data class ModuleItem(val module: Int, val moduleName: String) : NavItem()
        data class QuestionItem(val question: Question) : NavItem()
    }

    // 主列表适配器
    class QuestionAdapter(private val questions: List<Question>) :
        RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

        class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idText: TextView = itemView.findViewById(R.id.idText)
            val questionText: TextView = itemView.findViewById(R.id.questionText)
            val answerText: TextView = itemView.findViewById(R.id.answerText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question, parent, false)
            return QuestionViewHolder(view)
        }

        override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
            val question = questions[position]
            holder.idText.text = "${question.module}.${question.number}"
            holder.questionText.text = question.title
            holder.answerText.text = "\t\t" + question.answer

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

    // 导航适配器修改为使用外部定义的NavViewHolder
    private inner class NavAdapter(
        private val navItems: List<NavItem>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<NavViewHolder>() {  // 直接使用外部定义的NavViewHolder

        override fun getItemViewType(position: Int): Int {
            return when (navItems[position]) {
                is NavItem.ModuleItem -> R.layout.item_nav_module
                is NavItem.QuestionItem -> R.layout.item_nav_question
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
            return when (viewType) {
                R.layout.item_nav_module -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_nav_module, parent, false)
                    NavViewHolder.ModuleViewHolder(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_nav_question, parent, false)
                    NavViewHolder.QuestionViewHolder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
            when (holder) {
                is NavViewHolder.ModuleViewHolder -> {
                    val moduleItem = navItems[position] as NavItem.ModuleItem
                    holder.titleText.text = moduleItem.moduleName
                    holder.expandIcon.text = if (moduleItem.module in expandedModules) "▼" else "▶"
                    holder.itemView.setOnClickListener { onItemClick(position) }
                }
                is NavViewHolder.QuestionViewHolder -> {
                    val questionItem = navItems[position] as NavItem.QuestionItem
                    holder.titleText.text = "${questionItem.question.number}. ${questionItem.question.title}"
                    holder.itemView.setOnClickListener { onItemClick(position) }
                }
            }
        }

        override fun getItemCount() = navItems.size
    }
}