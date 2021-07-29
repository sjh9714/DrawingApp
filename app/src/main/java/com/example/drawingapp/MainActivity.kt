package com.example.drawingapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.drawingapp.databinding.ActivityMainBinding
import com.example.drawingapp.databinding.DialogBrushSizeBinding
import java.util.jar.Manifest

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

        // ibGallery 버튼 클릭 시
        binding.ibGallery.setOnClickListener {
            if (isReadStorageAllowed()) {
                val pickPhotoIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(pickPhotoIntent, GALLERY)
            } else {
                requestStoragePermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                try {
                    if (data!!.data != null) {

                        // Here if the user selects the image from local storage make the image view visible.
                        // By Default we will make it VISIBILITY as GONE.
                        binding.ivBackground.visibility = View.VISIBLE

                        // Set the selected image to the backgroung view.
                        binding.ivBackground.setImageURI(data.data)
                    } else {
                        // If the selected image is not valid. Or not selected.
                        Toast.makeText(
                            this@MainActivity,
                            "Error in parsing the image or its corrupted.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 브러쉬 사이즈 선택 메소드
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
            // 다이얼로그 끄기
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

    // 페인트 클릭 메소드
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

    // 권한 요청 확인
    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).toString()
            )
        ) {
            Toast.makeText(this, "배경을 추가하기 위한 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), STORAGE_PERMISSION_CODE // 저장권한 코드 전달
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "부여된 권한으로 저장공간을 읽을 수 있습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "권한을 받지 못했습니다..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    // 상수 저장
    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
}