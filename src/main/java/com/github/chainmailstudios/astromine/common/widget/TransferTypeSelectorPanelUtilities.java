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

package com.github.chainmailstudios.astromine.common.widget;

import com.github.chainmailstudios.astromine.common.component.block.entity.BlockEntityTransferComponent;
import com.github.chainmailstudios.astromine.common.utilities.MirrorUtilities;
import com.github.vini2003.blade.common.data.Position;
import com.github.vini2003.blade.common.data.Size;
import com.github.vini2003.blade.common.data.widget.TabCollection;
import com.google.common.collect.ImmutableMap;
import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class TransferTypeSelectorPanelUtilities {
	public static void createTab(TabCollection tab, Position anchor, Direction rotation, BlockEntityTransferComponent component, BlockPos blockPos, ComponentType<?> type) {
		final Position finalNorth = new Position(anchor.getX() + 7 + 22, anchor.getY() + 31 + 22);
		final Position finalSouth = new Position(anchor.getX() + 7 + 0, anchor.getY() + 31 + 44);
		final Position finalUp = new Position(anchor.getX() + 7 + 22, anchor.getY() + 31 + 0);
		final Position finalDown = new Position(anchor.getX() + 7 + 22, anchor.getY() + 31 + 44);
		final Position finalWest = new Position(anchor.getX() + 7 + 44, anchor.getY() + 31 + 22);
		final Position finalEast = new Position(anchor.getX() + 7 + 0, anchor.getY() + 31 + 22);

		final ImmutableMap<Direction, Position> positons = ImmutableMap.<Direction, Position> builder()
				.put(Direction.NORTH, finalNorth)
				.put(Direction.SOUTH, finalSouth)
				.put(Direction.WEST, finalWest)
				.put(Direction.EAST, finalEast)
				.put(Direction.UP, finalUp)
				.put(Direction.DOWN, finalDown)
				.build();

		for (Direction direction : Direction.values()) {
			TransferTypeSelectorButtonWidget button = new TransferTypeSelectorButtonWidget();
			button.setPosition(positons.get(MirrorUtilities.rotate(direction, rotation)));
			button.setSize(new Size(18, 18));
			button.setComponent(component);
			button.setType(type);
			button.setRotation(rotation);
			button.setDirection(direction);
			button.setBlockPos(blockPos);

			tab.addWidget(button);
		}
	}
}
