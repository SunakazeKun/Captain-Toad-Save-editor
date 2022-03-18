# CTSe - Captain Toad Save Editor
A save game editor for all versions of **Captain Toad Treasure Tracker**. This project uses [org.json](https://mvnrepository.com/artifact/org.json/json), [Aurum's Java Utility Library](https://github.com/SunakazeKun/AJUL) and [Apache Ant](https://ant.apache.org/) to build distributables. The fundamental features of this project are 100% finished. However, I'm open to include new localizations for other languages if people can provide them.

<img src="https://aurumsmods.com/res/img/programs/CTSe00.png" width=50%> <img src="https://aurumsmods.com/res/img/programs/CTSe01.png" width=50%>

## Features
- Supports all major game versions:
  - *Wii U v1.0.0*
  - *Wii U v1.1.0* (amiibo Support)
  - *3DS v1.0.0*
  - *Switch v1.0.0*
  - *Switch v1.1.0* (Co-op Mode)
  - *Switch v1.2.0* (Special Episode DLC)
  - *Switch v1.3.0* (VR Mode)
- All outdated save files can be converted to the latest version (*Switch v1.3.0*). This allows you to replay your old save files from Wii U or 3DS on the Switch!
- All level and episode data and scores can be edited. This includes collectibles, coin highscore and more.
- The number of lives and other major game data can be edited.
- Nice preview images and a structured tree view for every ingame level.
- The editor supports multiple languages. As of now, English and German are supported.

## Omissions
- Support for the *Switch eShop Demo* won't be added in any way since it is not a complete game. Converting its level progress is not useful as it would cause major gaps in the game progression.
- Flexible conversion between *all* game versions is incredibly tedious to implement even for such a small game. *Switch v1.3.0* is the latest version and the one that people are most likely to play on.
