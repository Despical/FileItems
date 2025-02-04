<h1 align="center">File Items</h1>

<div align="center">

[![](https://github.com/Despical/FileItems/actions/workflows/build.yml/badge.svg)](https://github.com/Despical/FileItems/actions/workflows/build.yml)
[![](https://img.shields.io/github/v/release/Despical/FileItems)](https://github.com/Despical/FileItems/releases/latest)
[![](https://jitpack.io/v/Despical/FileItems.svg)](https://jitpack.io/#Despical/FileItems)
[![](https://img.shields.io/badge/License-GPLv3-blue.svg)](../LICENSE)
[![](https://img.shields.io/badge/javadoc-latest-lime.svg)](https://javadoc.jitpack.io/com/github/Despical/FileItems/latest/javadoc/index.html)

File Items is a very lightweight library that helps loading items with customizable features, such as display names, glowing effects,
hidden tooltips, amounts, lore, data, durability, unbreakable item flags, and custom keys from a YAML file.

</div>

## Documentation
- [Wiki](https://github.com/Despical/FileItems/wiki)
- [JavaDocs](https://javadoc.jitpack.io/com/github/Despical/FileItems/latest/javadoc/index.html)

## Donations
- [Patreon](https://www.patreon.com/despical)
- [Buy Me A Coffee](https://www.buymeacoffee.com/despical)

## Using File Items
The project isn't in the Central Repository yet, so specifying a repository is needed.<br>
To add this project as a dependency to your project, add the following to your pom.xml:

### Maven dependency

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.Despical</groupId>
    <artifactId>FileItems</artifactId>
    <version>1.1.3</version>
</dependency>
```

### Gradle dependency
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
    implementation 'com.github.Despical:FileItems:1.1.3'
}
```

## Example usage

```java
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 19.09.2024
 */
public class ExamplePlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		// Create the file if not exist.

		ItemManager itemManager = new ItemManager(this);
		itemManager.addCustomKey("slot");
		itemManager.registerItems("items", "game-items");

		SpecialItem item = itemManager.getItem("leave-item");
		int slot = item.getCustomKey("slot");

		getServer().getOnlinePlayers().forEach(player -> player.getInventory().setItem(slot, item.getItemStack()));
	}
}
```

```yaml
game-items:
  leave-item:
    name: "&c&lReturn to Lobby &7(Right Click)"
    material: RED_BED
    slot: 8
    lore:
      - "&7Right-click to leave to the lobby!"
```

## License
This code is under [GPL-3.0 License](http://www.gnu.org/licenses/gpl-3.0.html)

See the [LICENSE](https://github.com/Despical/FileItems/blob/main/LICENSE) file for required notices and attributions.

## Contributing

I accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for me:
+ Ensure you didn't use spaces! Please use tabs for indentation.
+ Respect the code style.
+ Do not increase the version numbers in any examples files and the README.md to the new version that this Pull Request would represent.
+ Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source code should be reformatted create a separate PR for this change.

You can learn more about contributing via GitHub in [contribution guidelines](../CONTRIBUTING.md).

## Building from source
To build this project from source code, run the following from Git Bash:
```
git clone https://www.github.com/Despical/FileItems && cd FileItems
mvn clean package
```

> [!IMPORTANT]  
> **[Maven](https://maven.apache.org/)** must be installed to build this project.
