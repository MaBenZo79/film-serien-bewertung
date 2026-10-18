package de.benzo.scorium

/** Curated offline DC catalog. Entries are shared across the three collection filters. */
object DcCatalog {
 val dcuFilms = listOf("Superman","Supergirl","Clayface")
 val dcuShows = listOf(ShowInfo("Creature Commandos",listOf(7)),ShowInfo("Peacemaker",listOf(8,8)),ShowInfo("Lanterns",listOf(8)))
 val dceuFilms = listOf(
  "Man of Steel","Batman v Superman: Dawn of Justice","Suicide Squad","Wonder Woman",
  "Justice League","Aquaman","Shazam!","Birds of Prey","Wonder Woman 1984",
  "Zack Snyder's Justice League","The Suicide Squad","Black Adam","Shazam! Fury of the Gods",
  "The Flash","Blue Beetle","Aquaman and the Lost Kingdom"
 )
 val dceuShows = listOf(ShowInfo("Peacemaker",listOf(8,8)))
 val otherFilms = listOf(
  "Superman","Superman II","Superman III","Superman IV: The Quest for Peace","Superman Returns",
  "Batman","Batman Returns","Batman Forever","Batman & Robin","Batman Begins",
  "The Dark Knight","The Dark Knight Rises","Joker","Joker: Folie à Deux",
  "The Batman","Constantine","Watchmen","V for Vendetta","Green Lantern"
 )
 val otherShows = listOf(
  ShowInfo("Smallville",listOf(21,23,22,22,22,22,20,22,21,22)),
  ShowInfo("Gotham",listOf(22,22,22,22,12)),
  ShowInfo("The Penguin",listOf(8)),
  ShowInfo("Watchmen",listOf(9)),
  ShowInfo("Titans",listOf(11,13,13,12)),
  ShowInfo("Doom Patrol",listOf(15,9,10,12)),
  ShowInfo("The Flash",listOf(23,23,23,23,22,19,18,20,13)),
  ShowInfo("Arrow",listOf(23,23,23,23,23,23,22,10)),
  ShowInfo("Superman & Lois",listOf(15,15,13,10)),
  ShowInfo("The Sandman",listOf(11,12))
 )
 val films = (dceuFilms + dcuFilms + otherFilms).distinct()
 val shows = (dcuShows + dceuShows + otherShows).distinctBy { it.title }
 val series = shows.map { it.title }
 fun find(title:String) = shows.firstOrNull { it.title==title }
 fun inSection(title:String,section:String):Boolean = when(section) {
  "DCU" -> title in dcuFilms || dcuShows.any { it.title==title }
  "DCEU" -> title in dceuFilms || dceuShows.any { it.title==title }
  else -> title in films || title in series
 }
 fun releaseRank(title:String):Int = (dceuFilms + dcuFilms + otherFilms + series).distinct().indexOf(title).let { if(it<0) Int.MAX_VALUE else it }
}
