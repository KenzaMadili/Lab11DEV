# 📍 LocalisationSmartphone

**Réalisé par : MADILI Kenza**

## Description

Application Android de géolocalisation en temps réel. Elle récupère la position GPS du téléphone (latitude, longitude, altitude, précision) et l'envoie automatiquement vers une base de données MySQL via une API PHP hébergée sur un serveur local XAMPP.

---

## Fonctionnalités

- Détection automatique de la position GPS en temps réel
- Envoi des coordonnées vers un serveur PHP local via requête POST (Volley)
- Affichage de toutes les positions enregistrées en base de données via requête GET
- Identification unique de l'appareil via Android ID
- Interface moderne avec CardView et MaterialButton

---


https://github.com/user-attachments/assets/cfa5d8f0-d3b9-4758-b329-fe2ecef44e0c


## Stack technique

| Couche | Technologie |
|---|---|
| Mobile | Java / Android Studio |
| Réseau | Volley |
| Backend | PHP (XAMPP) |
| Base de données | MySQL (phpMyAdmin) |
| Localisation | LocationManager (GPS_PROVIDER) |

---

## Structure du projet Android
app/
├── manifests/
│   └── AndroidManifest.xml
├── java/com/example/localisationsmartphone/
│   └── MainActivity.java
└── res/
└── layout/
└── activity_main.xml

---

## Structure du serveur PHP
htdocs/localisation/
├── createPosition.php
├── getPositions.php
├── service/
│   └── PositionService.php
├── classe/
│   └── Position.php
├── connexion/
│   └── Connexion.php
└── dao/
└── IDao.php

---

## Installation et configuration

### 1. Serveur PHP
- Installer XAMPP et démarrer Apache + MySQL
- Placer le dossier `localisation/` dans `C:\xampp\htdocs\`
- Créer la base de données via phpMyAdmin :

```sql
CREATE DATABASE localisation;

USE localisation;

CREATE TABLE `position` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    date_position DATETIME NOT NULL,
    imei VARCHAR(255) NOT NULL
);
```

### 2. Application Android
- Ouvrir le projet dans Android Studio
- Modifier l'IP dans `MainActivity.java` :

```java
private String insertUrl = "http://VOTRE_IP/localisation/createPosition.php";
private String getUrl   = "http://VOTRE_IP/localisation/getPositions.php";
```

- Trouver votre IP locale avec `ipconfig` (Windows) dans le terminal
- Ajouter dans `res/xml/network_security_config.xml` :

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">VOTRE_IP</domain>
    </domain-config>
</network-security-config>
```

- Ajouter dans `AndroidManifest.xml` :

```xml
android:networkSecurityConfig="@xml/network_security_config"
```

---

## Utilisation

1. Lancer XAMPP (Apache + MySQL)
2. Installer l'APK sur le téléphone (même réseau WiFi que le PC)
3. Accepter les permissions de localisation
4. L'app envoie automatiquement la position GPS dès qu'elle est détectée
5. Appuyer sur **Afficher les positions** pour voir toutes les entrées en base

---

## Test API avec Postman

**Insérer une position (POST)**
- URL : `http://VOTRE_IP/localisation/createPosition.php`
- Body : `x-www-form-urlencoded`

| Key | Value |
|---|---|
| latitude | 33.5731 |
| longitude | -7.5898 |
| date_position | 2026-04-29 23:00:00 |
| imei | test123 |

**Récupérer les positions (GET)**
- URL : `http://VOTRE_IP/localisation/getPositions.php`

---

## Permissions requises

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## Dépendances

```gradle
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'com.google.android.material:material:1.9.0'
implementation 'com.android.volley:volley:1.2.1'
```
