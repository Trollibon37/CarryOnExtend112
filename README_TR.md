# CarryOnExtend — 1.12.2 Portu

## Önemli: Neden "taşıma" değil, yeniden yazım oldu?

Elimdeki `carryonextend-forge-1_20_1-1_5.jar` sadece derlenmiş `.class`
dosyalarından oluşuyordu (kaynak kodu yoktu). Ayrıca 1.20.1'deki mod, CarryOn'un
**modern (capability tabanlı)** API'sine bağımlı — taşınan blok/varlık oyuncu
üzerinde bir "capability" olarak tutuluyor. 1.12.2'deki CarryOn (Tschipp,
yüklediğiniz `carryon-1_12_2-1_12_7_23.jar`) bambaşka ve çok daha eski bir
mimariye sahip: taşınan şey, oyuncunun **elindeki ItemStack** içinde
(`ItemTile` / `ItemEntity` sınıfları, NBT ile) tutuluyor.

Bu yüzden bytecode'u inceleyip (decompile ederek) mantığı çıkardım ve aynı
"his"i (fırlatma fiziği, güç ayarı) koruyarak 1.12.2 API'sine göre **sıfırdan**
yazdım. Fırlatma fiziği formülü orijinal moddan birebir alındı:

```
P  = 1 + power * 1.5
vx = bakışYönü.x * 0.8 * P
vy = 0.3 * P
vz = bakışYönü.z * 0.8 * P
```

## Özellikler (bu portta bulunanlar)

- **Shift + Q**: Elinde taşınan blok veya varlığı fırlatır.
- **Shift + Fare tekerleği**: Fırlatma gücünü %0-%100 arası ayarlar (adım
  `scrollStep`, config'den değiştirilebilir).
- Taşınan blok bir Tile Entity içeriyorsa (sandık, fırın vb.), NBT verisi
  korunur ve blok yere düştüğünde eski haliyle yerleşir (vanilla
  `EntityFallingBlock.tileEntityData` mekanizması kullanıldı).
- Taşınan bir varlık (mob) ise, gerçek varlık olarak dünyaya geri döner.

## Bu portta OLMAYANLAR (kapsam dışı bırakıldı)

Orijinal 1.20.1 modundaki şu özellikler bu ilk sürüme dahil edilmedi çünkü
1.12.2'de karşılıkları çok farklı bir alt yapı gerektiriyor:
- TNT fırlatma / kendini yok etme **advancement**'ları (1.12.2'de advancement
  sistemi yok, sadece Achievement/Statistics var — istenirse ayrıca eklenebilir)
- Özel "havada süzülen blok" render efektleri (şimdilik vanilla falling-block
  render'ı kullanılıyor)
- Oyuncu fırlatma (CarryType.PLAYER) özelliği

Bunları da istersen ekleyebiliriz, ayrı bir adım olarak konuşalım.

## Derleme (BUNU BEN YAPAMADIM — sandbox'ımın interneti Forge/Mojang
## maven sunucularına kapalı, bu yüzden projeyi sizin derlemeniz gerekiyor)

1. Bir 1.12.2 Forge MDK indirin (Forge 14.23.5.2860 önerilir):
   https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html
2. Bu klasördeki tüm dosyaları MDK'nin kök dizinine kopyalayın (`build.gradle`
   üzerine yazın, `src` klasörünü birleştirin).
3. `libs` klasörü oluşturup içine yüklediğiniz CarryOn jar'ını koyun ve şu
   isimle yeniden adlandırın:
   ```
   libs/carryon-1.12.2-1.12.3.jar
   ```
4. Terminalde:
   ```
   ./gradlew build
   ```
5. Çıkan jar `build/libs/carryonextend112-1.0.jar` yolunda olacak. Bunu hem
   CarryOn hem de bu modu birlikte mods klasörüne atarak test edin.

Eğer `gradlew` ilk çalıştırmada bağımlılık indirirken hata verirse (bazı ağ
ortamlarında forge maven'ine erişim kısıtlı olabilir), bana hata mesajını
gönderirseniz build.gradle'ı ona göre güncelleyebilirim.

## Test etmemi istediğin bir hata/log olursa

Sunucu/istemci logunu (`logs/latest.log` veya crash-report) paylaşırsan,
üzerinden birlikte debug ederiz — tıpkı önceki Forge/Sinytra çalışmalarımızda
yaptığımız gibi.
