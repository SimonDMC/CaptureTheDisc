# CaptureTheDisc
A minigame inspired by Hypixel's Capture the Flag.

## Usage
To set the game up, use `/pastemap`, which should teleport all players
to the correct world. Start a game with `/startctd`, end a game by 
capturing a disc. If you need to terminate the game for whatever 
reason, use `/forcestopctd`.

## Performance Metrics
To get updates on performance, use `/togglectdperformance`.
The performance log shows how long each runnable takes to execute.

## Event Core
As this game was made for an event, it works with the EventCore plugin,
however it also works without it. If EventCore is installed, set up
the game with `/event setup capture_the_disc`, and start it with
`/event start capture_the_disc`.