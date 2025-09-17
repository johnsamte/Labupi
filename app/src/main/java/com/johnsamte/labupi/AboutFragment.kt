package com.johnsamte.labupi

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.johnsamte.labupi.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val appVersion = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        val htmlText = """
            <p><b>Labupi</b> is a digital song book that brings together two beloved collections 
            of worship songs: <b>Biakna Labupi (BNL)</b> and <b>Pathian Ngaihla (PNL)</b>.</p><br>

            <p>The app is designed to make it easier for users to find, read, and sing along 
            with these treasured hymns anywhere, anytime. With a simple and clean interface, 
            Labupi allows you to quickly search for songs, browse by categories, and keep 
            your favorite songs close at hand.</p><br>

            <p>Whether for personal devotion, choir practice, or church services, Labupi provides 
            a convenient way to access both BNL and PNL in one place. It aims to preserve and 
            promote the use of these songs among the community, making worship more accessible 
            in today’s digital world.</p><br>

            <br><br>
            <p><b>Notes</b></p>
            <p>If you find any mistakes or missing content, please use the support below 
            to report corrections.</p>
            <p>Kipak</p>

            <br><br>
            
            <br>
            <p><b>Developed by:</b> Johnsamte</p>
            <p><b>Email:</b> <a href="mailto:johnsamte28@gmail.com">support@labupi.app</a></p>
            <br>
            
        """.trimIndent()

        binding.aboutTextView.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
        binding.aboutTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
