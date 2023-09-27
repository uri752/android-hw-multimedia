package ru.netology.hw_7_3_1_multimedia.viewmodel
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.netology.hw_7_3_1_multimedia.dto.Track

class TrackViewModel(
    private val mediaPlayer: MediaPlayer,

    ) : ViewModel() {

    val isPlaying
        get() = flow {
            while (true) {
                emit(mediaPlayer.isPlaying)
                delay(15)
            }
        }

    val data
        get() = flow {
            while (true) {
                emit(_data)
                delay(15)
            }
        }
    private var _data: List<Track> = listOf()
    private var currentTrack: Track? = null

    fun setAlbumTracks(value: List<Track>) {
        _data = value
    }

    fun onPlayItem(track: Track) {
        mediaPlayer.pause()
        _data = _data.map {
            it.copy(isPlaying = false)
        }

        if (currentTrack?.id != track.id) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/${track.file}")
            mediaPlayer.prepare()
            _data = _data.map {
                if (it.id == track.id) {
                    val minDur = mediaPlayer.duration / 60_000
                    val secDur = (mediaPlayer.duration / 1_000) - (minDur * 60)
                    val newTrack = it.copy(
                        isPlaying = true,
                        duration = "$minDur:${if (secDur < 10) "0" else ""}$secDur"
                    )
                    currentTrack = newTrack
                    newTrack
                } else {
                    it
                }
            }
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                val thisTrackInData = _data.find {
                    it.id == track.id
                }
                val thisTrackIndex = _data.indexOf(thisTrackInData)
                val lastIndex = _data.lastIndex
                if (lastIndex == thisTrackIndex) {
                    onPlayItem(_data[0])
                } else {
                    onPlayItem(_data[thisTrackIndex + 1])
                }
            }
        } else {
            _data = _data.map {
                if (it.id == track.id) {
                    val newTrack = it.copy(isPlaying = !track.isPlaying)
                    currentTrack = newTrack
                    newTrack
                } else {
                    it
                }
            }

            if (currentTrack?.isPlaying == true) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }


    }
}