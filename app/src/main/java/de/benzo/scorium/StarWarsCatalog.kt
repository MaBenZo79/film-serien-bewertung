package de.benzo.scorium

/** Offline STAR WARS collection. Each film and show has a unique rating/poster key. */
object StarWarsCatalog {
 val films = listOf(
  "Star Wars: Episode I – Die dunkle Bedrohung",
  "Star Wars: Episode II – Angriff der Klonkrieger",
  "Star Wars: Episode III – Die Rache der Sith",
  "Star Wars: Episode IV – Eine neue Hoffnung",
  "Star Wars: Episode V – Das Imperium schlägt zurück",
  "Star Wars: Episode VI – Die Rückkehr der Jedi-Ritter",
  "Star Wars: Episode VII – Das Erwachen der Macht",
  "Star Wars: Episode VIII – Die letzten Jedi",
  "Star Wars: Episode IX – Der Aufstieg Skywalkers",
  "Rogue One: A Star Wars Story",
  "Solo: A Star Wars Story",
  "Star Wars: The Clone Wars (Film)",
  "The Mandalorian & Grogu"
 )
 val shows = listOf(
  ShowInfo("Star Wars: The Clone Wars",listOf(22,22,22,22,20,13,12)),
  ShowInfo("Star Wars: Clone Wars (2003)",listOf(10,10,5)),
  ShowInfo("Star Wars Rebels",listOf(15,22,22,16)),
  ShowInfo("Star Wars Resistance",listOf(21,19)),
  ShowInfo("The Mandalorian",listOf(8,8,8)),
  ShowInfo("Das Buch von Boba Fett",listOf(7)),
  ShowInfo("Obi-Wan Kenobi",listOf(6)),
  ShowInfo("Andor",listOf(12,12)),
  ShowInfo("Ahsoka",listOf(8)),
  ShowInfo("The Acolyte",listOf(8)),
  ShowInfo("Skeleton Crew",listOf(8)),
  ShowInfo("The Bad Batch",listOf(16,16,15)),
  ShowInfo("Star Wars: Geschichten der Jedi",listOf(6)),
  ShowInfo("Star Wars: Geschichten des Imperiums",listOf(6)),
  ShowInfo("Star Wars: Geschichten der Unterwelt",listOf(6))
 )
 val series=shows.map { it.title }
 fun find(title:String)=shows.firstOrNull { it.title==title }
 fun inSection(title:String,section:String):Boolean = when(section) {
  "Original Trilogie" -> title in films.slice(3..5)
  "Prequel Trilogie" -> title in films.slice(0..2)
  "Sequel Trilogie" -> title in films.slice(6..8)
  "Zusatz Filme" -> title in films.drop(9)
  else -> title in films || title in series
 }
 fun releaseRank(title:String)=(films+series).indexOf(title).let { if(it<0) Int.MAX_VALUE else it }
}
