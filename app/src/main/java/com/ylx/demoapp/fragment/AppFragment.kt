package com.ylx.demoapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ylx.demoapp.R
import com.ylx.demoapp.utils.JsonUtils

class AppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app, container, false)

        // 从 JSON 文件加载问题
        val questions: List<Question> = JsonUtils.parseJsonFromRaw(requireContext(), R.raw.app)

        // 绑定 RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = QuestionAdapter(questions)
        recyclerView.adapter = adapter

        return view
    }

    // 数据模型
    data class Question(
        val id: Int,
        val title: String, // 题目
        val answer: String // 答案
    )

    // 适配器
    class QuestionAdapter(private val questions: List<Question>) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

        // 定义 ViewHolder
        class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idText: TextView = itemView.findViewById(R.id.idText)
            val questionText: TextView = itemView.findViewById(R.id.questionText)
            val answerText: TextView = itemView.findViewById(R.id.answerText)
        }

        // 创建 ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
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

        // 返回数据数量
        override fun getItemCount(): Int {
            return questions.size
        }
    }
}