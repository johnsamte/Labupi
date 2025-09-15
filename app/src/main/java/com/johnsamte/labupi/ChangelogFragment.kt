package com.johnsamte.labupi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnsamte.labupi.databinding.FragmentChangelogBinding
import org.json.JSONArray
import java.net.URL
import kotlin.concurrent.thread

class ChangelogFragment : Fragment() {
    private var _binding: FragmentChangelogBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ChangelogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangelogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChangelogAdapter()
        binding.changelogRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.changelogRecycler.adapter = adapter

        fetchGithubReleases()
    }

    private fun fetchGithubReleases() {
        val apiUrl = "https://api.github.com/repos/johnsamte/Labupi/releases"

        thread {
            try {
                val jsonStr = URL(apiUrl).readText()
                val jsonArray = JSONArray(jsonStr)

                val changelogList = mutableListOf<ChangelogItem>()

                for (i in 0 until jsonArray.length()) {
                    val release = jsonArray.getJSONObject(i)

                    val id = release.getString("id")
                    val title = release.getString("name") // Release title
                    val body = release.getString("body") // Notes
                    val date = release.getString("published_at").substring(0, 10)

                    val assets = release.optJSONArray("assets")
                    val downloadUrl = if (assets != null && assets.length() > 0) {
                        assets.getJSONObject(0).getString("browser_download_url")
                    } else ""

                    changelogList.add(
                        ChangelogItem(
                            id = id,
                            title = title,
                            date = date,
                            body = body,
                            downloadUrl = downloadUrl
                        )
                    )
                }

                requireActivity().runOnUiThread {
                    adapter.setItems(changelogList)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
