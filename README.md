# Tekoälypohjainen Luonnollisen Kielen Kääntäjä

Tämä sovellus on Java-pohjainen tekoälykääntäjä, joka hyödyntää OpenAI:n API:a kielenkääntämiseen. Sovellus tarjoaa komentorivityökalun, jonka avulla käyttäjät voivat kääntää tekstiä eri kielten välillä hyödyntäen kehittyneitä kielimalleja.

## Ominaisuudet

- Tekstin kääntäminen määriteltyjen kielten välillä
- Automaattinen kielen tunnistus
- Toimialakohtaisen terminologian huomioiminen
- Optimoidut, tarkat käännökset prompt engineering -tekniikoilla
- Helppokäyttöinen komentorivityökalu

## Tekniset vaatimukset

- Java 17 tai uudempi
- Maven
- Toimiva internet-yhteys OpenAI API -kutsuille
- OpenAI API -avain

## Asennus ja käyttöönotto

### 1. Kloonaa projekti

```bash
git clone [projektin-URL]
cd chatgptconnector
```

### 2. OpenAI API -avaimen hankkiminen ja asentaminen

#### API-avaimen hankkiminen:

1. Luo tili OpenAI:n sivuilla: https://platform.openai.com/signup
2. Kirjaudu sisään juuri luomallesi tilille
3. Siirry API-avaimet osioon: https://platform.openai.com/api-keys
4. Napsauta "Create new secret key" -painiketta
5. Anna avaimelle kuvaava nimi (esim. "AI-Translator")
6. Kopioi luotu API-avain talteen - tätä avainta näytetään vain kerran!

#### API-avaimen konfigurointi sovellukseen:

**Vaihtoehto 1: Määrittely application.properties -tiedostossa**
1. Avaa tiedosto `src/main/resources/application.properties`
2. Etsi rivi, jossa lukee `openai.api.key=your_openai_api_key_here`
3. Korvaa `your_openai_api_key_here` juuri luomallasi API-avaimella
4. Tallenna tiedosto

**Vaihtoehto 2: Määrittely ympäristömuuttujana**
1. Aseta ympäristömuuttuja `OPENAI_API_KEY` komentorivillä:

   Linux/macOS:
   ```bash
   export OPENAI_API_KEY=your_api_key_here
   ```
   
   Windows:
   ```cmd
   set OPENAI_API_KEY=your_api_key_here
   ```

**Vaihtoehto 3: Määrittely Java-parametrina**
1. Välitä API-avain Java-parametrina sovelluksen käynnistyksessä:
   ```bash
   java -Dopenai.api.key=your_api_key_here -jar target/chatgptconnector-1.0.0.jar
   ```

### 3. Sovelluksen kääntäminen

Käännä sovellus Maven-työkalulla:

```bash
mvn clean package
```

### 4. Sovelluksen käynnistäminen

**Maven-työkalulla:**
```bash
mvn spring-boot:run
```

**Suoraan JAR-tiedostona:**
```bash
java -jar target/chatgptconnector-1.0.0.jar
```

## Käyttöohjeet

Sovelluksen käynnistyttyä seuraa komentorivin ohjeita:

1. Valitse käännöstoiminto valikosta:
   ```
   Valitse toiminto:
   1. Käännä teksti (määrittele lähdekieli)
   2. Käännä teksti (automaattinen kielen tunnistus)
   3. Lopeta
   
   Valintasi (1-3):
   ```

2. Valitsemalla vaihtoehdon 1, voit määritellä sekä lähde- että kohdekielen:
   ```
   Syötä lähdekieli (esim. suomi, englanti): suomi
   Syötä kohdekieli (esim. suomi, englanti): englanti
   Syötä toimiala (valinnainen, paina Enter ohittaaksesi):
   
   Syötä käännettävä teksti (tyhjä rivi lopettaa):
   ```

3. Valitsemalla vaihtoehdon 2, sovellus tunnistaa kielen automaattisesti:
   ```
   Syötä kohdekieli (esim. suomi, englanti): englanti
   
   Syötä käännettävä teksti (tyhjä rivi lopettaa):
   ```

4. Kirjoita käännettävä teksti ja paina tyhjää riviä (Enter) kun olet valmis.

5. Sovellus näyttää käännöksen:
   ```
   Käännös valmis (0.88 s):
   ---------------------------------------------
   [Käännetty teksti]
   ---------------------------------------------
   ```

## Kansiorakenne

```
chatgptconnector/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── com/
│   │       └── vitec/
│   │           └── translator/
│   │               ├── TranslatorApp.java
│   │               │
│   │               ├── service/
│   │               │   └── TranslationService.java
│   │               │
│   │               └── model/
│   │                   ├── ChatCompletionMessage.java
│   │                   ├── ChatCompletionRequest.java
│   │                   ├── ChatCompletionResponse.java
│   │                   └── MessageRole.java
│   │
│   └── resources/
│       └── application.properties
│
├── pom.xml
└── README.md
```

## Huomioitavaa API-avaimen käytöstä

- **Turvallisuus**: Älä koskaan jaa API-avaintasi julkisesti tai sisällytä sitä versionhallintaan.
- **Kustannukset**: OpenAI API:n käyttö on maksullista. Tarkista hinnoittelu ja käyttörajoitukset osoitteesta: https://openai.com/pricing
- **Käyttörajoitukset**: Uusilla API-avaimilla voi olla käyttörajoituksia. Jos kohtaat virheitä, tarkista OpenAI:n käyttäjäpaneelista rajoituksesi.

## Jatkokehitysideat

- Graafisen käyttöliittymän (GUI) lisääminen JavaFX:llä
- Käännösmuistin toteuttaminen aiemmin käännettyjen tekstien hyödyntämiseksi
- Tuki useammille käännöspalveluille (DeepL, Google Translate)
- Toimialakohtaisten termitietokantojen integrointi
- Batch-kääntäminen tiedostoista

## Vianetsintä

**Yleisiä ongelmia ja ratkaisuja:**

1. **"Invalid API key" -virhe:**
   - Tarkista, että API-avain on syötetty oikein
   - Varmista, että API-avaimella on riittävät käyttöoikeudet

2. **Käännös epäonnistuu yhteysvirheen vuoksi:**
   - Tarkista internet-yhteytesi
   - Varmista, että palomuurisi sallii yhteydet OpenAI:n API-palvelimiin

3. **Sovellus ei käynnisty Java-virheillä:**
   - Varmista, että käytät Java 17:ää tai uudempaa
   - Tarkista, että Maven-riippuvuudet on asennettu oikein: `mvn clean install`



---

© WinterIt, 2025