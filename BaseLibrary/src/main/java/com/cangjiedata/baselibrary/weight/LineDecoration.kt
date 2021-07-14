package com.cangjiedata.baselibrary.weight

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author wangjun
 * @version 1.0
 * @date 2016/8/25
 */
class LineDecoration : RecyclerView.ItemDecoration {
    private var space: Int
    var color = -1
    private var mDivider: Drawable? = null
    private var mPaint: Paint? = null
    private var type = 0

    constructor(space: Int) {
        this.space = space
    }

    constructor(space: Int, color: Int) {
        this.space = space
        this.color = color
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.isAntiAlias = true
        mPaint!!.color = color
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.strokeWidth = (space * 2).toFloat()
    }

    constructor(space: Int, color: Int, type: Int) {
        this.space = space
        this.color = color
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = color
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.strokeWidth = (space * 2).toFloat()
        this.type = type
    }

    constructor(space: Int, mDivider: Drawable?) {
        this.space = space
        this.mDivider = mDivider
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager != null) {
            if (parent.layoutManager is LinearLayoutManager && parent.layoutManager !is GridLayoutManager) {
                if ((parent.layoutManager as LinearLayoutManager).orientation === LinearLayoutManager.HORIZONTAL) {
                    outRect[space, 0, space] = 0
                } else {
                    outRect[0, space, 0] = space
                }
            } else {
                outRect[space, space, space] = space
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (parent.layoutManager != null) {
            if (parent.layoutManager is LinearLayoutManager && parent.layoutManager !is GridLayoutManager) {
                if ((parent.layoutManager as LinearLayoutManager).orientation === LinearLayoutManager.HORIZONTAL) {
                    drawHorizontal(c, parent)
                } else {
                    drawVertical(c, parent)
                }
            } else {
                if (type == 0) {
                    drawGridView(c, parent)
                } else {
                    drawGridView1(c, parent)
                }
            }
        }
    }

    //绘制纵向 item 分割线
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top: Int = parent.paddingTop
        val bottom: Int = parent.measuredHeight - parent.paddingBottom
        val childSize: Int = parent.childCount
        for (i in 0 until childSize) {
            val child: View = parent.getChildAt(i)
            val layoutParams: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left: Int = child.right + layoutParams.rightMargin
            val right = left + space
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
            }
        }
    }

    //绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left: Int = parent.paddingLeft
        val right: Int = parent.measuredWidth - parent.paddingRight
        val childSize: Int = parent.childCount
        for (i in 0 until childSize) {
            val child: View = parent.getChildAt(i)
            val layoutParams: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + layoutParams.bottomMargin
            val bottom = top + space
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
            }
        }
    }

    //绘制grideview item 分割线 不是填充满的
    private fun drawGridView(canvas: Canvas, parent: RecyclerView) {
        val linearLayoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        val childSize: Int = parent.childCount
        var other: Int = parent.childCount / linearLayoutManager.spanCount - 1
        if (other < 1) {
            other = 1
        }
        other *= linearLayoutManager.spanCount
        if (parent.childCount < linearLayoutManager.spanCount) {
            other = parent.childCount
        }
        var top: Int
        var bottom: Int
        var left: Int
        var right: Int
        var spancount: Int = linearLayoutManager.spanCount - 1
        for (i in 0 until childSize) {
            val child: View = parent.getChildAt(i)
            val layoutParams: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            if (i < other) {
                top = child.bottom + layoutParams.bottomMargin
                bottom = top + space
                left = (layoutParams.leftMargin + space) * (i + 1)
                right = child.measuredWidth * (i + 1) + left + space * i
                if (mDivider != null) {
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)
                }
                if (mPaint != null) {
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
                }
            }
            if (i != spancount) {
                top = (layoutParams.topMargin + space) * (i / linearLayoutManager.spanCount + 1)
                bottom = (child.measuredHeight + space) * (i / linearLayoutManager.spanCount + 1) + space
                left = child.right + layoutParams.rightMargin
                right = left + space
                if (mDivider != null) {
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)
                }
                if (mPaint != null) {
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
                }
            } else {
                spancount += 4
            }
        }
    }

    /** */
    private fun drawGridView1(canvas: Canvas, parent: RecyclerView) {
        val linearLayoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        val childSize: Int = parent.childCount
        var top: Int
        var bottom: Int
        var left: Int
        var right: Int
        val spanCount: Int = linearLayoutManager.spanCount
        for (i in 0 until childSize) {
            val child: View = parent.getChildAt(i)
            //画横线
            val layoutParams: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            top = child.bottom + layoutParams.bottomMargin
            bottom = top + space
            left = layoutParams.leftMargin + child.paddingLeft + space
            right = child.measuredWidth * (i + 1) + left + space * i
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
            }
            //画竖线
            top = (layoutParams.topMargin + space) * (i / linearLayoutManager.spanCount + 1)
            bottom = (child.measuredHeight + space) * (i / linearLayoutManager.spanCount + 1) + space
            left = child.right + layoutParams.rightMargin
            right = left + space
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
            }

            //画上缺失的线框
            if (i < spanCount) {
                top = child.top + layoutParams.topMargin
                bottom = top + space
                left = (layoutParams.leftMargin + space) * (i + 1)
                right = child.measuredWidth * (i + 1) + left + space * i
                if (mDivider != null) {
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)
                }
                if (mPaint != null) {
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
                }
            }
            if (i % spanCount == 0) {
                top = (layoutParams.topMargin + space) * (i / linearLayoutManager.spanCount + 1)
                bottom = (child.measuredHeight + space) * (i / linearLayoutManager.spanCount + 1) + space
                left = child.left + layoutParams.leftMargin
                right = left + space
                if (mDivider != null) {
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)
                }
                if (mPaint != null) {
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
                }
            }
        }
    }
}