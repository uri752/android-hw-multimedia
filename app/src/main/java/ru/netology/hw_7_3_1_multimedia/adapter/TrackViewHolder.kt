package ru.netology.hw_7_3_1_multimedia.adapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.hw_7_3_1_multimedia.R
import ru.netology.hw_7_3_1_multimedia.databinding.ItemBinding
import ru.netology.hw_7_3_1_multimedia.dto.Track


class TrackViewHolder(
    private val binding: ItemBinding,
    private val interactionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            trackName.text = track.file
            playItem.setImageResource(
                if (track.isPlaying) {
                    R.drawable.ic_baseline_pause_circle_48dp
                } else {
                    R.drawable.ic_baseline_play_circle_filled_48dp
                }
            )

            playItem.setOnClickListener {
                interactionListener.onTap(track)
            }

            trackTime.text = track.duration ?: ""
        }
    }


}