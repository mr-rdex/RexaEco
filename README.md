# RexaEco

*English | [Türkçe](#türkçe)*

**_RexaEco is a comprehensive economy plugin with modern interfaces and Vault support that will completely change your server's economy balance. Forget boring command-based economy; bank upgrades, a Black Market with a dynamic rarity system, Sell Wands, and much more await your players!_**

## 💰 Core Economy System
- Full Vault API compatibility.
- Modern, colorful, and fully customizable messages.
- Asynchronous database operations for lag-free performance.
- PlaceholderAPI support.

## 🏦 Advanced Bank System
![Bank](https://cdn.modrinth.com/data/tAZCPfnZ/images/29a649f9e48332802d02d4221512722dd2aaeabb.gif)

- Fully GUI (Menu) based bank management.
- Bank Upgrades: (Iron Account, Gold Account, etc.) Increasing money capacity and interest rates based on level.

![Bank Upgrades](https://cdn.modrinth.com/data/tAZCPfnZ/images/e91e92615d4f425d2b1ab9ce7957e84e7f5f7903.gif)

- Dynamic Interest System: Players can withdraw manually or have it automatically deposited to their cash account.
- Comprehensive transaction history (deposit, withdraw, interest records).

## 🎭 Black Market
![Black Market](https://cdn.modrinth.com/data/tAZCPfnZ/images/4ad15e16b4ace2e32769e260c1c667e87e302001.gif)
![Black Market Editor](https://cdn.modrinth.com/data/tAZCPfnZ/images/de4cc1a7fc57613bb7789088d6e8f45724c8f7fd.gif)

- Special market that refreshes with random items at designated intervals (e.g., every 12 hours).
- Item rarities (COMMON, RARE, EPIC, LEGENDARY) and showcase designs based on these rarities.
- Limited stock system (Out of stock items cannot be bought).
- In-Game Editor: Manage items via menu with `/blackmarket itemmanager` or instantly add the custom item in your hand to the market with `/blackmarket additem`!

## 🛒 Shop System
![Shop](https://cdn.modrinth.com/data/tAZCPfnZ/images/6e1f2234449b7f332d83d68086acab2bffbc48cb_350.webp)
![Shop Menu](https://cdn.modrinth.com/data/tAZCPfnZ/images/139ceb175928235e89cacb2d806ffa772216a6db.gif)

- Category-based advanced GUI shop.
- Buy/sell by specifying amount (left/right click) or directly as a full stack.
- Set buy and sell prices for each item.

## ⚔️ Other Features
- Banknotes & XP Bottling: Players can withdraw their money or convert their XP to physical bottles with a tax deduction.

![Withdraw](https://cdn.modrinth.com/data/tAZCPfnZ/images/7f0b1bd45b5f37083b99da4104e93de6427d5f0f.gif)

- SellWand: Instantly sell all valuable items in chests at market price by left-clicking.

## 📋 Requirements
- **Minecraft:** 1.21+
- **Server Software:** Paper (Recommended), Spigot, or Bukkit
- **Java:** 21+
- **Required Plugin:** Vault
- **Optional:** PlaceholderAPI

## 🧾 Commands

### 🔹 Player Commands
- `/balance` or `/money [player]` — View your own or someone else's balance.
- `/pay <player> <amount>` — Send money to another player.
- `/bank` — Open the bank menu.
- `/shop` or `/rshop` — Open the main shop menu.
- `/blackmarket` — Visit the Black Market (If open).
- `/withdraw <amount>` — Convert your money into a physical banknote.
- `/xpextract <amount>` — Convert your XP into a physical bottle.
- `/sell` — Sell items in your hand or inventory.
- `/balancetop` — View the richest players on the server.
- `/gamble` — Chance games menu.

### 🛠 Admin Commands
- `/admin give/take/set <player> <amount>` — Manage player balance.
- `/sellwand give <player>` — Give a sell wand to a player.
- `/blackmarket refresh` — Refresh black market items and stocks.
- `/blackmarket settime <hours>` — Set the black market refresh time.
- `/blackmarket forcestart / forcestop` — Instantly open or close the market.
- `/blackmarket additem <price> <stock> <rarity>` — Add the item in your hand to the black market.
- `/blackmarket itemmanager` — Open the black market visual editor.
- `/rexaeco reload` — Reload all configuration (config/menu) files.

## 🔐 Permissions

### 🔹 Player Permissions
- `rexaeco.balance` — View own balance
- `rexaeco.pay` — Send money
- `rexaeco.withdraw` & `rexaeco.xpextract` — Create banknote/XP bottle
- `rexaeco.sell` — Use quick sell menu
- `rexaeco.blackmarket.use` — Use Black Market
- `rexaeco.killcoins.bypass.limit` — Bypass daily KillCoin limit

### 🛠 Admin Permissions
- `rexaeco.admin` — Full access to all admin commands
- `rexaeco.admin.sellwand` — Give sell wands
- `rexagon.admin` — Access to black market management commands (OPs have automatic access)
- `rexaeco.admin.reload` — Reload the plugin

## ⚙️ Configuration
- All systems are fully YAML-based and can be updated instantly without restarting the server (`/rexaeco reload`).
- **Menus (`menus/`):** Customize the interface (glass colors, button locations, titles) of all menus such as Shop, Bank, Black Market, Editor, and Transaction History.
- **Language (`messages_en.yml`):** Change all messages in the plugin, including the prefix, to any color and format you want.
- **Economy (`prices.yml` & `bankUpgrades.yml`):** Fully customize shop prices, bank level prices, capacities, and interest cooldowns.

---

## Türkçe

*[English](#rexaeco) | Türkçe*

**_RexaEco, sunucunuzun ekonomi dengesini tamamen değiştirecek, modern arayüzlere sahip ve Vault destekli kapsamlı bir ekonomi eklentisidir. Sıkıcı komut tabanlı ekonomiyi unutun; banka yükseltmeleri, dinamik nadirlik sistemli Kara Borsa, Satış Çubukları ve çok daha fazlası oyuncularınızı bekliyor!_**

## 💰 Temel Ekonomi Sistemi
- Tam Vault API uyumluluğu.
- Modern, renkli ve tamamen kişiselleştirilebilir mesajlar (`messages_tr.yml`).
- Asenkron veritabanı işlemleri ile sunucuyu yormayan performans.
- PlaceholderAPI desteği.

## 🏦 Gelişmiş Banka Sistemi
![Banka](https://cdn.modrinth.com/data/tAZCPfnZ/images/29a649f9e48332802d02d4221512722dd2aaeabb.gif)

- Tamamen GUI (Menü) tabanlı banka yönetimi.
- Banka Yükseltmeleri: (Demir Hesap, Altın Hesap vb.) Seviyeye göre artan para kapasitesi ve faiz oranları.

![Banka Yükseltme](https://cdn.modrinth.com/data/tAZCPfnZ/images/e91e92615d4f425d2b1ab9ce7957e84e7f5f7903.gif)

- Dinamik Faiz Sistemi: İster oyuncu manuel olarak çeksin, ister zamanı geldiğinde otomatik olarak nakit hesabına yatsın.
- Kapsamlı işlem geçmişi (yatırma, çekme, faiz kaydı).

## 🎭 Kara Borsa (Black Market)
![Kara Borsa](https://cdn.modrinth.com/data/tAZCPfnZ/images/4ad15e16b4ace2e32769e260c1c667e87e302001.gif)
![Kara Borsa Düzenleyici](https://cdn.modrinth.com/data/tAZCPfnZ/images/de4cc1a7fc57613bb7789088d6e8f45724c8f7fd.gif)

- Belirlediğiniz sürelerde (Örn: 12 saatte bir) rastgele eşyalarla yenilenen özel pazar.
- Eşya nadirlikleri (COMMON, RARE, EPIC, LEGENDARY) ve bu nadirliklere göre vitrin tasarımı.
- Sınırlı stok sistemi (Tükenen eşyalar alınamaz).
- Oyun İçi Editör: `/blackmarket itemmanager` ile menü üzerinden eşyaları yönetin veya `/blackmarket additem` ile elinizdeki özel eşyayı anında borsaya ekleyin!

## 🛒 Market Sistemi
![Market](https://cdn.modrinth.com/data/tAZCPfnZ/images/6e1f2234449b7f332d83d68086acab2bffbc48cb_350.webp)
![Market Menu](https://cdn.modrinth.com/data/tAZCPfnZ/images/139ceb175928235e89cacb2d806ffa772216a6db.gif)

- Kategori bazlı gelişmiş GUI market.
- Adet belirleyerek (sol/sağ tık) veya direkt tam paket (Stack) olarak alım/satım imkanı.
- Her eşya için alış ve satış fiyatı belirleme.

## ⚔️ Diğer Özellikler
- Çek (Para Çekme) & XP Şişeleme: Oyuncular vergi kesintisiyle paralarını çeke veya XP'lerini şişeye dönüştürebilir.

![Para Çek](https://cdn.modrinth.com/data/tAZCPfnZ/images/7f0b1bd45b5f37083b99da4104e93de6427d5f0f.gif)

- SellWand (Satış Çubuğu): Sandıklara sol tıklayarak içindeki tüm değerli eşyaları anında market fiyatından satma.

## 📋 Gereksinimler
- **Minecraft:** 1.21+
- **Sunucu Yazılımı:** Paper (Önerilen) veya Spigot, Bukkit
- **Java:** 21+
- **Zorunlu Eklenti:** Vault
- **İsteğe Bağlı:** PlaceholderAPI

## 🧾 Komutlar

### 🔹 Oyuncu Komutları
- `/money` veya `/bakiye [oyuncu]` — Kendi bakiyeni veya başkasının bakiyesini gör.
- `/pay <oyuncu> <miktar>` — Başka bir oyuncuya para gönder.
- `/bank` — Banka menüsünü aç.
- `/market` veya `/rshop` — Ana market menüsünü aç.
- `/blackmarket` — Kara Borsa'yı ziyaret et (Açıksa).
- `/paracek <miktar>` — Paranı fiziksel bir çeke dönüştür.
- `/xpcek <miktar>` — XP'ni fiziksel bir şişeye dönüştür.
- `/sell` — Elindeki veya envanterindeki eşyaları sat.
- `/balancetop` — Sunucunun en zenginlerini gör.
- `/gamble` — Şans oyunları menüsü.

### 🛠 Yönetici (Admin) Komutları
- `/admin give/take/set <oyuncu> <miktar>` — Oyuncu bakiyesini yönet.
- `/sellwand give <oyuncu>` — Oyuncuya satış çubuğu ver.
- `/blackmarket refresh` — Kara borsa eşyalarını ve stoklarını yeniler.
- `/blackmarket settime <saat>` — Kara borsa yenilenme süresini ayarlar.
- `/blackmarket forcestart / forcestop` — Marketi anında açar veya kapatır.
- `/blackmarket additem <fiyat> <stok> <nadirlik>` — Elindeki eşyayı kara borsaya ekler.
- `/blackmarket itemmanager` — Kara borsa görsel editörünü açar.
- `/rexaeco reload` — Tüm yapılandırma (config/menu) dosyalarını yeniler.

## 🔐 Yetkiler (Permissions)

### 🔹 Oyuncu Yetkileri
- `rexaeco.balance` — Kendi bakiyesini görme
- `rexaeco.pay` — Para gönderme
- `rexaeco.xpcek` & `rexaeco.paracek` — Çek/Şişe oluşturma
- `rexaeco.sell` — Hızlı satış menüsünü kullanma
- `rexaeco.blackmarket.use` — Kara Borsa'yı kullanma
- `rexaeco.killcoins.bypass.limit` — KillCoin günlük limitine takılmama

### 🛠 Yönetici Yetkileri
- `rexaeco.admin` — Tüm admin komutlarına tam erişim
- `rexaeco.admin.sellwand` — Satış çubuğu verme
- `rexagon.admin` — Kara borsa yönetim komutlarına erişim (OP olanlar otomatik erişir)
- `rexaeco.admin.reload` — Eklentiyi yenileme

## ⚙️ Yapılandırma (Configuration)
- Tüm sistemler tamamen YAML tabanlıdır ve sunucu yeniden başlatılmadan (`/rexaeco reload`) anında güncellenebilir.
- **Menüler (`menus/`):** Market, Banka, Kara Borsa, Editör, İşlem geçmişi gibi tüm menülerin arayüzünü (cam renkleri, buton yerleri, başlıklar) buradan ayarlayabilirsiniz.
- **Dil (`messages_tr.yml`):** Prefix dahil eklentideki bütün mesajları istediğiniz renkte ve formatta değiştirebilirsiniz.
- **Ekonomi (`prices.yml` & `bankUpgrades.yml`):** Market fiyatlarını, banka seviyelerinin fiyatlarını, kapasitelerini ve faiz bekleme sürelerini tamamen özelleştirebilirsiniz.
