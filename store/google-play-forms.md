# Google Play Console – formların doldurulması (Psst)

Bu fayl Play Console-da **App content** (Tətbiq məzmunu) bölməsindəki formalara nə cavab veriləcəyini göstərir.
Cavablar tətbiqin hazırkı koduna əsaslanır (24.11.2026). Tətbiqə yeni məlumat növü əlavə olunsa (məsələn səsli mesaj, story, məkan), bu cavablar yenilənməlidir.

Linklər (GitHub Pages aktiv ediləndən sonra):

- Məxfilik siyasəti: `https://ilkinbayramof.github.io/NinjaTalkMultiPlatform/privacy-policy.html`
- Hesabın silinməsi: `https://ilkinbayramof.github.io/NinjaTalkMultiPlatform/delete-account.html`

---

## 1. Privacy policy (Məxfilik siyasəti)

Yuxarıdakı məxfilik siyasəti linkini yapışdır.

## 2. App access (Tətbiqə giriş)

Seç: **All or some functionality is restricted** (giriş tələb olunur).

Google yoxlayıcıları üçün **test hesabı** yarat və məlumatlarını bura yaz:

- Username: test hesabının e-poçtu
- Password: test hesabının şifrəsi
- Qeyd: "Register or sign in with the credentials above. All features are available after sign-in."

> Öz şəxsi hesabını vermə – yalnız bu məqsəd üçün ayrıca hesab aç.

## 3. Ads (Reklamlar)

**No, my app does not contain ads.**

## 4. Content rating (Yaş reytinqi anketi)

- Category: **Social / Communication** (Sosial şəbəkə / ünsiyyət)
- Users can interact or exchange content with each other: **Yes**
- Users can share their location with other users: **No**
- App allows users to purchase digital goods: **Yes**
- User-generated content (profil şəkli, bio, mesajlar): **Yes**
- Violence, sexual content, drugs, gambling (tətbiqin öz məzmununda): **No**

## 5. Target audience (Hədəf auditoriya)

- Target age groups: yalnız **18 and over**
- Appeals to children: **No**

## 6. Data safety (Məlumat təhlükəsizliyi)

### Ümumi suallar

| Sual | Cavab |
|---|---|
| Does your app collect or share any of the required user data types? | **Yes** |
| Is all of the user data collected by your app encrypted in transit? | **Yes** |
| Do you provide a way for users to request that their data is deleted? | **Yes** – hesabın silinməsi linkini əlavə et |

### Toplanan məlumat növləri

Hamısında: **Shared = No** (Firebase və hostinq "xidmət təminatçısı" sayılır, Google-un qaydasına görə bu "paylaşma" deyil), **Ephemeral = No**.

| Kateqoriya → Növ | Collected | Required / Optional | Məqsəd (Purposes) |
|---|---|---|---|
| Personal info → **Email address** | Yes | Required | Account management, App functionality |
| Personal info → **User IDs** | Yes | Required | Account management, App functionality |
| Personal info → **Other info** (cins, doğum tarixi, bio) | Yes | Required | App functionality, Account management |
| Photos and videos → **Photos** (profil şəkli) | Yes | Optional | App functionality |
| Messages → **Other in-app messages** | Yes | Required | App functionality |
| Financial info → **Purchase history** (Premium, Boost) | Yes | Optional | App functionality |
| Device or other IDs → **Device or other IDs** (FCM push tokeni) | Yes | Required | App functionality |

Toplanmayanlar (seçmə): Location, Contacts, Phone number, Name, Address, Audio, Files, Calendar, Health, Web browsing, App activity/analytics, Crash logs.

> ⚠️ Backend-də analitika və ya crash log toplayan bir xidmət varsa (məsələn Sentry), onu da əlavə et.

## 7. Account deletion (Hesabın silinməsi)

- Tətbiqdə silmə: **Yes** (Tənzimləmələr → Hesabı sil)
- Veb link: hesabın silinməsi linkini yapışdır.

## 8. Digər formalar

| Forma | Cavab |
|---|---|
| Government apps | No |
| Financial features | No |
| Health | No |
| News app | No |
| COVID-19 apps | No |
