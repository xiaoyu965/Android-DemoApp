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

data class QuestionData(
    val modules: Map<String, String>,
    val submodules: Map<String, Map<String, String>>,
    val questions: List<Question>
)

data class Question(
    val module: Int,
    val submodule: Int? = null,
    val title: String,
    val answer: String,
    var moduleLevelNumber: Int = 0,
    var submoduleLevelNumber: Int = 0
)

class AppFragment : Fragment() {
    sealed class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class ModuleViewHolder(itemView: View) : NavViewHolder(itemView) {
            val titleText: TextView = itemView.findViewById(R.id.moduleTitle)
            val expandIcon: TextView = itemView.findViewById(R.id.expandIcon)
        }

        class SubmoduleViewHolder(itemView: View) : NavViewHolder(itemView) {
            val titleText: TextView = itemView.findViewById(R.id.submoduleTitle)
            val expandIcon: TextView = itemView.findViewById(R.id.expandIcon)
        }

        class QuestionViewHolder(itemView: View) : NavViewHolder(itemView) {
            val titleText: TextView = itemView.findViewById(R.id.questionTitle)
        }
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navRecyclerView: RecyclerView
    private val expandedModules = mutableSetOf<Int>()
    private val expandedSubmodules = mutableMapOf<Int, MutableSet<Int>>()
    private lateinit var moduleNames: Map<Int, String>
    private lateinit var submoduleNames: Map<Int, Map<Int, String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app, container, false)

        drawerLayout = view.findViewById(R.id.drawerLayout)
        navRecyclerView = view.findViewById(R.id.navRecyclerView)

        val questionData = JsonUtils.parseJsonFromRaw<QuestionData>(requireContext(), R.raw.app)
        moduleNames = questionData.modules.mapKeys { it.key.toInt() }
        submoduleNames = questionData.submodules.mapKeys { it.key.toInt() }
            .mapValues { it.value.mapKeys { entry -> entry.key.toInt() } }

        val questions = processAutoNumbering(questionData.questions)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = QuestionAdapter(questions)
        recyclerView.adapter = adapter

        setupNavigation(questions, recyclerView)

        view.findViewById<ImageButton>(R.id.menuButton)?.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        return view
    }

    private fun processAutoNumbering(questions: List<Question>): List<Question> {
        val result = mutableListOf<Question>()

        // 按模块分组
        val byModule = questions.groupBy { it.module }

        byModule.forEach { (module, moduleQuestions) ->
            // 分离模块级别和子模块级别问题
            val moduleLevelQuestions = moduleQuestions.filter { it.submodule == null }
            val submoduleLevelQuestions = moduleQuestions.filter { it.submodule != null }

            // 处理模块级别问题编号 (如3.1)
            moduleLevelQuestions.forEachIndexed { index, question ->
                result.add(question.copy(moduleLevelNumber = index + 1))
            }

            // 处理子模块级别问题编号 (如3.2.1)
            val bySubmodule = submoduleLevelQuestions.groupBy { it.submodule }
            bySubmodule.forEach { (submodule, submoduleQuestions) ->
                submodule?.let {
                    submoduleQuestions.forEachIndexed { index, question ->
                        result.add(question.copy(
                            moduleLevelNumber = it, // 子模块号
                            submoduleLevelNumber = index + 1
                        ))
                    }
                }
            }
        }

        return result.sortedWith(compareBy<Question> { it.module }
            .thenBy { it.submodule ?: 0 }
            .thenBy { if (it.submodule == null) it.moduleLevelNumber else it.submoduleLevelNumber }
        )
    }

    private fun setupNavigation(questions: List<Question>, mainRecyclerView: RecyclerView) {
        navRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val groupedByModule = questions.groupBy { it.module }
        val navItems = mutableListOf<NavItem>()

        groupedByModule.forEach { (module, moduleQuestions) ->
            val moduleName = moduleNames[module] ?: "Module $module"
            navItems.add(NavItem.ModuleItem(module, moduleName))

            if (module in expandedModules) {
                // 初始化该模块的展开子模块集合
                if (!expandedSubmodules.containsKey(module)) {
                    expandedSubmodules[module] = mutableSetOf()
                }

                // 先添加无子模块的问题
                moduleQuestions.filter { it.submodule == null }
                    .sortedBy { it.moduleLevelNumber }
                    .forEach { question ->
                        navItems.add(NavItem.QuestionItem(question))
                    }

                // 再处理有子模块的问题
                moduleQuestions.filter { it.submodule != null }
                    .groupBy { it.submodule }
                    .forEach { (submodule, submoduleQuestions) ->
                        submodule?.let {
                            val submoduleName = submoduleNames[module]?.get(it) ?: "Submodule $it"
                            navItems.add(NavItem.SubmoduleItem(module, it, submoduleName))

                            // 检查当前模块下的子模块是否展开
                            if (expandedSubmodules[module]?.contains(it) == true) {
                                submoduleQuestions.sortedBy { it.submoduleLevelNumber }.forEach { question ->
                                    navItems.add(NavItem.QuestionItem(question))
                                }
                            }
                        }
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
                is NavItem.SubmoduleItem -> {
                    val moduleSubmodules = expandedSubmodules[item.module] ?: mutableSetOf()
                    if (moduleSubmodules.contains(item.submodule)) {
                        moduleSubmodules.remove(item.submodule)
                    } else {
                        moduleSubmodules.add(item.submodule)
                    }
                    expandedSubmodules[item.module] = moduleSubmodules
                    setupNavigation(questions, mainRecyclerView)
                }
                is NavItem.QuestionItem -> {
                    val questionPosition = questions.indexOfFirst { q ->
                        q.module == item.question.module &&
                                q.submodule == item.question.submodule &&
                                (if (q.submodule == null) q.moduleLevelNumber else q.submoduleLevelNumber) ==
                                (if (item.question.submodule == null) item.question.moduleLevelNumber else item.question.submoduleLevelNumber)
                    }
                    if (questionPosition != -1) {
                        mainRecyclerView.scrollToPosition(questionPosition)
                    }
                    drawerLayout.closeDrawers()
                }
            }
        }
        navRecyclerView.adapter = navAdapter
    }

    sealed class NavItem {
        data class ModuleItem(val module: Int, val moduleName: String) : NavItem()
        data class SubmoduleItem(val module: Int, val submodule: Int, val submoduleName: String) : NavItem()
        data class QuestionItem(val question: Question) : NavItem()
    }

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
            val idText = if (question.submodule != null) {
                "${question.module}.${question.moduleLevelNumber}.${question.submoduleLevelNumber}"
            } else {
                "${question.module}.${question.moduleLevelNumber}"
            }
            holder.idText.text = idText
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

    private inner class NavAdapter(
        private val navItems: List<NavItem>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<NavViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return when (navItems[position]) {
                is NavItem.ModuleItem -> R.layout.item_nav_module
                is NavItem.SubmoduleItem -> R.layout.item_nav_submodule
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
                R.layout.item_nav_submodule -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_nav_submodule, parent, false)
                    NavViewHolder.SubmoduleViewHolder(view)
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
                is NavViewHolder.SubmoduleViewHolder -> {
                    val submoduleItem = navItems[position] as NavItem.SubmoduleItem
                    holder.titleText.text = submoduleItem.submoduleName
                    val isExpanded = expandedSubmodules[submoduleItem.module]?.contains(submoduleItem.submodule) ?: false
                    holder.expandIcon.text = if (isExpanded) "▼" else "▶"
                    holder.itemView.setOnClickListener { onItemClick(position) }
                }
                is NavViewHolder.QuestionViewHolder -> {
                    val questionItem = navItems[position] as NavItem.QuestionItem
                    val prefix = if (questionItem.question.submodule != null) {
                        "${questionItem.question.module}.${questionItem.question.moduleLevelNumber}.${questionItem.question.submoduleLevelNumber}."
                    } else {
                        "${questionItem.question.module}.${questionItem.question.moduleLevelNumber}."
                    }
                    holder.titleText.text = "$prefix ${questionItem.question.title}"
                    holder.itemView.setOnClickListener { onItemClick(position) }
                }
            }
        }

        override fun getItemCount() = navItems.size
    }
}