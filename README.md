# General Summary
Alex 1.2 is a personal security application that allows sending emergency alerts with evidence (audio/video) and location to predefined contacts via email.

# Application Screens
## 1. MainActivity (Main Screen)
Function: Main screen with panic button
Elements:
Shows current location via GPS
Large panic button
Field for manual alternative location
Button to activate/deactivate alternative location

## 2. CambiarContactoDeEmergencia
Function: Configure emergency contact email
Elements:
Text field to enter contact email
Runs automatically on first installation

## 3. CambiarModoDeContacto
Function: Configure user credentials (their own email)
Elements:
Field for personal email
Field for email password
Sends confirmation email when configuring

## 4. AtestiguarVideo
Function: Video recording as evidence
Elements:
Camera preview
Buttons to record, stop and play
Automatically sends video via email

## 5. AtestiguarAudio
Function: Audio recording as evidence
Elements:
Automatic 60-second recording
Automatically sends audio via email
Automatically returns to main menu

# Navigation Diagram
```
App Start
↓
First installation?
├─ YES → CambiarContactoDeEmergencia → CambiarModoDeContacto → MainActivity
└─ NO → MainActivity (directly)
↓
MainActivity (Main)
├─ Menu → CambiarContactoDeEmergencia
├─ Menu → CambiarModoDeContacto
├─ Panic Button → AtestiguarVideo (if config = video)
└─ Panic Button → AtestiguarAudio (if config = audio)
↓
NOTE: Both return to MainActivity automatically
```

# Main Functions
## Panic System
Central panic button that activates the emergency system
Automatic location detection via GPS
Manual alternative location option if GPS fails
Automatic alert sending with evidence

## Geolocation
Uses Google Maps API to convert coordinates to addresses
Real-time GPS with automatic updates
Fallback to manual location if no GPS
Alerts if GPS is disabled

## Email System
Automatic email sending with attached evidence
Sender credentials configuration
Confirmation email when configuring
Predefined messages with location included

## Evidence Capture
Video: Recording with preview, temporary storage
Audio: Automatic 60-second recording
Automatic attachments in emergency emails

# Stored Data (SharedPreferences)
## 1. "instalado"
SiInstalado: First configuration status
## 2. "ContactoMail"
ContactoEMail: Emergency contact email
## 3. "MiCorreo"
MiCorreoE: User's personal email
## 4. "MiPassword"
MiPasswordE: User's email password
## 5. "Evi"
Evidencia: Type of evidence (video/audio)

# Permisos Requeridos
Internet, GPS, Camera, Microphone, Storage, Phone Calls, SMS
