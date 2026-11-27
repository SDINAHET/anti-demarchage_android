# Dinahet AntiDémarchage — stratégie qualité V2

## Exécuter localement

Depuis PowerShell à la racine du dépôt :

```powershell
& "C:\Gradle\gradle-8.11.1\bin\gradle.bat" clean testDebugUnitTest lintDebug assembleDebug
$env:ANDROID_SERIAL = "emulator-5554"
try {
  & "C:\Gradle\gradle-8.11.1\bin\gradle.bat" connectedDebugAndroidTest
} finally {
  Remove-Item Env:\ANDROID_SERIAL -ErrorAction SilentlyContinue
}
```

Rapports : `app/build/reports/tests/testDebugUnitTest/index.html`, `app/build/reports/lint-results-debug.html`, `app/build/reports/androidTests/connected/index.html`.

## Tests automatisés présents

- `PrefixFilterTest` : 4 méthodes, formats internationaux, 22 préfixes, numéros ordinaires, malformés et urgences.
- `CallDecisionTest` : 4 méthodes, ancienne logique isolée de test (ne remplace pas les tests du service Android).
- `ScreeningPolicyTest` : 8 méthodes, décision pure désormais utilisée en production.
- `PreferencesIntegrationTest` : 3 méthodes instrumentées, préférences, interrupteurs et compteur.
- `ScreeningUiIntegrationTest` : 3 méthodes instrumentées, affichage, restauration d'état et groupes.
- `MainActivityE2ETest` : 1 test de lancement instrumenté (smoke test).

Total : 23 méthodes. Les tests instrumentés ne réalisent **aucun appel réel**.

## Couverture restant à développer avant une publication stable

| Domaine | Scénario | Nature |
|---|---|---|
| Service Android | Appels entrants autorisés/rejetés, exception fail-open, réponse dans les délais | Intégration instrumentée avec doubles de test |
| Journal d'appels | Réponse `skipCallLog=false` puis journal visible sur MIUI | Test sur téléphone réel |
| Notifications | Canal sans son/vibration, permission refusée, numéro affiché, notification désactivée | Intégration instrumentée |
| Compteur | Un incrément par appel bloqué, aucun sur appel autorisé | Intégration instrumentée |
| Rôle de filtrage | Accord, refus, révocation, réouverture | E2E et manuel |
| UI | Bascules des 22 préfixes, réglages restaurés après relance du processus | Instrumentation |
| Accessibilité | TalkBack, grandes polices, contraste, cibles tactiles | Audit manuel et automatisé |
| Compatibilité | Moto G7 Android 10, Xiaomi MIUI, émulateur Android 17 | Matrice appareils |
| Performance | Délai de réponse au screening, démarrage à froid, mémoire | Benchmark sur appareil |
| Robustesse | Numéro privé, URI absente, permissions refusées, appels répétés | Intégration |
| Sécurité | Aucune permission excessive, données locales, absence de fuite du numéro dans les logs | Audit |
| Distribution | Signature, mise à jour V1 vers V2, restauration préférences | Test réel |

## Critères de validation

1. Toutes les tâches Gradle ci-dessus réussissent sur le commit testé.
2. Aucun test d'urgence ne compose réellement un numéro d'urgence.
3. Les numéros ordinaires ne sont jamais bloqués par la politique de préfixes.
4. Le service reste fail-open en cas d'erreur.
5. Le journal Android reste demandé avec `setSkipCallLog(false)`.
6. Le rôle de filtrage et les notifications sont vérifiés sur le téléphone réel.
7. Aucun changement de production ni fusion sur `main` avant validation.

**Important :** ce document distingue les tests écrits des tests exécutés et les validations réelles encore nécessaires.
