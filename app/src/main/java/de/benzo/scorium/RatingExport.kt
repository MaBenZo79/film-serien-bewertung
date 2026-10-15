package de.benzo.scorium

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.OutputStream

/** Exports a local rating card without external services or storage permissions. */
object RatingExport {
    data class Report(val title: String, val subtitle: String, val score: String, val rows: List<Pair<String, String>>)
    private fun draw(canvas: Canvas, report: Report, width: Int, height: Int) {
        canvas.drawColor(Color.rgb(13,17,27))
        val paint=Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface=android.graphics.Typeface.create("sans-serif",android.graphics.Typeface.BOLD)
        paint.color=Color.rgb(228,196,119)
        paint.textSize=42f
        canvas.drawText("SCORIUM",44f,80f,paint)
        paint.color=Color.WHITE
        paint.textSize=27f
        canvas.drawText(report.title.take(35),44f,140f,paint)
        paint.typeface=android.graphics.Typeface.create("sans-serif",android.graphics.Typeface.NORMAL)
        paint.textSize=18f
        paint.color=Color.LTGRAY
        canvas.drawText(report.subtitle,44f,175f,paint)
        paint.color=Color.rgb(228,196,119)
        paint.textSize=36f
        canvas.drawText(report.score,44f,235f,paint)
        paint.textSize=17f
        report.rows.forEachIndexed { index, row ->
            val y=295f+index*44f
            if(y<height-25) {
                paint.color=Color.WHITE
                canvas.drawText(row.first.take(37),44f,y,paint)
                paint.color=Color.rgb(228,196,119)
                canvas.drawText(row.second,width-90f,y,paint)
            }
        }
    }
    fun png(report: Report, out: OutputStream) {
        val image=Bitmap.createBitmap(900,1200,Bitmap.Config.ARGB_8888)
        try {
            draw(Canvas(image),report,900,1200)
            require(image.compress(Bitmap.CompressFormat.PNG,100,out)) { "PNG export failed" }
        } finally { image.recycle() }
    }
    fun pdf(report: Report, out: OutputStream) {
        val pdf=PdfDocument()
        try {
            val page=pdf.startPage(PdfDocument.PageInfo.Builder(900,1200,1).create())
            draw(page.canvas,report,900,1200)
            pdf.finishPage(page)
            pdf.writeTo(out)
        } finally { pdf.close() }
    }
}
