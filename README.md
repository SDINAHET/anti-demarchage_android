# AntiDémarchage Android

Application Java Android 10+, filtrage local des 12 préfixes métropolitains, notifications silencieuses, compteur et raccourci masquable. Ne remplace pas le téléphone par défaut.

## Installation
Dans Actions > Build APK > exécution réussie > Artifacts, télécharger AntiDemarchage-debug, extraire le ZIP et installer l’APK. Ouvrir et accorder le rôle de filtrage. Voir docs/VALIDATION.md pour les limites, la recette et la signature.

## Compilation
JDK 17, Gradle 8.11.1, Android SDK 35. `gradle testDebugUnitTest lintDebug assembleDebug`.
