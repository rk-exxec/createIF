# CreatIF - Create: IngredientFilter

<a href="https://www.curseforge.com/minecraft/mc-mods/creatif"> <img alt="CurseForge Downloads" src="https://img.shields.io/curseforge/dt/1703463?style=for-the-badge&logo=curseforge&logoColor=23F16436&label=CurseForge"></a> <a href="https://modrinth.com/mod/creatif" ><img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/creatifTfWCSyeN?style=for-the-badge&logo=modrinth&logoColor=%2300AF5C&logoSize=100&label=Modrinth"></a>

Versions: [Forge 1.20.1](https://github.com/rk-exxec/createIF/tree/mc1.20.1), [NeoForge 1.21.1](https://github.com/rk-exxec/createIF/tree/mc1.21.1)


You know those modpacks, where the author tries to be very forthcoming to their playerbase by enabling them to multiply yields of lower tiered recipes by just adding another item?

Good intentions, I like it a lot, prevents having to rebuild parts of the factory when unlocking new recipes.

However, Create seems to not have had that in mind when designing basin processing.

## The solution: New filter type!

<img alt="alt: empty filter ui" src="https://raw.githubusercontent.com/rk-exxec/createIF/refs/heads/master/images/blank_filter.png" width="400">  <img alt="alt: filter item" src="https://raw.githubusercontent.com/rk-exxec/createIF/refs/heads/master/images/icon.png" width="400"> 

Allows you to specify items that need to be in a basin before it may start on the left.  
You can also use buckets/fluids to require that specific liquids are present.  
Optionally you can specify the target recipe in the slot on the right.

**Supports adding recipes via \[+\] Button in EMI or JEI!**

### Example:

A recipe for a modded item has these two variants (check out Compression Modpack):

<img alt="alt: mixing recipe with 3 ingredients" src="https://raw.githubusercontent.com/rk-exxec/createIF/refs/heads/master/images/recipe_less.png" width="400"> <img alt="alt: mixing recipe with one more ingredient" src="https://raw.githubusercontent.com/rk-exxec/createIF/refs/heads/master/images/recipe_more.png" width="400"> 

Normally, no issue, but if the additional item runs out, the basin will never go back to the better recipe.

However, with my filter, you can stop the basin if that happens.

<img alt="alt: filter with full recipe" src="https://raw.githubusercontent.com/rk-exxec/createIF/refs/heads/master/images/filter_with_recipe.png" width="400">  <img alt="alt: filter with only 4th item and match any config" src="https://raw.githubusercontent.com/rk-exxec/createIF/refs/heads/master/images/filter_with_recipe_any.png" width="400"> 

Both of the above prevent processing until the 4th ingredient is available.  
Note the rightmost pair of buttons:

*   MatchAny allows processing if any of the ingredients specified in the filter is available.
*   MatchAll waits until ALL are available.

You can even nest normal filters, like here for colored concrete powder  
<img alt="alt: example of nesting normal filters" src="https://raw.githubusercontent.com/rk-exxec/createIF/refs/heads/master/images/nested_filter.png" width="400"> 

which produces following interactions:

*   MatchAny: Expands available inventory (boring)
*   MatchAll: Checks any ONE item in the nested filter is available. Like:  
    `OuterFilter[Item1 AND Item2 AND InnerFilter(Item3 OR Item4)]`

NOTE: You cannot nest Ingredient filters!
