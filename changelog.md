### Version 1.4.0:

- Removed the following configs. These configs were needed to change the spawn values of Fluids Cows, but now we can change the biome modifier directly, no need to external config.
  - **Config::SPAWN_MAX_COUNT**
  - **Config::SPAWN_MIN_COUNT**
  - **Config::SPAWN_WEIGHT**
- Remove **temporarily** custom buckets for fluids that don't have an in-game bucket like milk. This means that *bucket-less* cows don't spawn for now.
- Add tag with allowed biomes that FluidCows can spawn.
- QOL - Only consume bucket on survival mode. 
- Fix **/moofluid find** command not printing the right coordinates and not giving the click to tp.