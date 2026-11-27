# Tests sur PC avant compilation et transfert de l'APK

Le projet se clone dans Android Studio : **Get from VCS** puis `https://github.com/SDINAHET/anti-demarchage_android.git`. Le dépôt est privé : se connecter à GitHub.

## Environnement
- Android Studio récent avec SDK Android 35 et build tools
- JDK 21 pour exécuter Gradle (dans Settings > Build, Execution, Deployment > Build Tools > Gradle)
- Gradle 8.11.1. Si le projet ne possède pas de wrapper, Android Studio propose sa synchronisation ou lancer `gradle` depuis une installation locale.
- Java/Kotlin ciblent JVM 17 pour la compatibilité Android 10 (API 29).

## Ordre recommandé
1. **Tests unitaires sans APK** : `gradle testDebugUnitTest` ou **Run** sur `PrefixFilterTest` et `CallDecisionTest`.
2. **Contrôles statiques** : `gradle lintDebug`.
3. **Test E2E d'interface sur émulateur** : créer un émulateur Android 10 (ou récent) dans Device Manager, lancer `gradle connectedDebugAndroidTest`. Cela installe un APK de test dans l'émulateur mais **ne déclenche jamais de véritable appel**.
4. **Compilation à installer sur le Moto G7 uniquement après réussite** : `gradle assembleDebug`. Fichier `app/build/outputs/apk/debug/app-debug.apk`.

On peut exécuter (tests unitaires + analyse statique) avant de compiler l'APK principal. L'E2E Android nécessite, par nature, de **compiler une version de test pour l'émulateur**, pas de compiler une version définitive pour ton téléphone.

Jest est principalement utilisé pour JavaScript/TypeScript : dans cette application Android Java/Kotlin, **JUnit 4 et AndroidX Test** sont mieux adaptés. Aucun besoin de Node ni Jest.

## Limites de validation
- Les tests JVM vérifient les préfixes, numéros inconnus, exceptions et règles de filtrage.
- Les tests instrumentés vérifient l'ouverture de l'interface et ses composants Android.
- Ils ne prouvent pas qu'Android rejettera avant sonnerie tous les appels sur le Moto G7. Ce comportement exige une recette sur appareil réel, avec appels de test autorisés.
- Aucun test ne doit appeler les numéros d'urgence 15, 17, 18 ou 112.
- Conserver l'icône visible et le rôle de filtrage désactivé lors des premiers essais.
