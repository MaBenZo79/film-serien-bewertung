package de.benzo.scorium

object EpisodeTitles {
 private val known = mapOf(
  ("MARVEL's Daredevil" to 1) to listOf(
   "Into the Ring","Cut Man","Rabbit in a Snowstorm","In the Blood","World on Fire",
   "Condemned","Stick","Shadows in the Glass","Speak of the Devil","Nelson v. Murdock",
   "The Path of the Righteous","The Ones We Leave Behind","Daredevil"
  ),
  ("MARVEL's Daredevil" to 2) to listOf(
   "Bang","Dogs to a Gunfight","New York's Finest","Penny and Dime","Kinbaku",
   "Regrets Only","Semper Fidelis","Guilty as Sin","Seven Minutes in Heaven",
   "The Man in the Box",".380","The Dark at the End of the Tunnel","A Cold Day in Hell's Kitchen"
  ),
  ("MARVEL's Daredevil" to 3) to listOf(
   "Resurrection","Please","No Good Deed","Blindsided","The Perfect Game",
   "The Devil You Know","Aftermath","Upstairs/Downstairs","Revelations",
   "Karen","Reunion","One Last Shot","A New Napkin"
  ),
  ("MARVEL's Jessica Jones" to 1) to listOf(
   "AKA Ladies Night","AKA Crush Syndrome","AKA It's Called Whiskey","AKA 99 Friends",
   "AKA The Sandwich Saved Me","AKA You're a Winner!","AKA Top Shelf Perverts",
   "AKA WWJD?","AKA Sin Bin","AKA 1,000 Cuts","AKA I've Got the Blues",
   "AKA Take a Bloody Number","AKA Smile"
  ),
  ("MARVEL's Luke Cage" to 1) to listOf(
   "Moment of Truth","Code of the Streets","Who's Gonna Take the Weight?",
   "Step in the Arena","Just to Get a Rep","Suckas Need Bodyguards",
   "Manifest","Blowin' Up the Spot","DWYCK","Take It Personal",
   "Now You're Mine","Soliloquy of Chaos","You Know My Steez"
  ),
  ("MARVEL's DEFENDERS" to 1) to listOf(
   "The H Word","Mean Right Hook","Worst Behavior","Royal Dragon",
   "Take Shelter","Ashes, Ashes","Fish in the Jailhouse","The Defenders"
  ),
  ("MARVEL's Jessica Jones" to 2) to listOf(
   "AKA Start at the Beginning","AKA Freak Accident","AKA Sole Survivor",
   "AKA God Help the Hobo","AKA The Octopus","AKA Facetime","AKA I Want Your Cray Cray",
   "AKA Ain't We Got Fun","AKA Shark in the Bathtub, Monster in the Bed",
   "AKA Pork Chop","AKA Three Lives and Counting","AKA Pray for My Patsy","AKA Playland"
  ),
  ("MARVEL's Jessica Jones" to 3) to listOf(
   "AKA The Perfect Burger","AKA You're Welcome","AKA I Have No Spleen",
   "AKA Customer Service Is Standing By","AKA I Wish","AKA Sorry Face",
   "AKA The Double Half-Wappinger","AKA Camera Friendly","AKA I Did Something Today",
   "AKA Hero Pants","AKA Hellcat","AKA A Lotta Worms","AKA Everything"
  ),
  ("MARVEL's Luke Cage" to 2) to listOf(
   "Soul Brother #1","Straighten It Out","Wig Out","I Get Physical",
   "All Souled Out","The Basement","On and On","If It Ain't Rough, It Ain't Right",
   "For Pete's Sake","The Main Ingredient","The Creator","Can't Front on Me","They Reminisce Over You"
  ),
  ("MARVEL's Iron Fist" to 1) to listOf(
   "Snow Gives Way","Shadow Hawk Takes Flight","Rolling Thunder Cannon Punch",
   "Eight Diagram Dragon Palm","Under Leaf Pluck Lotus","Immortal Emerges from Cave",
   "Felling Tree with Roots","The Blessing of Many Fractures","The Mistress of All Agonies",
   "Black Tiger Steals Heart","Lead Horse Back to Stable","Bar the Big Boss",
   "Dragon Plays with Fire"
  ),
  ("MARVEL's Iron Fist" to 2) to listOf(
   "The Fury of Iron Fist","The City's Not for Burning","This Deadly Secret...",
   "Target: Iron Fist","Heart of the Dragon","The Dragon Dies at Dawn",
   "Morning of the Mindstorm","Citadel on the Edge of Vengeance",
   "War Without End","A Duel of Iron"
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
