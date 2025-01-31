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
