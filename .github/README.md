<h1 align="center">File Items</h1>

<div align="center">

[![](https://github.com/Despical/FileItems/actions/workflows/build.yml/badge.svg)](https://github.com/Despical/FileItems/actions/workflows/build.yml)
[![](https://img.shields.io/maven-central/v/com.github.despical/file-items.svg?label=Maven%20Central)](https://repo1.maven.org/maven2/com/github/despical/file-items)
[![](https://img.shields.io/badge/License-GPLv3-blue.svg)](../LICENSE)
[![](https://img.shields.io/badge/Javadoc-latest-blue.svg)](https://despical.github.io/FileItems)

File Items is a very lightweight library that helps loading items with customizable features, such as display names, glowing effects,
hidden tooltips, amounts, lore, data, durability, unbreakable item flags, and custom keys from a YAML file.

</div>

## Documentation
- [Wiki](https://github.com/Despical/FileItems/wiki)
- [Javadocs](https://despical.github.io/FileItems)
- [Maven Central](https://repo1.maven.org/maven2/com/github/despical/file-items)
- [Sonatype Central](https://central.sonatype.com/artifact/com.github.despical/file-items)

## Donations
- [Patreon](https://www.patreon.com/despical)
- [Buy Me A Coffee](https://www.buymeacoffee.com/despical)

## Using File Items
To add this project as a dependency to your project, add the following to your pom.xml:

### Maven
```xml
<dependency>
    <groupId>com.github.despical</groupId>
    <artifactId>file-items</artifactId>
    <version>1.1.9</version>
</dependency>
```

### Gradle dependency
```gradle
dependencies {
    implementation 'com.github.despical:file-items:1.1.9'
}
```

## Example usage

```java
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
