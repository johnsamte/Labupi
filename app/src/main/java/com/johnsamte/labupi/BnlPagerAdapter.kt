package com.johnsamte.labupi

import android.content.res.Resources
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.johnsamte.labupi.databinding.ItemBnlPnlBinding

class BnlPagerAdapter(
    private val BNL: List<LabuData>
) : RecyclerView.Adapter<BnlPagerAdapter.BiaknalaViewHolder>() {

    inner class BiaknalaViewHolder(private val binding: ItemBnlPnlBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Single MediaPlayer instance for this ViewHolder
        private var mediaPlayer: MediaPlayer? = null

        // Map notes from DB → asset file names
        private val noteToFileMap = mapOf(
            "DOH IS A" to "a.mp3",
            "DOH IS A#" to "a_sharp.mp3",
            "DOH IS B♭" to "b_flat.mp3",
            "DOH IS B" to "b.mp3",
            "DOH IS C" to "c.mp3",
            "DOH IS C#" to "c_sharp.mp3",
            "DOH IS D♭" to "d_flat.mp3",
            "DOH IS D" to "d.mp3",
            "DOH IS D#" to "d_sharp.mp3",
            "DOH IS E♭" to "e_flat.mp3",
            "DOH IS E" to "e.mp3",
            "DOH IS F" to "f.mp3",
            "DOH IS F#" to "f_sharp.mp3",
            "DOH IS G♭" to "g_flat.mp3",
            "DOH IS G" to "g.mp3",
            "DOH IS G#" to "g_sharp.mp3",
            "DOH IS A♭" to "a_flat.mp3",
            "DOH IS E♭ DUET" to "e_flat.mp3",
            "DOH IS A♭ Damdam in" to "a_flat.mp3",
            "DOH IS C DUET" to "c.mp3",
            "DOH IS A♭ KHUANGTHUM A KHEN" to "a_flat.mp3",
            "DOH IS E♭ KINDEUH IN" to "e_flat.mp3",
            "DOH IS G MODERATO" to "g.mp3",
            "DOH IS A♭ (TEHNA KHATA KHUANG LI PAI)" to "a_flat.mp3",
            "DOH IS A SLOW WALTZ, TEMPO" to "a.mp3",
            "DOH IS C [LAH IS A]" to "c.mp3"

        )

        fun bind(biaknala: LabuData) {
            //val context = binding.root.context

            binding.laNumber.text = biaknala.la_number
            binding.laThulu.text = biaknala.la_thulu
            binding.laThupineu.text = biaknala.la_thupineu

            // Handle titles
            setupText(binding.laPhuaktu1, biaknala.la_phuaktu1)
            setupText(binding.laPhuaktu2, biaknala.la_phuaktu2)
            setupText(binding.laPhuaktu3, biaknala.la_phuaktu3)
            setupText(binding.laPhuaktu4, biaknala.la_phuaktu4)

            // Handle key
            if (biaknala.la_key.isNullOrEmpty()) {
                binding.laKey.visibility = View.GONE
            } else {
                binding.laKey.visibility = View.VISIBLE
                binding.laKeyText.text = biaknala.la_key

                binding.laKey.setOnClickListener {
                    val note = biaknala.la_key.trim()
                    playNoteFromDbKey(note)
                }
            }

            // Collect layouts and texts in lists
            val verseLayouts = listOf(
                binding.verse1, binding.verse2, binding.verse3, binding.verse4,
                binding.verse5, binding.verse6, binding.verse7, binding.verse8,
                binding.verse9, binding.verse10
            )

            val verseTexts = listOf(
                binding.laText1, binding.laText2, binding.laText3, binding.laText4,
                binding.laText5, binding.laText6, binding.laText7, binding.laText8,
                binding.laText9, binding.laText10
            )

            val verses = listOf(
                biaknala.la_text1, biaknala.la_text2, biaknala.la_text3, biaknala.la_text4,
                biaknala.la_text5, biaknala.la_text6, biaknala.la_text7, biaknala.la_text8,
                biaknala.la_text9, biaknala.la_text10
            )

            // Loop through and set visibility/text
            for (i in verses.indices) {
                val text = verses[i]
                if (text.isNullOrBlank()) {
                    verseLayouts[i].visibility = View.GONE
                } else {
                    verseLayouts[i].visibility = View.VISIBLE
                    verseTexts[i].text = text.replace("\\n", "\n")
                }
            }

            val chorusLayouts = listOf(
                binding.laChorus1, binding.laChorus2, binding.laChorus3, binding.laChorus4,
                binding.laChorus5, binding.laChorus6, binding.laChorus7, binding.laChorus8
            )

            val chorusTexts = listOf(
                binding.chorus1, binding.chorus2, binding.chorus3, binding.chorus4,
                binding.chorus5, binding.chorus6, binding.chorus7, binding.chorus8
            )

            val chorusData = listOf(
                biaknala.chorus1, biaknala.chorus2, biaknala.chorus3, biaknala.chorus4,
                biaknala.chorus5, biaknala.chorus6, biaknala.chorus7, biaknala.chorus8
            )

            for (i in chorusData.indices) {
                val text = chorusData[i]
                if (text.isNullOrEmpty()) {
                    chorusLayouts[i].visibility = View.GONE
                } else {
                    chorusLayouts[i].visibility = View.VISIBLE
                    chorusTexts[i].text = text.replace("\\n", "\n")
                }
            }

            //if (dbHelper.isPageBnlBookmarked(biaknala.la_number)) {
               // binding.bookmarked.visibility = View.VISIBLE
                //binding.bookmarked.setOnClickListener {
                   // val intent = Intent(binding.root.context, BookmarkActivity::class.java)
                   // intent.putExtra("TAB_INDEX", 0) // 0 = Biaknala tab
                   // binding.root.context.startActivity(intent)
               // }
           // } else {
               // binding.bookmarked.visibility = View.GONE

            //}

            val copyrightText = "\u00A9Labupi"  // or "© Labupi"
            binding.laCopyrightText.text = copyrightText
            binding.laCopyrightText.visibility = View.VISIBLE
        }

        private fun setupText(view: TextView, text: String?) {
            if (text.isNullOrEmpty()) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
                view.text = text.replace("\\n", "\n")
            }
        }

        private fun playNoteFromDbKey(dbKey: String?) {
            val context = binding.root.context
            val normalizedKey = dbKey?.trim()?.uppercase()
            val fileName = noteToFileMap[normalizedKey]

            fileName?.let {
                try {
                    // Release old player if still playing
                    mediaPlayer?.release()

                    val afd = context.assets.openFd("notes/$it")
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                        prepare()
                        start()
                        setOnCompletionListener { mp ->
                            mp.release()
                            mediaPlayer = null
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiaknalaViewHolder {
        val binding = ItemBnlPnlBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BiaknalaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BiaknalaViewHolder, position: Int) {
        holder.bind(BNL[position])
    }

    override fun getItemCount(): Int = BNL.size

    fun getItemAt(position: Int): LabuData {
        return BNL[position]
    }

    fun getCleanedTextForSharing(position: Int): String {
        val item = BNL[position]
        val builder = StringBuilder()

        // Get screen width in density-independent pixels (dp)
        val screenWidthDp = Resources.getSystem().displayMetrics.widthPixels / Resources.getSystem().displayMetrics.density

        // Define characters per line based on an average character width (adjust as needed)
        val charactersPerLine = (screenWidthDp / 7).toInt() // Assuming ~7dp average width per character

        // Make la_number + la_thulu uppercase + bold
        val titleLine = "**${item.la_number.uppercase()}  ${item.la_thulu.uppercase()}**"

        // Center-align Title Line
        val titlePadding = (charactersPerLine - titleLine.length) / 2
        val centeredTitleLine = " ".repeat(maxOf(0, titlePadding)) + titleLine
        builder.appendLine(centeredTitleLine)
        builder.appendLine()

        // Add key if present
        if (!item.la_key.isNullOrEmpty()) {
            builder.appendLine(item.la_key.replace("\\n", "\n").trim())
            builder.appendLine()
        }

        // Verses and choruses (indexed together)
        val verses = listOf(
            item.la_text1, item.la_text2, item.la_text3, item.la_text4,
            item.la_text5, item.la_text6, item.la_text7, item.la_text8,
            item.la_text9, item.la_text10
        )

        val choruses = listOf(
            item.chorus1, item.chorus2, item.chorus3, item.chorus4,
            item.chorus5, item.chorus6, item.chorus7, item.chorus8,
            null, null
        )

        for (i in verses.indices) {
            val verse = verses[i]
            val chorus = choruses[i]

            if (!verse.isNullOrBlank()) {
                // Verse with numbering
                builder.appendLine("${i + 1} ${verse.replace("\\n", "\n").trim()}")
                builder.appendLine() // blank line

                // Chorus (if exists for this verse)
                if (!chorus.isNullOrBlank()) {
                    builder.appendLine(chorus.replace("\\n", "\n").trim())
                    builder.appendLine() // blank line
                }
            }
        }
        builder.appendLine()
        builder.appendLine("©Labupi")
        // Return the cleaned and formatted text
        return builder.toString().trim()
    }

}
