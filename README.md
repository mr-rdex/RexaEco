```bbcode
[b][i]RexaEco is a comprehensive economy plugin with modern interfaces and Vault support that will completely change your server's economy balance. Forget boring command-based economy; bank upgrades, a Black Market with a dynamic rarity system, Sell Wands, and much more await your players![/i][/b]

[b]💰 Core Economy System[/b]
[list]
[*]Full Vault API compatibility.
[*]Modern, colorful, and fully customizable messages.
[*]Asynchronous database operations for lag-free performance.
[*]PlaceholderAPI support.
[/list]

[b]🏦 Advanced Bank System[/b]
[img]https://cdn.modrinth.com/data/tAZCPfnZ/images/29a649f9e48332802d02d4221512722dd2aaeabb.gif[/img]
[list]
[*]Fully GUI (Menu) based bank management.
[*]Bank Upgrades: (Iron Account, Gold Account, etc.) Increasing money capacity and interest rates based on level.
[/list]
[img]https://cdn.modrinth.com/data/tAZCPfnZ/images/e91e92615d4f425d2b1ab9ce7957e84e7f5f7903.gif[/img]
[list]
[*]Dynamic Interest System: Players can withdraw manually or have it automatically deposited to their cash account.
[*]Comprehensive transaction history (deposit, withdraw, interest records).
[/list]

[b]🎭 Black Market[/b]
[img]https://cdn.modrinth.com/data/tAZCPfnZ/images/4ad15e16b4ace2e32769e260c1c667e87e302001.gif[/img]
[img]https://cdn.modrinth.com/data/tAZCPfnZ/images/de4cc1a7fc57613bb7789088d6e8f45724c8f7fd.gif[/img]
[list]
[*]Special market that refreshes with random items at designated intervals (e.g., every 12 hours).
[*]Item rarities (COMMON, RARE, EPIC, LEGENDARY) and showcase designs based on these rarities.
[*]Limited stock system (Out of stock items cannot be bought).
[*]In-Game Editor: Manage items via menu with /blackmarket itemmanager or instantly add the custom item in your hand to the market with /blackmarket additem!
[/list]

[b]🛒 Shop System[/b]
[img]https://cdn.modrinth.com/data/tAZCPfnZ/images/6e1f2234449b7f332d83d68086acab2bffbc48cb_350.webp[/img]
[img]https://cdn.modrinth.com/data/tAZCPfnZ/images/139ceb175928235e89cacb2d806ffa772216a6db.gif[/img]
[list]
[*]Category-based advanced GUI shop.
[*]Buy/sell by specifying amount (left/right click) or directly as a full stack.
[*]Set buy and sell prices for each item.
[/list]

[b]⚔️ Other Features[/b]
[list]
[*]Banknotes & XP Bottling: Players can withdraw their money or convert their XP to physical bottles with a tax deduction.
[/list]
[img]https://cdn.modrinth.com/data/tAZCPfnZ/images/7f0b1bd45b5f37083b99da4104e93de6427d5f0f.gif[/img]
[list]
[*]SellWand: Instantly sell all valuable items in chests at market price by left-clicking.
[/list]

[b]📋 Requirements[/b]
[list]
[*]Minecraft: 1.21+
[*]Server Software: Paper (Recommended), Spigot, or Bukkit
[*]Java: 21+
[*]Required Plugin: Vault
[*]Optional: PlaceholderAPI
[/list]

[b]🧾 Commands[/b]

[b]🔹 Player Commands[/b]
[list]
[*]/balance or /money [player] — View your own or someone else's balance.
[*]/pay <player> <amount> — Send money to another player.
[*]/bank — Open the bank menu.
[*]/shop or /rshop — Open the main shop menu.
[*]/blackmarket — Visit the Black Market (If open).
[*]/withdraw <amount> — Convert your money into a physical banknote.
[*]/xpextract <amount> — Convert your XP into a physical bottle.
[*]/sell — Sell items in your hand or inventory.
[*]/balancetop — View the richest players on the server.
[*]/gamble — Chance games menu.
[/list]

[b]🛠 Admin Commands[/b]
[list]
[*]/admin give/take/set <player> <amount> — Manage player balance.
[*]/sellwand give <player> — Give a sell wand to a player.
[*]/blackmarket refresh — Refresh black market items and stocks.
[*]/blackmarket settime <hours> — Set the black market refresh time.
[*]/blackmarket forcestart / forcestop — Instantly open or close the market.
[*]/blackmarket additem <price> <stock> <rarity> — Add the item in your hand to the black market.
[*]/blackmarket itemmanager — Open the black market visual editor.
[*]/rexaeco reload — Reload all configuration (config/menu) files.
[/list]

[b]🔐 Permissions[/b]

[b]🔹 Player Permissions[/b]
[list]
[*]rexaeco.balance — View own balance
[*]rexaeco.pay — Send money
[*]rexaeco.withdraw & rexaeco.xpextract — Create banknote/XP bottle
[*]rexaeco.sell — Use quick sell menu
[*]rexaeco.blackmarket.use — Use Black Market
[*]rexaeco.killcoins.bypass.limit — Bypass daily KillCoin limit
[/list]

[b]🛠 Admin Permissions[/b]
[list]
[*]rexaeco.admin — Full access to all admin commands
[*]rexaeco.admin.sellwand — Give sell wands
[*]rexagon.admin — Access to black market management commands (OPs have automatic access)
[*]rexaeco.admin.reload — Reload the plugin
[/list]

[b]⚙️ Configuration[/b]
[list]
[*]All systems are fully YAML-based and can be updated instantly without restarting the server (/rexaeco reload).
[*]Menus (menus/): Customize the interface (glass colors, button locations, titles) of all menus such as Shop, Bank, Black Market, Editor, and Transaction History.
[*]Language (messages_en.yml): Change all messages in the plugin, including the prefix, to any color and format you want.
[*]Economy (prices.yml & bankUpgrades.yml): Fully customize shop prices, bank level prices, capacities, and interest cooldowns.
[/list]

```
