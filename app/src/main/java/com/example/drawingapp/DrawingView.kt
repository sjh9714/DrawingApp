package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private var mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    // 객체 생성 시 호출되어 실행
    init {
        setUpdrawing()
    }

    fun onClickUndo() {
        if (mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate()
        }
    }

    private fun setUpdrawing() {
        mDrawPaint = Paint() // 페인트 객체 생성
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color // Color.BLACK 설정
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND // 끝이 둥근 모양
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat() // 선 굵기 20
    }

    // 화면의 사이즈가 변하거나 초기 액티비티가 생성될 때 호출되는 메소드
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888) // 비트맵 생성
        canvas = Canvas(mCanvasBitmap!!) // 캔버스 생성
    }

    // 화면을 그려주는 메소드
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // mCanvasBitmap을 불러와 (0, 0) 위치에 그림
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        // 이전의 Path들이 사라지지 않게함
        for (path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness // 브러시 두께만큼 Path를 그림
            mDrawPaint!!.color = mDrawPath!!.color // 지정된 색으로 Path를 그림
            canvas.drawPath(mDrawPath!!, mDrawPaint!!) // 지정된 Path를 그림
        }
    }

    // 화면 터치 이벤트
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 화면 터치 x,y 값
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            // 화면 터치
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color // 색상
                mDrawPath!!.brushThickness = mBrushSize // 굵기

                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX!!, touchY!!) // 위치만 이동
                    }
                }
            }
            // 화면 드래그
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX!!, touchY!!) // 선을 그림
                    }
                }
            }
            // 화면 터치 해제
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!) // Path 저장
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate() // 화면 다시 그리기

        return true
    }

    // 브러쉬 크기 변경
    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    // 색상 변경
    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {

    }
}