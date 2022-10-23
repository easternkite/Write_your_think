package com.multimedia.writeyourthink.adapters


import androidx.recyclerview.widget.RecyclerView
import com.multimedia.writeyourthink.models.Diary
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.Util.formatTime
import com.multimedia.writeyourthink.databinding.ItemDiaryPreviewBinding

class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(val binding: ItemDiaryPreviewBinding) : RecyclerView.ViewHolder(binding.root)


    /**
     * DiffUtil은 리사이클러뷰의 성능을 한 층 더 개선할 수 있게 해주는 유틸리티 클래스다.
     * 기존의 데이터 리스트와 교체할 데이터 리스트를 비교하여 실질적으로 업데이트가 필요한 아이템을 추려낸다.
     * 백그라운드 스레드에서 작동되기 때문에 메인스레드에 영향을 안준다.
     */
    private val differCallback = object : DiffUtil.ItemCallback<Diary>() {
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * AsyncListDiffer는 DiffUtil을 더 단순하게 사용할 수 있게 해주는 클래스다.
     * 자체적으로 멀티 쓰레드에 대한 처리가 되어있기 때문에 개발자가 직접 동기화 처리를 할 필요가 없다.
     * ...getCurrentList: adapter에서 사용하는 item 리스트에 접근하고 싶을 때 사용한다.
     * ...submitList(List<T> value): 리스트 데이터를 교체할 때 사용한다.
     */
    val differ = AsyncListDiffer(this, differCallback)
    /**
     *  ViewHolder 객체가 생성되는 곳이다. ViewHolder객체를 return해주면 된다.
     *  화면을 채울 정도의 ViewHolder 객체를 생성하므로 대략 10회 정도의 호출만 발생한다.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        return DiaryViewHolder(ItemDiaryPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }
    /**
     * onBindViewHolder는 뷰홀더에 데이터를 결합해주는 함수이다.
     * 스크롤 하면서 맨 위의 뷰 홀더가 아래로 이동(재사용)한다면,
     * position을 사용하여 데이터를 새롭게 갱신하여준다.
     * 스크롤을 하여 새로운 데이터 결합이 필요할 때 마다 호출된다. (계속 스크롤을 한다면 무한정 호출된다...)
     * 그러나 뷰홀더의 재사용으로 인하여 실제 사용되는 뷰의 객체는 10개정도만 사용하는 꼴이다.
     */
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diary = differ.currentList[position]
        holder.binding.apply {
            setDiary(diary)
            Glide.with(root)
                .load(diary.profile)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(ivDiaryImage)
            tvPlace.text = if (!diary.where.isNullOrEmpty()) " • ${diary.where}" else ""
            tvContent.text = diary.contents
            tvDateAndTime.text = if (diary.diaryDate.isEmpty()) formatTime(diary.date) else formatTime(diary.diaryDate)
            diary.location.let {
                val location = it.split(" ")
                tvLocation.text = location[location.size - 1]
            }

            divider.isVisible = try {
                differ.currentList[position + 1]
                true
            } catch (e: java.lang.IndexOutOfBoundsException) {
                false
            }
            cvContainer.setOnClickListener {
                onItemClickListener?.let { it(cvContainer, diary) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((CardView, Diary) -> Unit)? = null

    fun setOnItemClickListener(listener: (CardView, Diary) -> Unit) {
        onItemClickListener = listener
    }
}
