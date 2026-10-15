package de.benzo.scorium

/** Criterion-specific guidance for the five score bands. */
internal fun criterionDescription(criterion:String,score:Int):String {
 val band=when(score) { in 1..2->0;in 3..4->1;in 5..6->2;in 7..8->3;else->4 }
 val descriptions=mapOf(
 "Handlung & Drehbuch" to listOf(
 "Die Geschichte wirkt kaum durchdacht. Wichtige Ereignisse ergeben wenig Sinn und die Handlung findet keinen überzeugenden roten Faden.",
 "Die Grundidee ist erkennbar, doch Logiklücken, schwache Wendungen oder unausgereifte Szenen bremsen die Erzählung deutlich.",
 "Die Handlung funktioniert im Wesentlichen. Einige vorhersehbare Entwicklungen oder unklare Entscheidungen verhindern jedoch einen stärkeren Eindruck.",
 "Die Geschichte ist schlüssig, abwechslungsreich und gut aufgebaut. Die meisten Wendungen entstehen glaubwürdig aus dem Geschehen.",
 "Ein außergewöhnlich präzises Drehbuch: Jede Szene trägt zur Geschichte bei, Wendungen überraschen sinnvoll und die Erzählung bleibt nachhaltig im Kopf."),
 "Charaktere & Entwicklung" to listOf(
 "Die Figuren bleiben austauschbar; ihre Entscheidungen sind kaum nachvollziehbar und eine glaubwürdige Entwicklung fehlt.",
 "Einzelne Figuren besitzen Potenzial, doch Motive und Veränderungen werden zu selten überzeugend gezeigt.",
 "Die Hauptfiguren sind verständlich angelegt, entwickeln sich aber nicht durchgehend oder bleiben stellenweise oberflächlich.",
 "Die Figuren besitzen klare Ziele, erkennbare Konflikte und entwickeln sich über die Geschichte hinweg glaubwürdig.",
 "Vielschichtige Persönlichkeiten mit überzeugenden Motiven und eindrucksvollen Entwicklungen machen die Figuren unvergesslich."),
 "Charaktere & Interaktionen" to listOf(
 "Beziehungen und Gespräche wirken künstlich; zwischen den Figuren entsteht kaum eine glaubwürdige Verbindung.",
 "Die Figuren reagieren häufig unpassend aufeinander, und wichtige Begegnungen entfalten nur wenig Wirkung.",
 "Die Interaktionen sind überwiegend verständlich, bleiben aber teilweise oberflächlich oder vorhersehbar.",
 "Beziehungen, Konflikte und kleine Gesten machen die Figuren glaubwürdig und ihre Begegnungen interessant.",
 "Jede Begegnung offenbart neue Facetten der Figuren; Dialoge, Reaktionen und Beziehungen greifen außergewöhnlich stark ineinander."),
 "Antagonist & Konflikt" to listOf(
 "Dem zentralen Konflikt fehlt jede überzeugende Bedrohung. Die Gegenseite besitzt kaum nachvollziehbare Motive.",
 "Ein Konflikt ist vorhanden, wirkt aber konstruiert oder verliert durch schwache Gegenspieler an Bedeutung.",
 "Der Konflikt trägt die Geschichte, doch Motivation und Konsequenzen bleiben teilweise zu einfach.",
 "Ein überzeugender Gegenspieler oder innerer Konflikt erzeugt echte Spannung und stellt die Figuren vor schwierige Entscheidungen.",
 "Ein außergewöhnlich vielschichtiger Konflikt mit klaren Konsequenzen und einem faszinierenden Gegenpol prägt die gesamte Geschichte."),
 "Schauspiel & Dialoge" to listOf(
 "Die Darstellungen wirken häufig unglaubwürdig; Gespräche klingen unnatürlich und Emotionen erreichen kaum das Publikum.",
 "Einige Leistungen überzeugen, doch hölzerne Dialoge und unstimmige Reaktionen stören den Gesamteindruck.",
 "Das Schauspiel ist ordentlich, mit gelungenen Momenten und einzelnen schwächeren Gesprächen.",
 "Überzeugende Leistungen und natürliche Dialoge lassen Beziehungen, Konflikte und Emotionen lebendig werden.",
 "Herausragende Darstellungen, nuancierte Reaktionen und präzise Dialoge verleihen selbst kleinen Szenen große Bedeutung."),
 "Spannung & Erzähltempo" to listOf(
 "Die Geschichte zieht sich oder hetzt von Szene zu Szene. Ein tragfähiger Spannungsbogen ist kaum vorhanden.",
 "Lange Leerlaufphasen oder abrupte Temposprünge erschweren es, am Geschehen dranzubleiben.",
 "Das Tempo ist meist passend, verliert aber stellenweise an Schwung oder nimmt wichtigen Momenten zu wenig Zeit.",
 "Ruhige und intensive Passagen sind gut aufeinander abgestimmt; die Spannung entwickelt sich nachvollziehbar.",
 "Ein meisterhaftes Tempo hält die Aufmerksamkeit durchgehend hoch und setzt jede Ruhephase sowie jeden Höhepunkt gezielt ein."),
 "Action & Inszenierung" to listOf(
 "Action und Inszenierung wirken unübersichtlich oder beliebig; wichtige Szenen verlieren dadurch ihre Wirkung.",
 "Einzelne Momente gelingen, doch Schnitt, Choreografie oder räumliche Orientierung stören häufig.",
 "Die Inszenierung erfüllt ihren Zweck und bietet einige starke Bilder, bleibt aber selten besonders einprägsam.",
 "Bewegung, Kameraführung und Schnitt greifen gut ineinander; zentrale Szenen besitzen Energie und Klarheit.",
 "Außergewöhnlich kreative und präzise Inszenierung macht jede Schlüsselszene visuell und dramaturgisch eindrucksvoll."),
 "Regie & Inszenierung" to listOf(
 "Die Regie findet keinen klaren Ausdruck. Bildgestaltung und Szenenaufbau erschweren das Verständnis.",
 "Einzelne gute Einfälle werden durch unstimmige Übergänge oder wenig wirkungsvolle Szenen geschwächt.",
 "Die Regie erzählt verständlich, setzt jedoch nur vereinzelt besondere Akzente.",
 "Bildsprache, Rhythmus und Szenenführung unterstützen die Geschichte überzeugend.",
 "Jede gestalterische Entscheidung verstärkt Figuren, Stimmung und Erzählung; die Folge besitzt eine unverwechselbare Handschrift."),
 "Visuelle Qualität & Effekte" to listOf(
 "Effekte, Ausstattung oder Bildgestaltung wirken häufig unfertig und reißen aus der Geschichte heraus.",
 "Die visuelle Umsetzung ist wechselhaft; auffällige Schwächen mindern die Glaubwürdigkeit wichtiger Szenen.",
 "Die Bilder erfüllen ihren Zweck, auch wenn einige Effekte oder Kulissen wenig überzeugend wirken.",
 "Stimmige Bildgestaltung und überwiegend hochwertige Effekte unterstützen Welt und Atmosphäre.",
 "Eine außergewöhnlich überzeugende visuelle Welt, in der Kamera, Ausstattung und Effekte nahtlos zusammenwirken."),
 "Musik & Sounddesign" to listOf(
 "Musik und Geräusche passen selten zum Geschehen oder beeinträchtigen sogar wichtige Momente.",
 "Der Ton erfüllt grundlegende Aufgaben, doch Musik und Soundeffekte wirken oft beliebig oder aufdringlich.",
 "Musik und Klang unterstützen die Handlung solide, ohne besonders aufzufallen.",
 "Ein passender Soundtrack und differenzierte Klanggestaltung verstärken Spannung, Emotionen und Atmosphäre.",
 "Musik und Sounddesign erzählen spürbar mit und machen entscheidende Momente unverwechselbar."),
 "Emotionale Wirkung & Atmosphäre" to listOf(
 "Die Geschichte erzeugt kaum Gefühle oder eine erkennbare Stimmung; wichtige Momente bleiben wirkungslos.",
 "Atmosphäre und Emotionen blitzen gelegentlich auf, werden aber nicht konsequent aufgebaut.",
 "Einige Szenen berühren oder fesseln, während andere emotional auf Distanz bleiben.",
 "Die Atmosphäre trägt die Geschichte und zahlreiche Momente entfalten eine glaubwürdige emotionale Wirkung.",
 "Eine außergewöhnlich dichte Atmosphäre und tiefgreifende Emotionen wirken noch lange nach dem Ende nach."),
 "Langlebigkeit & Zeitlosigkeit" to listOf(
 "Der Film bietet kaum Gründe für ein erneutes Anschauen und wirkt bereits beim ersten Sehen wenig nachhaltig.",
 "Nur einzelne Szenen bleiben in Erinnerung; ein erneuter Blick verspricht wenig zusätzlichen Gewinn.",
 "Der Film besitzt einige erinnerungswürdige Momente und lässt sich gelegentlich gut wiedersehen.",
 "Figuren, Themen und Inszenierung laden zu wiederholtem Anschauen ein und behalten ihre Wirkung.",
 "Ein Film, der bei jeder Sichtung neue Details offenbart und dessen Themen, Bilder und Momente dauerhaft faszinieren."),
 "Sogwirkung & Dranbleibfaktor" to listOf(
 "Die Staffel weckt kaum Neugier auf weitere Folgen; Entwicklungen und offene Fragen bleiben belanglos.",
 "Die Motivation weiterzuschauen ist unregelmäßig, weil wichtige Handlungsstränge zu wenig Spannung erzeugen.",
 "Die Staffel hält das Interesse insgesamt aufrecht, auch wenn manche Folgen wenig zum Weitersehen anregen.",
 "Spannende Entwicklungen, Figuren und offene Fragen machen es leicht, direkt die nächste Folge zu starten.",
 "Die Staffel entwickelt eine außergewöhnliche Sogwirkung: Jede Folge vertieft die Neugier und steigert die Vorfreude auf die nächste."),
 "Episodenabschluss & Fortsetzungsreiz" to listOf(
 "Die Episode endet ohne überzeugenden Abschluss oder sinnvollen Ausblick. Das Interesse an der Fortsetzung bleibt aus.",
 "Der Abschluss wirkt schwach oder unbefriedigend. Offene Fragen erzeugen nur wenig Neugier auf die nächste Folge.",
 "Das Ende erfüllt seinen Zweck und bietet einen nachvollziehbaren Übergang, ohne einen besonders starken Eindruck zu hinterlassen.",
 "Die Episode findet einen wirkungsvollen Abschluss und weckt durch neue Fragen oder Entwicklungen spürbare Vorfreude.",
 "Ein außergewöhnlich starkes Ende verbindet emotionale Auflösung mit packenden neuen Perspektiven. Man möchte sofort weiterschauen.")
 )
 return descriptions[criterion]?.get(band) ?: listOf(
 "Dieser Aspekt beeinträchtigt die Wirkung der Geschichte erheblich und bleibt deutlich hinter den Erwartungen zurück.",
 "Hier zeigen sich mehrere spürbare Schwächen, die den Gesamteindruck merklich mindern.",
 "Dieser Aspekt funktioniert insgesamt ordentlich, lässt aber noch erkennbares Verbesserungspotenzial.",
 "Dieser Aspekt überzeugt durch eine sorgfältige Umsetzung und bereichert die Geschichte spürbar.",
 "Dieser Aspekt ist außergewöhnlich gelungen und trägt maßgeblich zur Qualität der Geschichte bei."
 )[band]
}
