Tesla Coil - Forge 1.20.1 / Java 17

Features:
- Tesla Coil block
- Placing player becomes owner
- Owner UUID persists
- 15-block spherical attack radius
- Owner is immune
- Attacks LivingEntity targets, including mobs and other players
- 100,000 FE internal capacity
- 2,500 FE per strike
- 5,000 FE/t input
- Attacks once every 10 ticks when powered
- 1000 magic damage per strike
- Pickaxe mineable
- Creative tab

IMPORTANT:
This source expects a normal ForgeGradle 1.20.1 environment using Java 17 and Gradle 8.x.
The source ZIP intentionally does not contain Gradle wrapper binaries.

Testing:
The coil starts with 0 FE. Connect a Forge Energy generator/cable to any side.

- Visible animated/jagged electrical arc from coil top terminal to struck target
