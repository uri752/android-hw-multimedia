package ru.netology.hw_7_3_1_multimedia

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import okhttp3.*
import ru.netology.hw_7_3_1_multimedia.adapter.OnInteractionListener
import ru.netology.hw_7_3_1_multimedia.adapter.TrackAdapter
import ru.netology.hw_7_3_1_multimedia.databinding.ActivityMainBinding
import ru.netology.hw_7_3_1_multimedia.dto.Album
import ru.netology.hw_7_3_1_multimedia.dto.Track
import ru.netology.hw_7_3_1_multimedia.viewmodel.TrackViewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var listItem: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var binding: ActivityMainBinding
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val viewModel: TrackViewModel = TrackViewModel(mediaPlayer)

    private val interactionListener = object : OnInteractionListener {
        override fun onTap(track: Track) {
            viewModel.onPlayItem(track)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listItem = binding.listItem
        adapter = TrackAdapter(interactionListener)
        listItem.adapter = adapter
        requestData()
        flowData()
        listeners()
    }

    private fun flowData() {
        lifecycleScope.launchWhenCreated {
            viewModel.isPlaying.collectLatest {
                if (mediaPlayer.isPlaying) {
                    binding.playAlbum.setImageResource(R.drawable.ic_baseline_pause_circle_48dp)
                } else {
                    binding.playAlbum.setImageResource(R.drawable.ic_baseline_play_circle_filled_48dp)
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitList(it)
            }
        }

    }

    private fun listeners() {
        binding.playAlbum.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }
    }

    private fun requestData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/album.json")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d("MainActivity", "Error fetching JSON: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body?.string()
                val album = Gson().fromJson(jsonString, Album::class.java)
                val albumTracks = album.tracks
                viewModel.setAlbumTracks(albumTracks)
                runOnUiThread {
                    binding.albumName.text = album.title
                    binding.artistName.text = album.artist
                    binding.genreName.text = album.genre
                    binding.yearName.text = album.published
                }
            }

        })
    }
}