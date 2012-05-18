Minecraft-Bukkit-Buoy
=====================

Ziel
----

Ziel des Plugins ist es Bojen der Schiffahrt nach zu empfinden.
Es sollen Schiffahrtswege definierbar sein. Es soll ohne Kommandos im Chat funktionieren.

Bojen
-----

Eine Boje ist ein Block Wolle aus roter oder grüner Farbe. Unter diesem Block muss sich Wasser befinden.
Ein Bojenpaar kann gebildet werden durch eine rote und eine grüne Boje welche eine direkte Verbindung durch Wasser besitzen.
Ein Bojenpaar muss aktiviert werden, damit es für den Schifffahrtsweg nutzbar ist.
Die AKtivierung erfolgt durch schlagen mit einer Schaufel auf eine der Bojen.

Zu einem aktivierten Bojenpaar kann man mit einem Boot fahren.
Dazu schlägt man mit einer Schaufel in die Richtung des Bojenpaares.
Dabei muss man in einem Boot sitzen und man muss hoch genug in die Luft schlagen.
Gefahren wird an die rote oder grüne Boje, je nach dem in welche Richtung man eher geschlagen hat.

Schifffahrtswege
----------------

Man kann Bojenpaare verbinden. Dazu stellt man sich auf eine Boje und schlägt mit einer Schaufel
in die Richtung der gleichfarbigen Boje des Zielbojenpaares. Es muss zwischen den Bojen eine direkte Wasserverbindung geben.
Eine Verbindung ist richtungsabhängig. Steht man auf der roten Boje und schlägt richtung der anderen roten Boje,
so wird die rote Strecke entsprechend verbunden. Die grüne Seite ist noch nicht verbunden.
Man kann so wechselseitige Wege definieren. Man stellt sich dazu auf die grüne Boje und schlägt in die
entgegengesetzt Richtung der anderen grünen Boje.
Entsprechend kann man auch zweigleisige Einbahnstraßen definieren. Dazu verbindet man rote und grüne Bojen
immer in die gleiche Richtung.

Wird nun auf eine Boje zu gefahren in dem man mit einer Schaufel in deren Richtung in die Luft haut (Rechtsclick) und man in einem
Boot sitzt, so wird mit der nächsten Bojen weitergemacht. Man kann stoppen, indem man das Boot verläßt,
wobei das Boot dann weiter fährt, oder durch erneutes Schlagen in die Luft.
Ist das Ende erreicht (es gibt keine weiterführende Verbindung) wird gestoppt.
Erreicht man eine Gabelung wird ebenfalls gestoppt.

Eine Gabelung kann definiert werden, in dem man für eine Boje mehrere Zielbojen definiert.

Schaubild
---------


Hin- und Rückrichtung

R---->R---->R

G<----G<----G


Einbahnstraße

R---->R---->R

G---->G---->G


Korrekturen
-----------

Zieldefinitionen der Bojen können gelöscht werden. Dazu geht man in den Schleichenmodus (Sneak, Umschalttaste)
und schlägt (Rechtsclick) auf den Wolleblock. 

Kommandos
---------

* buoy_list   listet alle Bojenpaare der aktuellen Welt auf
* buoy_remove entfernt die Definition der Bojenpaare der aktuellen Welt des Spielers

Trivia
------

Wird die rote oder grüne Wolle entfernt, so wird auch die Definition der Boje zerstört. 
