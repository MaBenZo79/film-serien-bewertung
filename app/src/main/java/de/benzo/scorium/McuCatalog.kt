package de.benzo.scorium

/** Offline catalog. Episode counts are per season; unreleased episodes remain locked. */
data class ShowInfo(val title:String,val seasons:List<Int>,val available:List<Int> = seasons)
object McuCatalog {
 val films = listOf(
 "Iron Man","Der unglaubliche Hulk","Iron Man 2","Thor","Captain America: The First Avenger","Marvel's The Avengers",
 "Iron Man 3","Thor: The Dark Kingdom","Captain America: The Winter Soldier","Guardians of the Galaxy",
 "Avengers: Age of Ultron","Ant-Man","Captain America: Civil War","Doctor Strange","Guardians of the Galaxy Vol. 2",
 "Spider-Man: Homecoming","Thor: Ragnarok","Black Panther","Avengers: Infinity War","Ant-Man and the Wasp",
 "Captain Marvel","Avengers: Endgame","Spider-Man: Far From Home","Black Widow","Shang-Chi and the Legend of the Ten Rings",
 "Eternals","Spider-Man: No Way Home","Doctor Strange in the Multiverse of Madness","Thor: Love and Thunder",
 "Black Panther: Wakanda Forever","Ant-Man and the Wasp: Quantumania","Guardians of the Galaxy Vol. 3",
 "The Marvels","Deadpool & Wolverine","Captain America: Brave New World","Thunderbolts*",
 "The Fantastic Four: First Steps","Spider-Man: Brand New Day"
 )
 val shows = listOf(
  ShowInfo("WandaVision",listOf(9)),
  ShowInfo("The Falcon and the Winter Soldier",listOf(6)),
  ShowInfo("Loki",listOf(6,6)),
  ShowInfo("What If...?",listOf(9,9,8)),
  ShowInfo("Hawkeye",listOf(6)),
  ShowInfo("Moon Knight",listOf(6)),
  ShowInfo("Ms. Marvel",listOf(6)),
  ShowInfo("She-Hulk",listOf(9)),
  ShowInfo("Secret Invasion",listOf(6)),
  ShowInfo("Echo",listOf(5)),
  ShowInfo("Agatha All Along",listOf(9)),
  ShowInfo("Daredevil: Born Again",listOf(9,8)),
  ShowInfo("Ironheart",listOf(6)),
  ShowInfo("Wonder Man",listOf(8)),
  ShowInfo("VisionQuest",listOf(8),listOf(2)),
  ShowInfo("I Am Groot",listOf(5,5)),
  ShowInfo("Your Friendly Neighborhood Spider-Man",listOf(10)),
  ShowInfo("Marvel Zombies",listOf(4)),
  ShowInfo("Eyes of Wakanda",listOf(4))
 )
 fun find(title:String)=shows.firstOrNull { it.title==title }
}
