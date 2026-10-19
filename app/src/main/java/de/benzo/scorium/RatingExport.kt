package de.benzo.scorium

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.OutputStream

/** The same locally rendered layout is used for PNG and PDF. */
object RatingExport {
 data class Report(val title:String,val subtitle:String,val score:String,val rows:List<Pair<String,String>>)
 private val white=Color.WHITE
 private val muted=Color.rgb(177,185,207)
 private fun paint(size:Float,color:Int,bold:Boolean=false)=Paint(Paint.ANTI_ALIAS_FLAG).apply {
  textSize=size;this.color=color;typeface=Typeface.create("sans-serif",if(bold) Typeface.BOLD else Typeface.NORMAL)
 }
 private fun ring(canvas:Canvas,x:Float,y:Float,r:Float,score:Int?,integer:Boolean=false) {
  val v=score?.coerceIn(0,100)
  val base=when {
   v==null -> Color.rgb(90,94,110)
   v>=90 -> Color.rgb(0,126,50)
   v>=80 -> Color.rgb(63,207,67)
   v>=70 -> Color.rgb(255,214,51)
   v>=60 -> Color.rgb(255,152,40)
   v>=50 -> Color.rgb(255,101,56)
   else -> Color.rgb(237,23,59)
  }
  val glow=when {
   v==null -> muted
   v>=90 -> Color.rgb(0,255,101)
   v>=80 -> Color.rgb(145,255,80)
   v>=70 -> Color.rgb(255,255,54)
   v>=60 -> Color.rgb(255,193,58)
   v>=50 -> Color.rgb(255,139,72)
   else -> Color.rgb(255,41,83)
  }
  val fill=paint(14f,base)
  canvas.drawCircle(x,y,r*0.76f,fill)
  val stroke=paint(14f,Color.argb(65,Color.red(glow),Color.green(glow),Color.blue(glow))).apply { style=Paint.Style.STROKE;strokeWidth=r*0.17f }
  canvas.drawCircle(x,y,r*0.9f,stroke)
  if(v!=null) {
   stroke.color=glow
   stroke.strokeCap=Paint.Cap.ROUND
   canvas.drawArc(RectF(x-r*0.9f,y-r*0.9f,x+r*0.9f,y+r*0.9f),-90f,v*3.6f,false,stroke)
  }
  val label=if(v==null) "–" else if(integer) "${v/10}" else "${v/10}.${v%10}"
  val text=paint(if(integer) r*0.85f else r*0.69f,white,true)
  text.textAlign=Paint.Align.CENTER
  canvas.drawText(label,x,y-(text.ascent()+text.descent())/2,text)
 }
 private fun lines(canvas:Canvas,text:String,x:Float,y:Float,maxWidth:Float,size:Float,color:Int,maxLines:Int=4):Float {
  val p=paint(size,color,true)
  var baseline=y
  var current=""
  var drawn=0
  for(word in text.split(" ")) {
   val candidate=if(current.isEmpty()) word else "$current $word"
   if(p.measureText(candidate)>maxWidth && current.isNotEmpty()) {
    canvas.drawText(current,x,baseline,p);baseline+=size*1.23f;drawn++
    if(drawn>=maxLines) return baseline
    current=word
   } else current=candidate
  }
  if(current.isNotEmpty()) {canvas.drawText(current,x,baseline,p);baseline+=size*1.23f}
  return baseline
 }
 private fun draw(canvas:Canvas,context:Context,report:Report) {
  val width=1000f
  canvas.drawColor(Color.rgb(13,17,27))
  val logo=runCatching { BitmapFactory.decodeResource(context.resources,R.drawable.scorium_header) }.getOrNull()
  if(logo!=null) {
   val ratio=logo.width.toFloat()/logo.height
   val h=100f;val w=kotlin.math.min(520f,h*ratio)
   canvas.drawBitmap(logo,null,RectF(42f,32f,42f+w,32f+h),Paint(Paint.ANTI_ALIAS_FLAG))
   logo.recycle()
  } else canvas.drawText("SCORIUM",44f,105f,paint(54f,Color.rgb(180,140,246),true))
  val year=ReleaseYears.label(report.title)
  val titleBottom=lines(canvas,report.title,44f,195f,900f,38f,white,3)
  canvas.drawText(listOfNotNull(report.subtitle.takeIf { it.isNotBlank() },year).joinToString("  ·  "),44f,titleBottom+6f,paint(23f,muted))
  val top=titleBottom+65f
  val poster=runCatching { BitmapFactory.decodeFile(posterFile(context,report.title).absolutePath) }.getOrNull()
  if(poster!=null) {
   val bounds=RectF(44f,top,465f,top+650f)
   canvas.drawBitmap(poster,null,bounds,Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
   poster.recycle()
  } else {
   canvas.drawRoundRect(RectF(44f,top,465f,top+650f),22f,22f,paint(20f,Color.rgb(34,41,63)))
   lines(canvas,"Kein Poster hinterlegt",72f,top+320f,340f,25f,muted)
  }
  val rowX=505f
  val count=report.rows.size.coerceAtLeast(1)
  val spacing=kotlin.math.min(70f,650f/count)
  report.rows.forEachIndexed { index,row ->
   val y=top+34f+index*spacing
   val value=row.second.substringBefore("/").trim().toIntOrNull()
   ring(canvas,934f,y+3f,27f,value?.times(10),true)
   lines(canvas,row.first,rowX,y,360f,19f,white,2)
   if(index<report.rows.lastIndex) canvas.drawLine(rowX,y+spacing*0.52f,965f,y+spacing*0.52f,paint(1f,Color.rgb(53,59,77)).apply { strokeWidth=1f })
  }
  val bottom=top+700f
  canvas.drawRoundRect(RectF(44f,bottom,956f,bottom+170f),22f,22f,paint(12f,Color.rgb(28,35,57)))
  canvas.drawText("GESAMTBEWERTUNG",78f,bottom+95f,paint(30f,white,true))
  val score=Regex("""(\d+)\.(\d+)""").find(report.score)?.let { it.groupValues[1].toInt()*10+it.groupValues[2].toInt() }
  ring(canvas,860f,bottom+84f,65f,score)
 }
 fun png(context:Context,report:Report,out:OutputStream) {
  val bitmap=Bitmap.createBitmap(1000,1400,Bitmap.Config.ARGB_8888)
  try { draw(Canvas(bitmap),context,report);require(bitmap.compress(Bitmap.CompressFormat.PNG,100,out)) }
  finally { bitmap.recycle() }
 }
 fun pdf(context:Context,report:Report,out:OutputStream) {
  val document=PdfDocument()
  try {
   val page=document.startPage(PdfDocument.PageInfo.Builder(1000,1400,1).create())
   draw(page.canvas,context,report)
   document.finishPage(page)
   document.writeTo(out)
  } finally { document.close() }
 }
}
