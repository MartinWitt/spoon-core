# SpoonRebuilder

This is a simple tool to rebuild [Spoon](https://github.com/INRIA/spoon) without generics.
Spoon is super backward compatible, and therefore we keep the generics in the codebase.
The generics are never really used and are hard to use correctly.
This tool removes the generics from the codebase.

All features of Spoon except Templates/Patterns should work.
The only generic left is the one in the `CtLiteral` interface.
This generic is used to specify the type of the literal and useful for the `CtLiteral#getValue()` method.


## Motivation
Spoon is a great tool, but it is hard to use correctly. The generics are not used and if used are hard to use correctly.
They only add complexity to the code and are hard to understand for starters.
## Usage
Currently, the tool is not published on Maven Central. You can either install it locally with `mvn install` or [jitpack.io](https://jitpack.io/).
For every spoon commit, we publish a new release on GitHub.
1. Go to [https.//github.com/inria/spoon/spoon](https://github.com/INRIA/spoon/) and search for the commit you want to use.
2. Go to [https://jitpack.io/#MartinWitt/spoon-core/](https://jitpack.io/#MartinWitt/spoon-core/) and search for the commit in the list of releases.
3. Follow the instructions on the page to add the dependency to your project (Gradle, Maven, etc.).
### Gradle
1. Add the JitPack repository to your build file:
Add it in your root `build.gradle` at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. Add the dependency
```groovy
dependencies {
		implementation 'com.github.MartinWitt:spoon-core:spoon-core-<commit>'
}
```
### Maven
1. Add the JitPack repository to your build file:
```xml
<repositories>
<repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>
</repositories>
```
2. Add the dependency
```xml
<dependency>
	<groupId>com.github.MartinWitt</groupId>
	<artifactId>spoon-core</artifactId>
	<version>commit-633b0f2b5af615ff8f3f078a22ffef45a3099d5a</version>
</dependency>
```
## License
The code is licensed under the [[MIT License](](https://opensource.org/licenses/MIT).
Spoon is licensed under the [CeCILL-C License](https://github.com/INRIA/spoon/blob/master/LICENSE-CECILL-C.txt) or  [MIT License](https://github.com/INRIA/spoon/blob/master/LICENSE-MIT.txt) at your option. SPDX-License-Identifier: (MIT OR CECILL-C).
Therefore, these binaries are licensed under the same licenses as Spoon.
## SniperPrinting Fixes

Sniperprinting a large project has some small problems. The following fixes are applied to the code after printing:
- [CastSniperFixer.java](src/main/java/io/github/martinwitt/spoonrebuilder/fixes/CastSniperFixer.java) fixes the sniper printing of casts. Also, it includes some fixes for sniper printing problems with comments.
- [GenericTypeArgumentsFixer.java](src/main/java/io/github/martinwitt/spoonrebuilder/fixes/GenericTypeArgumentsFixer.java) fixes the sniper printing of inner generic type arguments.
- [PomGroupIdFixer](src/main/java/io/github/martinwitt/spoonrebuilder/fixes/PomGroupIdFixer.java) fixes the groupId of the pom.xml file. This is necessary because the groupId is changed to `io.github.martinwitt.spoonrebuilder` by jitpack.io.
## Contributors ✨
Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):
<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->
