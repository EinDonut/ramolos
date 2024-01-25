# Ramolos
Ermöglicht das Teilen von RageMode-Chat-Logs um Turniere und Events durchzuführen. Ramolos ist eine verbesserte Version des rm-log-share Projektes.

## Überblick

Dieses Programm liest den Inhalt der letzten Miencraft-Log-Datei in Echtzeit aus und übermittelt Nachrichten welche einem strengen Suchkriterium entsprechen an einen Server.
Auf diese Wweise ist es möglich, den Spielverlauf einer RageMode Runde aufzuzeichnen und die Statistiken einzelner Spieler für diese Runde auszuwerten.
Eingesetzt werden soll diese Funktion zum Beispiel bei Turnieren und anderen Events.
Es ist eigenständig mit dem Projektverantwortlichen zu klären, ob die Verwendung dieses Programmes erforderlich ist.

Zusätzlich wird es in Zukunft möglich sein, neue Statistiken von seinem Spielverlauf aufzuzeichnen und diese privat zu speichern.

Die folgenden Dateien können beobachtet und ausgelesen werden:
|Minecraft Client|Log-Datei|
|----------------|---------|
|Vanilla|.minecraft/logs/latest.log|
|LabyMod|.minecraft/logs/latest.log|
|Badlion|.minecraft/logs/blclient/minecraft/latest.log|
|Lunar|.lunarclient/offline/multiver/logs/latest.log|

## Verwendung

Um das Programm zustarten muss [Java 8 oder höher](https://www.java.com/de/download/manual.jsp) installiert sein.


### Verbindung

Um sich mit unserem Server zu verbinden, müsst ihr vorher Zugangsdaten von einem Event- bzw Turnierveranstalter erhalten haben.

## Datenschutz und Sicherheit

Übermittelt werden nur Textpassagen der Log-Datei, welche im Chat angezeigt wurden und mit dem Präfix `[RageMode]` beginnen. Dies beinhaltet Nachrichten zum Spielablauf und zu Killstreaks.
Dies beinhaltet **nicht** Nachrichten von oder zu anderen Spielern oder Statistiken die über den Chat abgerufen werden. 
An die Serversoftware werden keine weiteren Informationen weitergeleitet, welche nicht zur Verbindungsherstellung benötigt werden.
Gespeichert werden ausschließlich Teile des Inhalts der Chatnachrichten.
