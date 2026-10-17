package de.benzo.scorium

object EpisodeTitles {
 private val known = mapOf(
  ("MARVEL's Daredevil" to 1) to listOf(
   "Into the Ring","Cut Man","Rabbit in a Snowstorm","In the Blood","World on Fire",
   "Condemned","Stick","Shadows in the Glass","Speak of the Devil","Nelson v. Murdock",
   "The Path of the Righteous","The Ones We Leave Behind","Daredevil"
  ),
  ("MARVEL's Agents of Shield" to 1) to listOf(
   "Pilot","0-8-4","The Asset","Eye Spy","Girl in the Flower Dress","FZZT",
   "The Hub","The Well","Repairs","The Bridge","The Magical Place","Seeds",
   "T.R.A.C.K.S.","T.A.H.I.T.I.","Yes Men","End of the Beginning","Turn, Turn, Turn",
   "Providence","The Only Light in the Darkness","Nothing Personal","Ragtag","Beginning of the End"
  )
 )
 fun title(series: String, season: Int, episode: Int): String? =
  known[series to season]?.getOrNull(episode - 1)
}
