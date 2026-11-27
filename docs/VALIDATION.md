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
