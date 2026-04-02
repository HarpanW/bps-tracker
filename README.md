# BPS Tracker
A lightweight fabric mod that tracks broken Blocks Per Second (BPS).

- The first 5 block breaks are ignored to serve as a buffer (to for example reach full speed). 
- The bps calculation is initiated on the tick of the 6th break. 
- The tick of the last/latest break is used for the bps calculation.

>Any block that is insta-broken is tracked by detecting client side 
packets. Since this mod is intended for hypixel skyblock farming,
`ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK` sent on cactus is 
also tracked.

# Requirements
- Fabric loader
- Fabric API 1.21.6+

# Commands

| Command    |       Details        |
|------------|:--------------------:|
| /bps-reset | Resets the bps stats |
| /bps-show  |    Shows the HUD     |
| /bps-hide  |    Hides the HUD     |

# Installing
Download the appropriate .jar file in the releases section or alternatively compile it yourself using `.\gradlew build`. 
Put the .jar along with fabric API in your minecraft mods folder. 