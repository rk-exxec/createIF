# Create: IngredientFilter

You know those modpacks, where the author tries to be very forthcoming to their playerbase by enabling them to multiply yields of lower tiered recipes by just adding another item?

Good intentions, I like it a lot, prevents having to rebuild parts of the factory when unlocking new recipes.

However, Create seems to not have thought of that at all, basins just pick any recipe matching a subset ingredients. 

The solution: New filter type!

Allows you to specify items that need to be in the basin before it may start.

TODO: Check that only maximally matching recipe is then also executed!

TODO: mixin inject in update basing after filter existance ckeck? -> only called once per recipe test
     then check every recipe for maximum overlap in matchRecipe

TODO: rework filter "inventory" to allow both requirements and resulting recipe. 
make it addable via emi "plus", make it so that wrapping match is not necessary cus the recipe result will be in  that listfilter overlap