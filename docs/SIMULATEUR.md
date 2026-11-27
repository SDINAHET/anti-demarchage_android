# Simulateur fictif intégré

Ouvrir **Dinahet AntiDémarchage**, choisir **Tester un appel fictif**, saisir un numéro ou choisir un exemple.

Le simulateur est écrit en **Kotlin** et appelle la même logique de décision que le service de filtrage. Il respecte l'interrupteur général et les préfixes individuellement activés.

Il n'envoie aucun appel, ne fait pas sonner le téléphone, ne crée aucune notification et ne modifie pas le compteur réel. Le service de filtrage officiel d'Android reste indépendant.

## Exemples
- 01 62 45 78 90 : devrait être rejeté par défaut
- 09 48 32 15 67 : devrait être rejeté par défaut
- 06 12 34 56 78 : autorisé
- 07 12 34 56 78 : autorisé
- 02 97 12 34 56 : autorisé
- 0800 112 112 : autorisé

**Attention :** une simulation réussie ne valide pas la réception d'un vrai appel, la disponibilité du rôle Android, l'absence de sonnerie ni les notifications. Tester ces aspects physiquement avant usage quotidien.

## Versions de Java et Kotlin

Le projet utilise **JDK 21 pour Gradle et la compilation**, avec du Java et du Kotlin ciblant **JVM 17** afin de conserver la compatibilité avec Android 10. Kotlin est ici un langage, pas une version du JDK.
