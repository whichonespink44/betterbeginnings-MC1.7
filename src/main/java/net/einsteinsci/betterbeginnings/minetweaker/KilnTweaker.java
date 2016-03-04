package net.einsteinsci.betterbeginnings.minetweaker;

import java.util.ArrayList;
import java.util.List;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.einsteinsci.betterbeginnings.minetweaker.util.KilnRecipeWrapper;
import net.einsteinsci.betterbeginnings.register.recipe.KilnRecipes;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterbeginnings.Kiln")
public class KilnTweaker 
{
	@ZenMethod
	public static void addRecipe(IIngredient input, IItemStack output, float xp)
	{
		MineTweakerAPI.apply(new AddKilnRecipe(input, output, xp));
	}
	
	@ZenMethod
	public static void addRecipe(IIngredient input, IItemStack output)
	{
		MineTweakerAPI.apply(new AddKilnRecipe(input, output, 0.2f));
	}
	
	@ZenMethod
	public static void removeRecipe(IIngredient input, IItemStack output)
	{
		MineTweakerAPI.apply(new RemoveKilnRecipe(input, output));
	}
	
	@ZenMethod
	public static void removeOutput(IItemStack output)
	{
		MineTweakerAPI.apply(new RemoveKilnOutput(output));
	}
	
	private static class AddKilnRecipe implements IUndoableAction
	{
		private IIngredient input;
		private ItemStack output;
		private float xp;
		
		public AddKilnRecipe(IIngredient input, IItemStack output, float xp) 
		{
			this.input = input;
			this.output = MineTweakerMC.getItemStack(output);
			this.xp = xp;
		}

		@Override
		public void apply() 
		{
			for(IItemStack inputStack : input.getItems())
			{
				KilnRecipes.addRecipe(MineTweakerMC.getItemStack(input), output, xp);
			}
			
		}

		@Override
		public boolean canUndo() 
		{
			return true;
		}

		@Override
		public void undo() 
		{
				KilnRecipes.removeRecipe(MineTweakerMC.getItemStack(input), null);
		}

		@Override
		public String describe() 
		{
			String inputIDString = (input instanceof IOreDictEntry) ? ((IOreDictEntry) input).getName() : MineTweakerMC.getItemStack(input).getDisplayName();
			return "Adding recipe " + inputIDString 
					+ " -> " + output.getDisplayName() + " * "  + output.stackSize + " to Kiln";
		}

		@Override
		public String describeUndo() 
		{
			String inputIDString = (input instanceof IOreDictEntry) ? ((IOreDictEntry) input).getName() : MineTweakerMC.getItemStack(input).getDisplayName();
			return "Removing recipe " + inputIDString
					+ " -> " + output.getDisplayName() + " * "  + output.stackSize + " from Kiln";
		}

		@Override
		public Object getOverrideKey() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class RemoveKilnRecipe implements IUndoableAction
	{
		private IIngredient input;
		private ItemStack output;
		private float xp;

		public RemoveKilnRecipe(IIngredient input, IItemStack output) 
		{
			this.input = input;
			this.output = MineTweakerMC.getItemStack(output);
			this.xp = KilnRecipes.smelting().giveExperience(MineTweakerMC.getItemStack(input));
		}
		
		@Override
		public void apply() 
		{
			for(IItemStack inputStack : input.getItems())
			{
				KilnRecipes.removeRecipe(MineTweakerMC.getItemStack(inputStack), output);
			}
		}

		@Override
		public boolean canUndo() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void undo() 
		{
			for(IItemStack inputStack : input.getItems())
			{
				KilnRecipes.addRecipe(MineTweakerMC.getItemStack(inputStack), output, xp);
			}	
		}

		@Override
		public String describe() 
		{
			String inputIDString = (input instanceof IOreDictEntry) ? ((IOreDictEntry) input).getName() : MineTweakerMC.getItemStack(input).getDisplayName();
			return "Removing recipe " + inputIDString
					+ " -> " + output.getDisplayName() + " * "  + output.stackSize + " from Kiln";
		}

		@Override
		public String describeUndo() 
		{
			String inputIDString = (input instanceof IOreDictEntry) ? ((IOreDictEntry) input).getName() : MineTweakerMC.getItemStack(input).getDisplayName();
			return "Readding recipe " + inputIDString
					+ " -> " + output.getDisplayName() + " * "  + output.stackSize + " to Kiln";
		}

		@Override
		public Object getOverrideKey() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class RemoveKilnOutput implements IUndoableAction
	{
		private ItemStack output;
		private List<KilnRecipeWrapper> removedRecipes = new ArrayList<KilnRecipeWrapper>();

		public RemoveKilnOutput(IItemStack output) 
		{
			this.output = MineTweakerMC.getItemStack(output);
		}

		public void apply() 
		{
			removedRecipes = KilnRecipes.removeOutput(output);
		}

		@Override
		public boolean canUndo() 
		{
			return true;
		}

		@Override
		public void undo() 
		{
			for(KilnRecipeWrapper r : removedRecipes)
			{
				KilnRecipes.addRecipe(r.getInput(), r.getOutput(), r.getXP());
			}
		}

		@Override
		public String describe() 
		{
			return "Removing recipes for " +  output.getDisplayName() + " * "  + output.stackSize + " from Smelter";
		}

		@Override
		public String describeUndo() 
		{
			return "Readding recipes for " + output.getDisplayName() + " * "  + output.stackSize + " to Smelter";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}

	}
}
