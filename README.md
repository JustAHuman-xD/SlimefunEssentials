### SlimefunEssentials
Slimefun Essentials is a minecraft mod dedicated to add support for Slimefun4 items and mechanics to other client sided mods.

### What does it do:
- Adds Support for Slimefun Recipes to EMI, REI, JEI
- Adds Support for Custom Textures for Placed Slimefun Blocks (Requires More Block Predicates on the Client and Requires Slimefun Server Essentials on the Server)
- Adds Support for Jade Tooltips for Placed Slimefun Blocks (Requires Jade on the Client and Requires Slimefun Server Essentials on the Server)
- More Coming Soon™

### Downloads
- [Github](https://github.com/JustAHuman-xD/SlimefunEssentials/releases)
- [Modrinth](https://modrinth.com/mod/slimefun-essentials)
- [Curseforge](https://www.curseforge.com/minecraft/mc-mods/slimefun-essentials)

### Dependencies
- Mod Menu (Optional)
- Cloth Config API (Optional)
- More Block Predicates (Optional)
- Jade (Optional)
- One of the following: (Optional)
  - EMI
  - JEI
  - REI

### Configuration:
- "block_features"
  - Should Slimefun Essentials enabled support for Custom Textures & Jade Integration
  - Requires More Block Predicates (For Custom Textures)
  - Requires Jade (For Well Jade Integration)
  - Requires Slimefun Server Essentials on the Server (For Any Features)
- "recipe_features"
  - Should Slimefun Essentials add support for Slimefun Recipes
  - Requires EMI, JEI, or REI
- "auto_toggle_addons"
  - Should Slimefun Essentials attempt to automatically toggle on or off Addons when Joining a Server with Slimefun Server Essentials installed
- "addons"
  - A list of addons to support
  - Items **for all features** will only be supported if the addon they come from is in this list