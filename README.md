# SCORIUM

Native Android-App (Kotlin / Jetpack Compose) für persönliche Film- und Serienbewertungen.

## Implementierter Entwicklungsstand
- Dunkles Farbschema mit Goldakzenten
- Offline-Starterkatalog mit MCU-Filmen und Serien; noch unvollständig
- Suche und Film-/Serienfilter
- Eigene Filme und Serien lokal hinzufügen und dauerhaft speichern
- 10 Film-, 11 Staffel- und 8 Episodenbewertungskriterien
- Bewertungen 1–10 mit fünf groben Textstufen
- Prozentwert und A–F-Noten mit +/- (vorläufige Schwellen)
- Staffel- und Episodenanzahl je Serie/Staffel lokal gespeichert
- Separat gespeicherte Bewertungen je Film, Staffel und Episode
- Episodendurchschnitt nur bei vollständig bewerteten Episoden
- Wahl zwischen manueller Staffelwertung und Episodendurchschnitt, wenn beides vorhanden ist
- PNG- und PDF-Export für vollständig bewertete Einträge über Android-Dateiauswahl
- GitHub Actions: automatischer Versuch eines Debug-APK-Builds bei Push

## Noch offen / Einschränkungen
- Build und Installation auf einem realen Android-Gerät **noch nicht bestätigt**
- Vollständiger MCU-Katalog mit Staffel- und Episodenmetadaten
- Hochwertige, rechtlich nutzbare Poster und 2,5D-Animationen
- Kriterien-spezifische ausführliche Erläuterungen der Bewertungsstufen
- Poliertes Premium-Design, Sortierung, Statistiken, Datenmigration, Tests
- Serien-Durchschnitt über Staffeln noch nicht als eigene Detailansicht
- Manuell hinzugefügte Staffeln/Episoden haben bisher nur Nummern, keine Titel

## APK
Auf der GitHub-Registerkarte **Actions** den neuesten Workflow **Android APK** öffnen. Nur wenn dieser erfolgreich abgeschlossen ist, steht das Artefakt `Scorium-debug-apk` zur Verfügung.

Projekt nutzt JDK 17, Android SDK 35 und Gradle 8.11.1. Es benötigt keine Online-Metadaten-API, kein Konto und keine Netzwerkberechtigung.
