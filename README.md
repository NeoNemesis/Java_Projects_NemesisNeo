# Träningsaktivitetsvisualiserare

## Om projektet
St8tus4U är en Java-baserad applikation för visualisering och analys av träningsaktiviteter utomhus. Projektet är utvecklat som en del av kurs.

## Funktioner
- **Användarhantering**: Stöd för flera användare med individuella profiler
- **Aktivitetsvisualisering**: 
  - Kartvy med rutt och start/slutpunkter
  - Statistikpanel med distans, tid och tempoinformation
  - Grafisk representation av puls, hastighet och höjddata
- **Datahantering**:
  - Import av träningsdata via CSV-filer
  - Automatisk datalagring
  - Persistent lagring av användardata

## Teknisk överblick
Applikationen är byggd enligt MVC-arkitekturmönstret med tydlig separation mellan:
- **Model**: Hanterar användardata och aktivitetslogik
- **View**: Grafiska komponenter för datavisualisering
- **Controller**: Koordinerar dataflöde och användarinteraktioner

## Installation
1. Klona projektet:
```bash
git clone https://github.com/ditt-användarnamn/Java_Projects_NemesisNeo.git
Baserat på dokumentationen ska jag hjälpa dig skapa en utförlig README för ditt St8tus4U-projekt. Jag skapar först README-innehållet och ger dig sedan instruktioner för licens och utveckling.

2. Öppna projektet i din Java IDE
3. Säkerställ att du har Java Runtime Environment (JRE) installerat
4. Kompilera och kör projektet

## Användning
1. Starta applikationen
2. Välj användare från rullgardinsmenyn
3. Importera aktiviteter via "Lägg till aktivitet"-knappen
4. Utforska dina aktiviteter genom de olika vyerna:
   - Statistikpanel
   - Kartvy
   - Grafer för puls, hastighet och höjddata

## CSV-filformat
Applikationen accepterar CSV-filer med följande format:
```
Datum;Tid;Förfluten tid;Longitud;Latitud;Höjd;Distans;Puls;Hastighet;Kadens
```

## Systemkrav
- Java Runtime Environment (JRE)
- Grafikstöd för kartvisualisering
- Tillräckligt med minne för att hantera stora datamängder

## Projektstruktur
```
.
├── src/
│   ├── model/      # Datamodeller och logik
│   ├── view/       # GUI-komponenter
│   ├── controller/ # Applikationslogik
│   └── util/       # Hjälpklasser
├── userData/       # Användardata
└── README.md
```

```markdown
## Bidra till utvecklingen
1. Forka repositoryt
2. Skapa en feature branch (`git checkout -b feature/AmazingFeature`)
3. Commita dina ändringar (`git commit -m 'Add some AmazingFeature'`)
4. Pusha till branchen (`git push origin feature/AmazingFeature`)
5. Öppna en Pull Request

## Utvecklingsmöjligheter
- Stöd för fler filformat (JSON, XML)
- Förbättrad felhantering för korrupta filer
- Implementering av molnlagring
- Realtidsuppdateringar med Observer-mönster
- Prestandaoptimering för stora datamängder
- Avancerade analysverktyg
- Integration med andra fitness-appar

## Licens
Detta projekt är licensierat under [MIT License](LICENSE) - se LICENSE-filen för detaljer.

## Kontakt
Victor Vilches - victorvilches@protonmail.com
Projektlänk: https://github.com/ditt-användarnamn/Java_Projects_NemesisNeo
```
