package de.benzo.scorium

/** Local, offline release-year labels. Unknown custom titles deliberately show no invented year. */
object ReleaseYears {
 private val filmYears = listOf(
  2008,2008,2010,2011,2011,2012,
  2013,2013,2014,2014,2015,2015,
  2016,2016,2017,2017,2017,2018,2018,2018,
  2019,2019,2019,2021,2021,2021,2021,2022,2022,
  2022,2023,2023,2023,2024,2025,2025,2025,2026
 )
 private val seriesYears = mapOf(
  "WandaVision" to "2021",
  "The Falcon and the Winter Soldier" to "2021",
  "Loki" to "2021–2023",
  "What If...?" to "2021–2024",
  "Hawkeye" to "2021",
  "Moon Knight" to "2022",
  "Ms. Marvel" to "2022",
  "She-Hulk" to "2022",
  "Secret Invasion" to "2023",
  "Echo" to "2024",
  "Agatha All Along" to "2024",
  "Daredevil: Born Again" to "2025–",
  "Ironheart" to "2025",
  "Wonder Man" to "2026",
  "VisionQuest" to "2026",
  "I Am Groot" to "2022–2023",
  "Your Friendly Neighborhood Spider-Man" to "2025–",
  "Marvel Zombies" to "2025",
  "Eyes of Wakanda" to "2025",
  "MARVEL's Agents of Shield" to "2013–2020",
  "MARVEL's Daredevil" to "2015–2018",
  "MARVEL's Jessica Jones" to "2015–2019",
  "MARVEL's Luke Cage" to "2016–2018",
  "MARVEL's Iron Fist" to "2017–2018",
  "MARVEL's DEFENDERS" to "2017",
  "MARVEL's Punisher" to "2017–2019",
  "X-MEN 97" to "2024–"
 )
 private val seasonYears=mapOf(
  "MARVEL's Agents of Shield" to listOf("2013–2014","2014–2015","2015–2016","2016–2017","2017–2018","2019","2020"),
  "MARVEL's Daredevil" to listOf("2015","2016","2018"),
  "MARVEL's Jessica Jones" to listOf("2015","2018","2019"),
  "MARVEL's Luke Cage" to listOf("2016","2018"),
  "MARVEL's Iron Fist" to listOf("2017","2018"),
  "MARVEL's Punisher" to listOf("2017","2019"),
  "Loki" to listOf("2021","2023"),
  "What If...?" to listOf("2021","2023","2024"),
  "I Am Groot" to listOf("2022","2023"),
  "Daredevil: Born Again" to listOf("2025","2026"),
  "X-MEN 97" to listOf("2024","2026"),
  "She-Hulk" to listOf("2022"),
  "WandaVision" to listOf("2021"),
  "VisionQuest" to listOf("2026")
 )
 fun seasonLabel(title:String,season:Int):String? = seasonYears[title]?.getOrNull(season-1)
 fun label(title:String):String? {
  val index=McuCatalog.films.indexOf(title)
  val year=if(index>=0) filmYears.getOrNull(index)?.toString() else seriesYears[title]
  return year?.let { "($it)" }
 }
}
