package com.example.drawingapp

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.drawingapp.databinding.ActivityMainBinding
import com.example.drawingapp.databinding.DialogBrushSizeBinding

class MainActivity : AppCompatActivity() {

    // 익스텐션 대신 뷰바인딩 사용
    private lateinit var binding: ActivityMainBinding

    // 페인트 색상 변수
    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 액티비티 뷰바인딩 기본적 양식
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 브러쉬 사이즈 10
        binding.drawingView.setSizeForBrush(10.toFloat())

        // llPaintColors의 2번째 색상인 검정색 사용
        mImageButtonCurrentPaint = binding.llPaintColors[1] as ImageButton
        // 페인트 색상 클릭 시 테두리 변경
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        // ibBrush 버튼 클릭 시
        binding.ibBrush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
    }

    // 브러쉬 사이즈 선택 이벤트
    private fun showBrushSizeChooserDialog() {
        // 뷰바인딩, 기본 다이얼로그 설정
        var bindingBrush = DialogBrushSizeBinding.inflate(layoutInflater)
        val brushDialog = Dialog(this)
        brushDialog.setContentView(bindingBrush.root)
        brushDialog.setTitle("Brush size: ")

        // 스몰 버튼 클릭
        val smallBtn = bindingBrush.ibSmallBrush
        smallBtn.setOnClickListener {
            binding.drawingView.setSizeForBrush(10.toFloat())
            // 다이얼로그 끄기기
           brushDialog.dismiss()
        }

        // 미디엄 버튼 클릭
        val mediumBtn = bindingBrush.ibMediumBrush
        mediumBtn.setOnClickListener {
            binding.drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        // 라지 버튼 클릭
        val largeBtn = bindingBrush.ibLargeBrush
        largeBtn.setOnClickListener {
            binding.drawingView.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        // 다이얼로그 실행
        brushDialog.show()
    }

    // 페인트 클릭 이벤트
    fun paintClicked(view: View) {
        // 선택한 색상과 이전의 색상이 같지 않을 때
        if (view !== mImageButtonCurrentPaint) {
            // 이미지버튼을 view로 받아옴
            val imageButton = view as ImageButton
            // xml에 입력해둔 태그를 받아옴
            val colorTag = imageButton.tag.toString()
            // 태그로 색상 변경
            binding.drawingView.setColor(colorTag)
            // 바뀐 색상의 테두리 변경
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
            // 이전 색상의 테두리 변경
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            // 색상값을 바뀐 view로 변경
            mImageButtonCurrentPaint = view
        }
    }
}