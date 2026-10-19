package de.benzo.scorium

object EpisodeTitles {
 private val known = mapOf(
  ("Arrow" to 7) to listOf("Inmate 4587","The Longbow Hunters","Crossing Lines","Level Two","The Demon","Due Process","The Slabside Redemption","Unmasked","Elseworlds, Part 2","My Name Is Emiko Queen","Past Sins","Emerald Archer","Star City Slayer","Brothers & Sisters","Training Day","Star City 2040","Inheritance","Lost Canary","Spartan","Confessions","Living Proof","You Have Saved This City"),
  ("Arrow" to 8) to listOf("Starling City","Welcome to Hong Kong","Leap of Faith","Present Tense","Prochnost","Reset","Purgatory","Crisis on Infinite Earths: Part Four","Green Arrow & The Canaries","Fadeout"),
  ("Peacemaker" to 2) to listOf("The Ties That Grind","A Man Is Only as Good as His Bird","Another Rick Up My Sleeve","Need I Say Door","Back to the Suture","Ignorance Is Chris","Like a Keith in the Night","Full Nelson"),

  ("Arrow" to 4) to listOf("Green Arrow","The Candidate","Restoration","Beyond Redemption","Haunted","Lost Souls","Brotherhood","Legends of Yesterday","Dark Waters","Blood Debts","A.W.O.L.","Unchained","Sins of the Father","Code of Silence","Taken","Broken Hearts","Beacon of Hope","Eleven-Fifty-Nine","Canary Cry","Genesis","Monument Point","Lost in the Flood","Schism"),
  ("Arrow" to 5) to listOf("Legacy","The Recruits","A Matter of Trust","Penance","Human Target","So It Begins","Vigilante","Invasion!","What We Leave Behind","Who Are You?","Second Chances","Bratva","Spectre of the Gun","The Sin-Eater","Fighting Fire with Fire","Checkmate","Kapiushon","Disbanded","Dangerous Liaisons","Underneath","Honor Thy Fathers","Missing","Lian Yu"),
  ("Arrow" to 6) to listOf("Fallout","Tribute","Next of Kin","Reversal","Deathstroke Returns","Promises Kept","Thanksgiving","Crisis on Earth-X, Part 2","Irreconcilable Differences","Divided","We Fall","All for Nothing","The Devil's Greatest Trick","Collision Course","Doppelganger","The Thanatos Guild","Brothers in Arms","Fundamentals","The Dragon","Shifting Allegiances","Docket No. 11-19-41-73","The Ties That Bind","Life Sentence"),

  ("Star Wars Resistance" to 1) to listOf("The Recruit: Part 1","The Recruit: Part 2","The Triple Dark","Fuel for the Fire","The High Tower","The Children from Tehar","Signal from Sector Six","Synara's Score","The Platform Classic","Secrets and Holograms","Station Theta Black","Bibo","Dangerous Business","The Doza Dilemma","The First Order Occupation","The New Trooper","The Core Problem","The Disappeared","Descent","No Escape: Part 1","No Escape: Part 2"),
  ("Star Wars Resistance" to 2) to listOf("Into the Unknown","A Quick Salvage Run","Live Fire","Hunt on Celsor 3","The Engineer","From Beneath","The Relic Raiders","Rendezvous Point","The Voxx Vortex 5000","Kaz's Curse","Station to Station","The Missing Agent","Breakout","The Mutiny","The New World","No Place Safe","Rebuilding the Resistance","The Escape: Part 1","The Escape: Part 2"),

  ("Marvel Zombies" to 1) to listOf("Wonderful","The New World","The Dead","The Living"),
  ("Creature Commandos" to 1) to listOf("The Collywobbles","The Tourmaline Necklace","Cheers to the Tin Man","Chasing Squirrels","The Iron Pot","Priyatel Skelet","A Very Funny Monster"),
  ("Peacemaker" to 1) to listOf("A Whole New Whirled","Best Friends, For Never","Better Goff Dead","The Choad Less Traveled","Monkey Dory","Murn After Reading","Stop Dragon My Heart Around","It's Cow or Never"),

  ("Star Wars Rebels" to 1) to listOf("Spark of Rebellion: Part 1","Spark of Rebellion: Part 2","Droids in Distress","Fighter Flight","Rise of the Old Masters","Breaking Ranks","Out of Darkness","Empire Day","Gathering Forces","Path of the Jedi","Idiot's Array","Vision of Hope","Call to Action","Rebel Resolve","Fire Across the Galaxy"),
  ("Star Wars Rebels" to 2) to listOf("The Siege of Lothal: Part 1","The Siege of Lothal: Part 2","The Lost Commanders","Relics of the Old Republic","Always Two There Are","Brothers of the Broken Horn","Wings of the Master","Blood Sisters","Stealth Strike","The Future of the Force","Legacy","A Princess on Lothal","The Protector of Concord Dawn","Legends of the Lasat","The Call","Homecoming","The Honorable Ones","Shroud of Darkness","The Forgotten Droid","The Mystery of Chopper Base","Twilight of the Apprentice: Part 1","Twilight of the Apprentice: Part 2"),
  ("Star Wars Rebels" to 3) to listOf("Steps Into Shadow: Part 1","Steps Into Shadow: Part 2","The Holocrons of Fate","The Antilles Extraction","Hera's Heroes","The Last Battle","Imperial Supercommandos","Iron Squadron","The Wynkahthu Job","An Inside Man","Visions and Voices","Ghosts of Geonosis: Part 1","Ghosts of Geonosis: Part 2","Warhead","Trials of the Darksaber","Legacy of Mandalore","Through Imperial Eyes","Secret Cargo","Double Agent Droid","Twin Suns","Zero Hour: Part 1","Zero Hour: Part 2"),
  ("Star Wars Rebels" to 4) to listOf("Heroes of Mandalore: Part 1","Heroes of Mandalore: Part 2","In the Name of the Rebellion: Part 1","In the Name of the Rebellion: Part 2","The Occupation","Flight of the Defender","Kindred","Crawler Commandeers","Rebel Assault","Jedi Night","DUME","Wolves and a Door","A World Between Worlds","A Fool's Hope","Family Reunion – and Farewell: Part 1","Family Reunion – and Farewell: Part 2"),

  ("Star Wars: The Clone Wars" to 3) to listOf("Clone Cadets","ARC Troopers","Supply Lines","Sphere of Influence","Corruption","The Academy","Assassin","Evil Plans","Hunt for Ziro","Heroes on Both Sides","Pursuit of Peace","Nightsisters","Monster","Witches of the Mist","Overlords","Altar of Mortis","Ghosts of Mortis","The Citadel","Counterattack","Citadel Rescue","Padawan Lost","Wookiee Hunt"),
  ("Star Wars: The Clone Wars" to 4) to listOf("Water War","Gungan Attack","Prisoners","Shadow Warrior","Mercy Mission","Nomad Droids","Darkness on Umbara","The General","Plan of Dissent","Carnage of Krell","Kidnapped","Slaves of the Republic","Escape from Kadavo","A Friend in Need","Deception","Friends and Enemies","The Box","Crisis on Naboo","Massacre","Bounty","Brothers","Revenge"),
  ("Star Wars: The Clone Wars" to 5) to listOf("Revival","A War on Two Fronts","Front Runners","The Soft War","Tipping Points","The Gathering","A Test of Strength","Bound for Rescue","A Necessary Bond","Secret Weapons","A Sunny Day in the Void","Missing in Action","Point of No Return","Eminence","Shades of Reason","The Lawless","Sabotage","The Jedi Who Knew Too Much","To Catch a Jedi","The Wrong Jedi"),
  ("Star Wars: The Clone Wars" to 6) to listOf("The Unknown","Conspiracy","Fugitive","Orders","An Old Friend","The Rise of Clovis","Crisis at the Heart","The Disappeared, Part I","The Disappeared, Part II","The Lost One","Voices","Destiny","Sacrifice"),
  ("Star Wars: The Clone Wars" to 7) to listOf("The Bad Batch","A Distant Echo","On the Wings of Keeradaks","Unfinished Business","Gone with a Trace","Deal No Deal","Dangerous Debt","Together Again","Old Friends Not Forgotten","The Phantom Apprentice","Shattered","Victory and Death"),

  ("Star Wars: The Clone Wars" to 1) to listOf("Ambush","Rising Malevolence","Shadow of Malevolence","Destroy Malevolence","Rookies","Downfall of a Droid","Duel of the Droids","Bombad Jedi","Cloak of Darkness","Lair of Grievous","Dooku Captured","The Gungan General","Jedi Crash","Defenders of Peace","Trespass","The Hidden Enemy","Blue Shadow Virus","Mystery of a Thousand Moons","Storm Over Ryloth","Innocents of Ryloth","Liberty on Ryloth","Hostage Crisis"),
  ("Star Wars: The Clone Wars" to 2) to listOf("Holocron Heist","Cargo of Doom","Children of the Force","Senate Spy","Landing at Point Rain","Weapons Factory","Legacy of Terror","Brain Invaders","Grievous Intrigue","The Deserter","Lightsaber Lost","The Mandalore Plot","Voyage of Temptation","Duchess of Mandalore","Senate Murders","Cat and Mouse","Bounty Hunters","The Zillo Beast","The Zillo Beast Strikes Back","Death Trap","R2 Come Home","Lethal Trackdown"),

  ("The Bad Batch" to 1) to listOf("Aftermath","Cut and Run","Replacements","Cornered","Rampage","Decommissioned","Battle Scars","Reunion","Bounty Lost","Common Ground","Devil's Deal","Rescue on Ryloth","Infested","War-Mantle","Return to Kamino","Kamino Lost"),
  ("The Bad Batch" to 2) to listOf("Spoils of War","Ruins of War","The Solitary Clone","Faster","Entombed","Tribe","The Clone Conspiracy","Truth and Consequences","The Crossing","Retrieval","Metamorphosis","The Outpost","Pabu","Tipping Point","The Summit","Plan 99"),
  ("The Bad Batch" to 3) to listOf("Confined","Paths Unknown","Shadows of Tantiss","A Different Approach","The Return","Infiltration","Extraction","Bad Territory","The Harbinger","Identity Crisis","Point of No Return","Juggernaut","Into the Breach","Flash Strike","The Cavalry Has Arrived"),

  ("The Flash" to 3) to listOf("Flashpoint","Paradox","Magenta","The New Rogues","Monster","Shade","Killer Frost","Invasion!","The Present","Borrowing Problems from the Future","Dead or Alive","Untouchable","Attack on Gorilla City","Attack on Central City","The Wrath of Savitar","Into the Speed Force","Duet","Abra Kadabra","The Once and Future Flash","I Know Who You Are","Cause and Effect","Infantino Street","Finish Line"),
  ("Supergirl" to 3) to listOf("Girl of Steel","Triggers","Far from the Tree","The Faithful","Damage","Midvale","Wake Up","Crisis on Earth-X, Part 1","Reign","Legion of Super-Heroes","Fort Rozz","For Good","Both Sides Now","Schott Through the Heart","In Search of Lost Time","Of Two Minds","Trinity","Shelter from the Storm","The Fanatical","Dark Side of the Moon","Not Kansas","Make It Reign","Battles Lost and Won"),

  ("DC’s Legends of Tomorrow" to 2) to listOf("Out of Time","The Justice Society of America","Shogun","Abominations","Compromised","Outlaw Country","Invasion!","The Chicago Way","Raiders of the Lost Art","The Legion of Doom","Turncoat","Camelot/3000","Land of the Lost","Moonshot","Fellowship of the Spear","Doomworld","Aruba"),
  ("Arrow" to 3) to listOf("The Calm","Sara","Corto Maltese","The Magician","The Secret Origin of Felicity Smoak","Guilty","Draw Back Your Bow","The Brave and the Bold","The Climb","Left Behind","Midnight City","Uprising","Canaries","The Return","Nanda Parbat","The Offer","Suicidal Tendencies","Public Enemy","Broken Arrow","The Fallen","Al Sah-him","This Is Your Sword","My Name Is Oliver Queen"),

  ("Arrow" to 2) to listOf("City of Heroes","Identity","Broken Dolls","Crucible","League of Assassins","Keep Your Enemies Closer","State v. Queen","The Scientist","Three Ghosts","Blast Radius","Blind Spot","Tremors","Heir to the Demon","Time of Death","The Promise","Suicide Squad","Birds of Prey","Deathstroke","The Man Under the Hood","Seeing Red","City of Blood","Streets of Fire","Unthinkable"),
  ("Supergirl" to 2) to listOf("The Adventures of Supergirl","The Last Children of Krypton","Welcome to Earth","Survivors","Crossfire","Changing","The Darkest Place","Medusa","Supergirl Lives","We Can Be Heroes","The Martian Chronicles","Luthors","Mr. & Mrs. Mxyzptlk","Homecoming","Exodus","Star-Crossed","Distant Sun","Ace Reporter","Alex","City of Lost Children","Resist","Nevertheless, She Persisted"),

  ("Arrow" to 1) to listOf("Pilot","Honor Thy Father","Lone Gunmen","An Innocent Man","Damaged","Legacies","Muse of Fire","Vendetta","Year's End","Burned","Trust but Verify","Vertigo","Betrayal","The Odyssey","Dodger","Dead to Rights","The Huntress Returns","Salvation","Unfinished Business","Home Invasion","The Undertaking","Darkness on the Edge of Town","Sacrifice"),
  ("Black Lightning" to 2) to listOf("The Book of Consequences: Chapter One: Rise of the Green Light Babies","The Book of Consequences: Chapter Two: Black Jesus Blues","The Book of Consequences: Chapter Three: Master Lowry","The Book of Consequences: Chapter Four: Translucent Freak","The Book of Blood: Chapter One: Requiem","The Book of Blood: Chapter Two: The Perdi","The Book of Blood: Chapter Three: The Sange","The Book of Rebellion: Chapter One: Exodus","The Book of Rebellion: Chapter Two: Gift of the Magi","The Book of Rebellion: Chapter Three: Angelitos Negros","The Book of Secrets: Chapter One: Prodigal Son","The Book of Secrets: Chapter Two: Just and Unjust","The Book of Secrets: Chapter Three: Pillar of Fire","The Book of Secrets: Chapter Four: Original Sin","The Book of the Apocalypse: Chapter One: The Alpha","The Book of the Apocalypse: Chapter Two: The Omega"),
  ("The Flash" to 2) to listOf("The Man Who Saved Central City","Flash of Two Worlds","Family of Rogues","The Fury of Firestorm","The Darkness and the Light","Enter Zoom","Gorilla Warfare","Legends of Today","Running to Stand Still","Potential Energy","The Reverse-Flash Returns","Fast Lane","Welcome to Earth-2","Escape from Earth-2","King Shark","Trajectory","Flash Back","Versus Zoom","Back to Normal","Rupture","The Runaway Dinosaur","Invincible","The Race of His Life"),

  ("Supergirl" to 1) to listOf("Pilot","Stronger Together","Fight or Flight","Livewire","How Does She Do It?","Red Faced","Human for a Day","Hostile Takeover","Blood Bonds","Childish Things","Strange Visitor from Another Planet","Bizarro","For the Girl Who Has Everything","Truth, Justice and the American Way","Solitude","Falling","Manhunter","Worlds Finest","Myriad","Better Angels"),
  ("The Flash" to 1) to listOf("Pilot","Fastest Man Alive","Things You Can't Outrun","Going Rogue","Plastique","The Flash Is Born","Power Outage","Flash vs. Arrow","The Man in the Yellow Suit","Revenge of the Rogues","The Sound and the Fury","Crazy for You","The Nuclear Man","Fallout","Out of Time","Rogue Time","Tricksters","All Star Team Up","Who Is Harrison Wells?","The Trap","Grodd Lives","Rogue Air","Fast Enough"),

  ("Black Lightning" to 1) to listOf("The Resurrection","LaWanda: The Book of Hope","LaWanda: The Book of Burial","Black Jesus","And Then the Devil Brought the Plague: The Book of Green Light","Three Sevens: The Book of Thunder","Equinox: The Book of Fate","The Book of Revelations","The Book of Little Black Lies","Sins of the Father: The Book of Redemption","Black Jesus: The Book of Crucifixion","The Resurrection and the Light: The Book of Pain","Shadow of Death: The Book of War"),
  ("Batwoman" to 1) to listOf("Pilot","The Rabbit Hole","Down, Down, Down","Who Are You?","Mine Is a Long and a Sad Tale","I'll Be Judge, I'll Be Jury","Tell Me the Truth","A Mad Tea-Party","Crisis on Infinite Earths: Part Two","How Queer Everything Is Today!","An Un-Birthday Present","Take Your Choice","Drink Me","Grinning from Ear to Ear","Off with Her Head","Through the Looking-Glass","A Narrow Escape","If You Believe in Me, I'll Believe in You","A Secret Kept from All the Rest","O, Mouse!"),
  ("DC’s Legends of Tomorrow" to 1) to listOf("Pilot, Part 1","Pilot, Part 2","Blood Ties","White Knights","Fail-Safe","Star City 2046","Marooned","Night of the Hawk","Left Behind","Progeny","The Magnificent Eight","Last Refuge","Leviathan","River of Time","Destiny","Legendary"),

  ("Stargirl" to 1) to listOf("Pilot","S.T.R.I.P.E.","Icicle","Wildcat","Hourman and Dr. Mid-Nite","The Justice Society","Shiv Part One","Shiv Part Two","Brainwave","Brainwave Jr.","Shining Knight","Stars & S.T.R.I.P.E. Part One","Stars & S.T.R.I.P.E. Part Two"),
  ("Stargirl" to 2) to listOf("Summer School: Chapter One","Summer School: Chapter Two","Summer School: Chapter Three","Summer School: Chapter Four","Summer School: Chapter Five","Summer School: Chapter Six","Summer School: Chapter Seven","Summer School: Chapter Eight","Summer School: Chapter Nine","Summer School: Chapter Ten","Summer School: Chapter Eleven","Summer School: Chapter Twelve","Summer School: Chapter Thirteen"),
  ("Stargirl" to 3) to listOf("Frenemies: Chapter One – The Murder","Frenemies: Chapter Two – The Suspects","Frenemies: Chapter Three – The Blackmail","Frenemies: Chapter Four – The Evidence","Frenemies: Chapter Five – The Thief","Frenemies: Chapter Six – The Betrayal","Frenemies: Chapter Seven – Infinity Inc. Part One","Frenemies: Chapter Eight – Infinity Inc. Part Two","Frenemies: Chapter Nine – The Monsters","Frenemies: Chapter Ten – The Killer","Frenemies: Chapter Eleven – The Haunting","Frenemies: Chapter Twelve – The Last Will and Testament of Sylvester Pemberton","Frenemies: Chapter Thirteen – The Reckoning"),

  ("The Penguin" to 1) to listOf("After Hours","Inside Man","Bliss","Cent'Anni","Homecoming","Gold Summit","Top Hat","A Great or Little Thing"),
  ("Watchmen" to 1) to listOf("It's Summer and We're Running Out of Ice","Martial Feats of Comanche Horsemanship","She Was Killed by Space Junk","If You Don't Like My Story, Write Your Own","Little Fear of Lightning","This Extraordinary Being","An Almost Religious Awe","A God Walks into Abar","See How They Fly"),
  ("Swamp Thing" to 1) to listOf("Pilot","Worlds Apart","He Speaks","Darkness on the Edge of Town","Drive All Night","The Price You Pay","Brilliant Disguise","Long Walk Home","The Anatomy Lesson","Loose Ends"),
  ("Gotham Knights" to 1) to listOf("Pilot","Scene of the Crime","Under Pressure","Of Butchers and Betrayals","More Money, More Problems","A Chill in Gotham","Bad to Be Good","Belly of the Beast","Dark Knight of the Soul","Poison Pill","Daddy Issues","City of Owls","Night of the Owls"),
  ("Krypton" to 1) to listOf("Pilot","House of El","The Rankless Initiative","The Word of Rao","House of Zod","Civil Wars","Transformation","Savage Night","Hope","The Phantom Zone"),
  ("Krypton" to 2) to listOf("Light-Years from Home","Ghost in the Fire","Will to Power","Danger Close","A Better Yesterday","In Zod We Trust","Zods and Monsters","Mercy","Blood Moon","The Alpha and the Omega"),

  ("Skeleton Crew" to 1) to listOf("This Could Be a Real Adventure","Way, Way Out Past the Barrier","Very Interesting, as an Astrogation Problem","Can't Say I Remember No At Attin","You Have a Lot to Learn About Pirates","Zero Friends Again","We're Gonna Be in So Much Trouble","The Real Good Guys"),
  ("Star Wars: Geschichten der Jedi" to 1) to listOf("Life and Death","Justice","Choices","The Sith Lord","Practice Makes Perfect","Resolve"),
  ("Star Wars: Geschichten des Imperiums" to 1) to listOf("The Path of Fear","The Path of Anger","The Path of Hate","Devoted","Realization","The Way Out"),
  ("Star Wars: Geschichten der Unterwelt" to 1) to listOf("A Way Forward","Friends","One Good Deed","The Good Life","A Good Turn","One Last Fight"),
  ("Star Wars: Clone Wars (2003)" to 1) to (1..10).map { "Chapter $it" },
  ("Star Wars: Clone Wars (2003)" to 2) to (11..20).map { "Chapter $it" },
  ("Star Wars: Clone Wars (2003)" to 3) to (21..25).map { "Chapter $it" },

  ("Das Buch von Boba Fett" to 1) to listOf("Chapter 1: Stranger in a Strange Land","Chapter 2: The Tribes of Tatooine","Chapter 3: The Streets of Mos Espa","Chapter 4: The Gathering Storm","Chapter 5: Return of the Mandalorian","Chapter 6: From the Desert Comes a Stranger","Chapter 7: In the Name of Honor"),
  ("Obi-Wan Kenobi" to 1) to listOf("Part I","Part II","Part III","Part IV","Part V","Part VI"),
  ("Ahsoka" to 1) to listOf("Part One: Master and Apprentice","Part Two: Toil and Trouble","Part Three: Time to Fly","Part Four: Fallen Jedi","Part Five: Shadow Warrior","Part Six: Far, Far Away","Part Seven: Dreams and Madness","Part Eight: The Jedi, the Witch, and the Warlord"),
  ("The Acolyte" to 1) to listOf("Lost / Found","Revenge / Justice","Destiny","Day","Night","Teach / Corrupt","Choice","The Acolyte"),
  ("Andor" to 1) to listOf("Kassa","That Would Be Me","Reckoning","Aldhani","The Axe Forgets","The Eye","Announcement","Narkina 5","Nobody's Listening!","One Way Out","Daughter of Ferrix","Rix Road"),
  ("Andor" to 2) to listOf("One Year Later","Sagrona Teema","Harvest","Ever Been to Ghorman?","I Have Friends Everywhere","What a Festive Evening","Messenger","Who Are You?","Welcome to the Rebellion","Make It Stop","Who Else Knows?","Jedha, Kyber, Erso"),

  ("The Mandalorian" to 1) to listOf("Chapter 1: The Mandalorian","Chapter 2: The Child","Chapter 3: The Sin","Chapter 4: Sanctuary","Chapter 5: The Gunslinger","Chapter 6: The Prisoner","Chapter 7: The Reckoning","Chapter 8: Redemption"),
  ("The Mandalorian" to 2) to listOf("Chapter 9: The Marshal","Chapter 10: The Passenger","Chapter 11: The Heiress","Chapter 12: The Siege","Chapter 13: The Jedi","Chapter 14: The Tragedy","Chapter 15: The Believer","Chapter 16: The Rescue"),
  ("The Mandalorian" to 3) to listOf("Chapter 17: The Apostate","Chapter 18: The Mines of Mandalore","Chapter 19: The Convert","Chapter 20: The Foundling","Chapter 21: The Pirate","Chapter 22: Guns for Hire","Chapter 23: The Spies","Chapter 24: The Return"),

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
  ("MARVEL's Punisher" to 1) to listOf(
   "3 AM","Two Dead Men","Kandahar","Resupply","Gunner","The Judas Goat",
   "Crosshairs","Cold Steel","Front Toward Enemy","Virtue of the Vicious",
   "Danger Close","Home","Memento Mori"
  ),
  ("MARVEL's Punisher" to 2) to listOf(
   "Roadhouse Blues","Fight or Flight","Trouble the Water","Scar Tissue",
   "One-Eyed Jacks","Nakazat","One Bad Day","My Brother's Keeper",
   "Flustercluck","The Dark Hearts of Men","The Abyss","Collision Course","The Whirlwind"
  ),
  ("MARVEL's Agents of Shield" to 2) to listOf(
   "Shadows","Heavy Is the Head","Making Friends and Influencing People",
   "Face My Enemy","A Hen in the Wolf House","A Fractured House",
   "The Writing on the Wall","The Things We Bury","...Ye Who Enter Here",
   "What They Become","Aftershocks","Who You Really Are","One of Us",
   "Love in the Time of Hydra","One Door Closes","Afterlife","Melinda",
   "The Frenemy of My Enemy","The Dirty Half Dozen","Scars",
   "S.O.S. Part One","S.O.S. Part Two"
  ),
  ("MARVEL's Agents of Shield" to 3) to listOf(
   "Laws of Nature","Purpose in the Machine","A Wanted (Inhu)man",
   "Devils You Know","4,722 Hours","Among Us Hide...","Chaos Theory",
   "Many Heads, One Tale","Closure","Maveth","Bouncing Back",
   "The Inside Man","Parting Shot","Watchdogs","Spacetime","Paradise Lost",
   "The Team","The Singularity","Failed Experiments","Emancipation",
   "Absolution","Ascension"
  ),
  ("X-MEN 97" to 1) to listOf(
   "To Me, My X-Men","Mutant Liberation Begins","Fire Made Flesh",
   "Motendo / Lifedeath - Part 1","Remember It","Lifedeath - Part 2",
   "Bright Eyes","Tolerance Is Extinction - Part 1",
   "Tolerance Is Extinction - Part 2","Tolerance Is Extinction - Part 3"
  ),
  ("MARVEL's Agents of Shield" to 4) to listOf(
   "The Ghost","Meet the New Boss","Uprising","Let Me Stand Next to Your Fire",
   "Lockup","The Good Samaritan","Deals with Our Devils","The Laws of Inferno Dynamics",
   "Broken Promises","The Patriot","Wake Up","Hot Potato Soup","BOOM",
   "The Man Behind the Shield","Self Control","What If...","Identity and Change",
   "No Regrets","All the Madame's Men","Farewell, Cruel World!",
   "The Return","World's End"
  ),
  ("MARVEL's Agents of Shield" to 5) to listOf(
   "Orientation (Part One)","Orientation (Part Two)","A Life Spent",
   "A Life Earned","Rewind","Fun & Games","Together or Not at All",
   "The Last Day","Best Laid Plans","Past Life","All the Comforts of Home",
   "The Real Deal","Principia","The Devil Complex","Rise and Shine",
   "Inside Voices","The Honeymoon","All Roads Lead...","Option Two",
   "The One Who Will Save Us All","The Force of Gravity","The End"
  ),
  ("MARVEL's Agents of Shield" to 6) to listOf(
   "Missing Pieces","Window of Opportunity","Fear and Loathing on the Planet of Kitson",
   "Code Yellow","The Other Thing","Inescapable","Toldja",
   "Collision Course (Part I)","Collision Course (Part II)","Leap",
   "From the Ashes","The Sign","New Life"
  ),
  ("MARVEL's Agents of Shield" to 7) to listOf(
   "The New Deal","Know Your Onions","Alien Commies from the Future!",
   "Out of the Past","A Trout in the Milk","Adapt or Die",
   "The Totally Excellent Adventures of Mack and the D",
   "After, Before","As I Have Always Been","Stolen","Brand New Day",
   "The End Is at Hand","What We're Fighting For"
  ),
  ("WandaVision" to 1) to listOf(
   "Filmed Before a Live Studio Audience","Don't Touch That Dial","Now in Color",
   "We Interrupt This Program","On a Very Special Episode...","All-New Halloween Spooktacular!",
   "Breaking the Fourth Wall","Previously On","The Series Finale"
  ),
  ("The Falcon and the Winter Soldier" to 1) to listOf(
   "New World Order","The Star-Spangled Man","Power Broker","The Whole World Is Watching",
   "Truth","One World, One People"
  ),
  ("Loki" to 1) to listOf(
   "Glorious Purpose","The Variant","Lamentis","The Nexus Event",
   "Journey Into Mystery","For All Time. Always."
  ),
  ("Loki" to 2) to listOf(
   "Ouroboros","Breaking Brad","1893","Heart of the TVA",
   "Science/Fiction","Glorious Purpose"
  ),
  ("Hawkeye" to 1) to listOf(
   "Never Meet Your Heroes","Hide and Seek","Echoes","Partners, Am I Right?",
   "Ronin","So This Is Christmas?"
  ),
  ("Moon Knight" to 1) to listOf(
   "The Goldfish Problem","Summon the Suit","The Friendly Type","The Tomb",
   "Asylum","Gods and Monsters"
  ),
  ("Ms. Marvel" to 1) to listOf(
   "Generation Why","Crushed","Destined","Seeing Red","Time and Again","No Normal"
  ),
  ("She-Hulk" to 1) to listOf(
   "A Normal Amount of Rage","Superhuman Law","The People vs. Emil Blonsky",
   "Is This Not Real Magic?","Mean, Green, and Straight Poured into These Jeans",
   "Just Jen","The Retreat","Ribbit and Rip It","Whose Show Is This?"
  ),
  ("Secret Invasion" to 1) to listOf(
   "Resurrection","Promises","Betrayed","Beloved","Harvest","Home"
  ),
  ("Echo" to 1) to listOf(
   "Chafa","Lowak","Tuklo","Taloa","Maya"
  ),
  ("Agatha All Along" to 1) to listOf(
   "Seekest Thou the Road","Circle Sewn with Fate / Unlock Thy Hidden Gate",
   "Through Many Miles / Of Tricks and Trials","If I Can't Reach You / Let My Song Teach You",
   "Darkest Hour / Wake Thy Power","Familiar by Thy Side",
   "Death's Hand in Mine","Follow Me My Friend / To Glory at the End",
   "Maiden Mother Crone"
  ),
  ("What If...?" to 1) to listOf(
   "What If... Captain Carter Were the First Avenger?",
   "What If... T'Challa Became a Star-Lord?",
   "What If... the World Lost Its Mightiest Heroes?",
   "What If... Doctor Strange Lost His Heart Instead of His Hands?",
   "What If... Zombies?!","What If... Killmonger Rescued Tony Stark?",
   "What If... Thor Were an Only Child?","What If... Ultron Won?",
   "What If... the Watcher Broke His Oath?"
  ),
  ("What If...?" to 2) to listOf(
   "What If... Nebula Joined the Nova Corps?",
   "What If... Peter Quill Attacked Earth's Mightiest Heroes?",
   "What If... Happy Hogan Saved Christmas?",
   "What If... Iron Man Crashed into the Grandmaster?",
   "What If... Captain Carter Fought the Hydra Stomper?",
   "What If... Kahhori Reshaped the World?",
   "What If... Hela Found the Ten Rings?",
   "What If... the Avengers Assembled in 1602?",
   "What If... Strange Supreme Intervened?"
  ),
  ("What If...?" to 3) to listOf(
   "What If... the Hulk Fought the Mech Avengers?",
   "What If... Agatha Went to Hollywood?",
   "What If... the Red Guardian Stopped the Winter Soldier?",
   "What If... Howard the Duck Got Hitched?",
   "What If... the Emergence Destroyed the Earth?",
   "What If... 1872?","What If... the Watcher Disappeared?",
   "What If... What If?"
  ),
  ("I Am Groot" to 1) to listOf(
   "Groot's First Steps","The Little Guy","Groot's Pursuit",
   "Groot Takes a Bath","Magnum Opus"
  ),
  ("I Am Groot" to 2) to listOf(
   "Are You My Groot?","Groot Noses Around","Groot's Snow Day",
   "Groot's Sweet Treat","Groot and the Great Prophecy"
  ),
  ("Ironheart" to 1) to listOf(
   "Take Me Home","Will the Real Natalie Please Stand Up?",
   "We in Danger, Girl","Bad Magic","Karma's a Glitch","The Past Is the Past"
  ),
  ("Eyes of Wakanda" to 1) to listOf(
   "Into the Lion's Den","Legends and Lies","Lost and Found","The Last Panther"
  ),
  ("Daredevil: Born Again" to 1) to listOf(
   "Heaven's Half Hour","Optics","The Hollow of His Hand",
   "Sic Semper Systema","With Interest","Excessive Force",
   "Art for Art's Sake","Isle of Joy","Straight to Hell"
  ),
  ("Your Friendly Neighborhood Spider-Man" to 1) to listOf(
   "Amazing Fantasy","The Parker Luck","Secret Identity",
   "Hitting the Big Time","The Unicorn Unleashed",
   "Duel with the Devil","Scorpion Rising","Tangled Web",
   "Hero or Menace","If This Be My Destiny..."
  ),
  ("Wonder Man" to 1) to listOf(
   "Matinee","Self-Tape","Pacoima","Doorman","Found Footage",
   "Callback","Kathy Friedman","Yucca Valley"
  ),
  ("Daredevil: Born Again" to 2) to listOf(
   "The Northern Star","Shoot the Moon","The Scales & The Sword","Gloves Off",
   "The Grand Design","Requiem","The Hateful Darkness","The Southern Cross"
  ),
  ("X-MEN 97" to 2) to listOf(
   "Days of Past Future","A Force to Be Reckoned With","Rise of Apocalypse: Part 1",
   "Rise of Apocalypse: Part 2","Weapon X, Lies, and DVDs","Danger.exe",
   "Strange Land, Savage Heart","The Dead Man's Hand","Survival of the Fittest"
  ),
  ("VisionQuest" to 1) to listOf(
   "No Strings","Theseus","Reboot","Malware","Firewall",
   "Deadlock","Immolation","Forever"
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
