/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.common.recipe.ingredient;

import com.github.chainmailstudios.astromine.common.recipe.ingredient.ArrayIngredient.Entry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ArrayIngredient implements Predicate<ItemStack> {
	private final Entry[] entries;
	private ItemStack[] matchingStacks;
	private Ingredient ingredient;

	private ArrayIngredient(Entry... entries) {
		this.entries = entries;
	}

	public static ArrayIngredient ofItemStacks(ItemStack... stacks) {
		return ofItemStacks(Arrays.asList(stacks));
	}

	public static ArrayIngredient ofItemStacks(Collection<ItemStack> stacks) {
		return new ArrayIngredient(new SimpleEntry(stacks));
	}

	public static ArrayIngredient ofEntries(Stream<? extends Entry> stacks) {
		return new ArrayIngredient(stacks.toArray(Entry[]::new));
	}

	public static ArrayIngredient fromJson(JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			if (json.isJsonObject()) {
				return ofEntries(Stream.of(entryFromJson(json.getAsJsonObject())));
			} else if (json.isJsonArray()) {
				JsonArray jsonArray = json.getAsJsonArray();
				if (jsonArray.size() == 0) {
					throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
				} else {
					return ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map((jsonElement) -> {
						return entryFromJson(JSONUtils.convertToJsonObject(jsonElement, "item"));
					}));
				}
			} else {
				throw new JsonSyntaxException("Expected item to be object or array of objects");
			}
		} else {
			throw new JsonSyntaxException("Item cannot be null");
		}
	}

	private static Entry entryFromJson(JsonObject json) {
		if (json.has("item") && json.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
		} else {
			int count = 1;
			if (json.has("count")) {
				count = JSONUtils.getAsInt(json, "count");
			}
			if (json.has("item")) {
				ResourceLocation itemId = new ResourceLocation(JSONUtils.getAsString(json, "item"));
				Item item = Registry.ITEM.getOptional(itemId).orElseThrow(() -> {
					return new JsonSyntaxException("Unknown item '" + itemId + "'");
				});
				return new SimpleEntry(new ItemStack(item, count));
			} else if (json.has("tag")) {
				ResourceLocation tagId = new ResourceLocation(JSONUtils.getAsString(json, "tag"));
				ITag<Item> tag = TagCollectionManager.getInstance().getItems().getTag(tagId);
				if (tag == null) {
					throw new JsonSyntaxException("Unknown item tag '" + tagId + "'");
				} else {
					return new TagEntry(tag, count);
				}
			} else {
				throw new JsonParseException("An ingredient entry needs either a tag or an item");
			}
		}
	}

	public static ArrayIngredient fromPacket(PacketBuffer buffer) {
		int i = buffer.readVarInt();
		return ofEntries(Stream.generate(() -> {
			return new SimpleEntry(buffer.readItem());
		}).limit(i));
	}

	public ItemStack[] getMatchingStacks() {
		this.cacheMatchingStacks();
		return this.matchingStacks;
	}

	private void cacheMatchingStacks() {
		if (this.matchingStacks == null) {
			this.matchingStacks = Arrays.stream(this.entries).flatMap(Entry::getStacks).distinct().toArray(ItemStack[]::new);
		}
	}

	public Ingredient asIngredient() {
		if (ingredient == null) {
			ingredient = Ingredient.of(Stream.of(getMatchingStacks()));
		}
		return ingredient;
	}

	@Override
	public boolean test(ItemStack stack) {
		return testMatching(stack) != null;
	}

	public ItemStack testMatching(ItemStack stack) {
		if (stack == null)
			return null;
		ItemStack[] matchingStacks = getMatchingStacks();
		if (this.matchingStacks.length == 0)
			return null;
		for (ItemStack matchingStack : matchingStacks) {
			if (ItemStack.isSameIgnoreDurability(matchingStack, stack) && stack.getCount() >= matchingStack.getCount())
				return matchingStack.copy();
		}
		return null;
	}

	public void write(PacketBuffer buffer) {
		this.cacheMatchingStacks();
		buffer.writeVarInt(this.matchingStacks.length);

		for (ItemStack matchingStack : this.matchingStacks) {
			buffer.writeItem(matchingStack);
		}
	}

	interface Entry {
		Stream<ItemStack> getStacks();
	}

	private static class SimpleEntry implements Entry {
		private final Collection<ItemStack> stacks;

		public SimpleEntry(Collection<ItemStack> stacks) {
			this.stacks = stacks;
		}

		public SimpleEntry(ItemStack stack) {
			this(Collections.singleton(stack));
		}

		@Override
		public Stream<ItemStack> getStacks() {
			return stacks.stream();
		}
	}

	private static class TagEntry implements Entry {
		private final ITag<Item> tag;
		private final int count;

		private TagEntry(ITag<Item> tag, int count) {
			this.tag = tag;
			this.count = count;
		}

		private TagEntry(ITag<Item> tag) {
			this(tag, 1);
		}

		@Override
		public Stream<ItemStack> getStacks() {
			return this.tag.getValues().stream().map(item -> new ItemStack(item, count));
		}
	}
}
