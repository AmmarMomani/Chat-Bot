package com.ammar.chatfmp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ammar.chatfmp.databinding.FragmentHomeBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var aiMessage: MutableList<AiChat>
    private lateinit var adapter: AiChatAdapter

    private val client = OkHttpClient()
    private val url = "https://api.openai.com/v1/chat/completions"
    private val accessToken = "Here your API key"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        aiMessage = ArrayList()
        adapter = AiChatAdapter(aiMessage)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.btnSend.setOnClickListener {
            val question = binding.editText.text.toString().trim()
            if (question.isNotBlank()) {
                addToRecyclerView(question, AiChat.SENT_BY_ME)
                binding.welcomeTxt.visibility = View.GONE
                binding.editText.text.clear()
                callApi(question)
            }
        }

        return binding.root
    }

    private fun addToRecyclerView(message: String, sentBy: String) {
        aiMessage.add(AiChat(message, sentBy))
        adapter.notifyDataSetChanged()
        binding.recyclerView.smoothScrollToPosition(adapter.itemCount)
    }

    private fun callApi(question: String) {
        addToRecyclerView("Fetching data...", AiChat.SENT_BY_GPT)

        val parameter = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": "$question"}
                ],
                "max_tokens": 500,
                "temperature": 0.7
            }
        """.trimIndent().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .post(parameter)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    aiMessage.removeAt(aiMessage.size - 1)
                    addToRecyclerView("Failed to get a response", AiChat.SENT_BY_GPT)
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        activity?.runOnUiThread {
                            aiMessage.removeAt(aiMessage.size - 1)
                            addToRecyclerView("Failed to get a response", AiChat.SENT_BY_GPT)
                        }
                        return
                    }

                    val body = it.body?.string()
                    if (body == null) {
                        activity?.runOnUiThread {
                            aiMessage.removeAt(aiMessage.size - 1)
                            addToRecyclerView("Failed to get a response", AiChat.SENT_BY_GPT)
                        }
                        return
                    }

                    try {
                        val jsonObject = JSONObject(body)
                        val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
                        val textResult = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                        activity?.runOnUiThread {
                            aiMessage.removeAt(aiMessage.size - 1)
                            addToRecyclerView(textResult.trim(), AiChat.SENT_BY_GPT)
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            aiMessage.removeAt(aiMessage.size - 1)
                            addToRecyclerView("Failed to parse response", AiChat.SENT_BY_GPT)
                        }
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
