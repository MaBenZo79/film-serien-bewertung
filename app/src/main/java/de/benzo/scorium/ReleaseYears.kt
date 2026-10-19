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
 private val dcFilmYears=mapOf(
  "Superman (2025)" to "2025","Supergirl" to "2026","Clayface" to "2026",
  "Man of Steel" to "2013","Batman v Superman: Dawn of Justice" to "2016",
  "Suicide Squad" to "2016","Wonder Woman" to "2017","Justice League" to "2017",
  "Aquaman" to "2018","Shazam!" to "2019","Birds of Prey" to "2020",
  "Wonder Woman 1984" to "2020","Zack Snyder's Justice League" to "2021",
  "The Suicide Squad" to "2021","Black Adam" to "2022","Shazam! Fury of the Gods" to "2023",
  "The Flash (2023)" to "2023","Blue Beetle" to "2023","Aquaman and the Lost Kingdom" to "2023",
  "Superman (1978)" to "1978","Superman II" to "1980","Superman III" to "1983",
  "Superman IV: The Quest for Peace" to "1987","Superman Returns" to "2006",
  "Batman" to "1989","Batman Returns" to "1992","Batman Forever" to "1995",
  "Batman & Robin" to "1997","Batman Begins" to "2005","The Dark Knight" to "2008",
  "The Dark Knight Rises" to "2012","Joker" to "2019","Joker: Folie à Deux" to "2024",
  "The Batman" to "2022","Constantine" to "2005","Watchmen" to "2009",
  "V for Vendetta" to "2005","Green Lantern" to "2011","The LEGO Batman Movie" to "2017"
 )
 private val starWarsFilmYears=mapOf(
  "Star Wars: Episode I – Die dunkle Bedrohung" to "1999",
  "Star Wars: Episode II – Angriff der Klonkrieger" to "2002",
  "Star Wars: Episode III – Die Rache der Sith" to "2005",
  "Star Wars: Episode IV – Eine neue Hoffnung" to "1977",
  "Star Wars: Episode V – Das Imperium schlägt zurück" to "1980",
  "Star Wars: Episode VI – Die Rückkehr der Jedi-Ritter" to "1983",
  "Star Wars: Episode VII – Das Erwachen der Macht" to "2015",
  "Star Wars: Episode VIII – Die letzten Jedi" to "2017",
  "Star Wars: Episode IX – Der Aufstieg Skywalkers" to "2019",
  "Rogue One: A Star Wars Story" to "2016","Solo: A Star Wars Story" to "2018",
  "Star Wars: The Clone Wars (Film)" to "2008",
  "The Mandalorian & Grogu" to "2026","Star Wars: Starfighter" to "geplant 2027"
 )
 private val otherSeriesYears=mapOf(
  "Creature Commandos" to "2024","Peacemaker" to "2022–2025","Lanterns" to "2026",
  "Smallville" to "2001–2011","Gotham" to "2014–2019","The Penguin" to "2024",
  "Watchmen" to "2019","Titans" to "2018–2023","Doom Patrol" to "2019–2023",
  "The Flash" to "2014–2023","Arrow" to "2012–2020","Superman & Lois" to "2021–2024",
  "The Sandman" to "2022–2025","Supergirl (2015)" to "2015–2021","Stargirl" to "2020–2022",
  "Black Lightning" to "2018–2021","DC’s Legends of Tomorrow" to "2016–2022",
  "Batwoman" to "2019–2022","Krypton" to "2018–2019","Pennyworth" to "2019–2022",
  "Swamp Thing" to "2019","Gotham Knights" to "2023",
  "Star Wars: The Clone Wars" to "2008–2020","Star Wars: Clone Wars (2003)" to "2003–2005",
  "Star Wars Rebels" to "2014–2018","Star Wars Resistance" to "2018–2020",
  "The Mandalorian" to "2019–2023","Das Buch von Boba Fett" to "2021–2022",
  "Obi-Wan Kenobi" to "2022","Andor" to "2022–2025","Ahsoka" to "2023",
  "The Acolyte" to "2024","Skeleton Crew" to "2024–2025",
  "The Bad Batch" to "2021–2024","Star Wars: Geschichten der Jedi" to "2022",
  "Star Wars: Geschichten des Imperiums" to "2024",
  "Star Wars: Geschichten der Unterwelt" to "2025",
  "Star Wars: Maul – Shadow Lord" to "2026"
 )
 private val otherSeasonYears=mapOf(
  "Peacemaker" to listOf("2022","2025"),
  "Smallville" to listOf("2001–2002","2002–2003","2003–2004","2004–2005","2005–2006","2006–2007","2007–2008","2008–2009","2009–2010","2010–2011"),
  "Gotham" to listOf("2014–2015","2015–2016","2016–2017","2017–2018","2019"),
  "Titans" to listOf("2018","2019","2021","2022–2023"),
  "Doom Patrol" to listOf("2019","2020","2021","2022–2023"),
  "The Flash" to listOf("2014–2015","2015–2016","2016–2017","2017–2018","2018–2019","2019–2020","2021","2021–2022","2023"),
  "Arrow" to listOf("2012–2013","2013–2014","2014–2015","2015–2016","2016–2017","2017–2018","2018–2019","2019–2020"),
  "Superman & Lois" to listOf("2021","2022","2023","2024"),
  "The Sandman" to listOf("2022","2025"),
  "Supergirl (2015)" to listOf("2015–2016","2016–2017","2017–2018","2018–2019","2019–2020","2021"),
  "Stargirl" to listOf("2020","2021","2022"),
  "Black Lightning" to listOf("2018","2018–2019","2019–2020","2021"),
  "DC’s Legends of Tomorrow" to listOf("2016","2016–2017","2017–2018","2018–2019","2020","2021","2021–2022"),
  "Batwoman" to listOf("2019–2020","2021","2021–2022"),
  "Krypton" to listOf("2018","2019"),"Pennyworth" to listOf("2019","2020–2021","2022"),
  "Star Wars: Clone Wars (2003)" to listOf("2003","2004","2005"),
  "Star Wars: The Clone Wars" to listOf("2008–2009","2009–2010","2010–2011","2011–2012","2012–2013","2014","2020"),
  "Star Wars Rebels" to listOf("2014–2015","2015–2016","2016–2017","2017–2018"),
  "Star Wars Resistance" to listOf("2018–2019","2019–2020"),
  "The Mandalorian" to listOf("2019","2020","2023"),
  "Andor" to listOf("2022","2025"),"The Bad Batch" to listOf("2021","2023","2024")
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
 fun seasonLabel(title:String,season:Int):String? = (seasonYears[title] ?: otherSeasonYears[title])?.getOrNull(season-1)
 fun label(title:String):String? {
  val index=McuCatalog.films.indexOf(title)
  val year=if(index>=0) filmYears.getOrNull(index)?.toString() else seriesYears[title] ?: dcFilmYears[title] ?: starWarsFilmYears[title] ?: otherSeriesYears[title]
  return year?.let { "($it)" }
 }
}
