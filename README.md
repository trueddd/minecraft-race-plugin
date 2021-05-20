### Description

Minecraft server plugin for adjusting player attributes for races provided by such mods like JJ Races. Attributes adjusting is possible via plugin command: `/race <nickname> <race>`. All list of races loaded from config can be retrieved via `/races`. All commands request op permissions to run.

### Configuring plugin

Plugin config must be located at `/plugins` folder and have `.yml` extension.
Plugin config example (config.yml):
```yaml
world: "world" // name of the world where plugin should change players` data
races: // list of all races available on server
  default:
    abilities/walkSpeed: // path to attribute in *.dat file
      type: float // type of attribute
      value: 0.1f // value to apply
    Attributes[Name:generic_maxHealth]/Base:
      type: double
      value: 20.0
    Health:
      type: double
      value: 20.0
  speedRunner:
    abilities/walkSpeed:
      type: float
      value: 0.2f
    Attributes[Name:generic_maxHealth]/Base:
      type: double
      value: 16.0
    Health:
      type: double
      value: 16.0
```

All nested tags are separated by slash symbol (`/`).

If you want to change attribute that is located at List tag (e.g. `Attributes`), you have to provide path like this: `Attributes[Name:generic_maxHealth]/Base`. Where:
- `Attributes` - root tag
- `Name:generic_maxHealth` - pair of tag name and tag value of tag you want to modify to
- `Base` - name of the tag you want to modify

WARNING: All dots in paths should be replaced by underscore symbols (`_`).

Available types:

|Name|Config syntax|Config value example|NBT tag type|
|----|-------------|--------------------|------------|
|Byte|`byte`|2|TAG_Byte|
|Short|`short`|12|TAG_Short|
|Int|`int`|44|TAG_Int|
|Long|`long`|552L|TAG_Long|
|Float|`float`|20.0f|TAG_Float|
|Double|`double`|10.0|TAG_Double|
|String|`string`|qwerty|TAG_String|

More info about NBTags [here](https://minecraft.fandom.com/wiki/NBT_format).