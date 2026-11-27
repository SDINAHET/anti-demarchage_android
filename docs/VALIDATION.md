# Validation et limites
Android 10 minimum. Le rôle CALL_SCREENING est distinct du téléphone par défaut. Sans accès aux contacts, les contacts enregistrés ne sont généralement pas soumis au filtre. Les numéros masqués et usurpés ne sont pas tous détectables. Les préfixes ne prouvent pas qu'un appel est frauduleux.
## Recette sur Moto G7
1. Installer l'APK debug issu de CI, ouvrir et accorder le rôle.
2. Vérifier que le téléphone par défaut reste inchangé.
3. Vérifier un appel ordinaire et un appel de préfixe filtré si disponible.
4. Vérifier notification silencieuse et compteur.
5. Désactiver un préfixe, puis vérifier le comportement.
6. Redémarrer et vérifier le rôle.
7. Tester le masquage uniquement après avoir vérifié les notifications ; celles-ci permettent de rouvrir l'écran. Sinon réinstaller ou réactiver l'alias via ADB.
## Distribution
APK debug pour essais uniquement. Pour mises à jour fiables, configurer une clé de signature release persistante, stockée hors du dépôt. Les clés debug CI peuvent changer entre builds.
## Vie privée
Pas de permission Internet, contacts, SMS ou journal d'appels. Compteur et préférences locaux. Le numéro bloqué est affiché dans la notification privée ; le système peut conserver son journal d'appels.

## Vérifications avant usage quotidien
- Le code du filtre Java a passé 56 vérifications autonomes locales. Cela ne remplace ni les tests Gradle ni les essais Android.
- Attendre une compilation APK réussie et des tests unitaires Android verts.
- Tester d'abord l'application avec le rôle de filtrage désactivé puis activé, et garder l'icône visible.
- Pour vérifier le rejet réel, utiliser une ligne de test autorisée et une plage de préfixes contrôlée; ne jamais simuler un numéro officiel ou d'urgence.
- Tester les appels entrants inconnus, les appels sortants et les appels vers les secours (sans passer d'appel réel aux secours pour un test).
- La protection des rappels de secours ne peut être garantie à 100 % avec un simple filtre par préfixe.
