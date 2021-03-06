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

package com.github.chainmailstudios.astromine.transportations.registry;

import net.minecraft.block.entity.BlockEntityType;

import com.github.chainmailstudios.astromine.registry.AstromineBlockEntityTypes;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.AlternatorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.ConveyorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.DownVerticalConveyorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.IncineratorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.InserterBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.SplitterBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.VerticalConveyorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.base.AbstractConveyableBlockEntity;

public class AstromineTransportationsBlockEntityTypes extends AstromineBlockEntityTypes {
	public static final BlockEntityType<AbstractConveyableBlockEntity> ALTERNATOR = register("alternator", AlternatorBlockEntity::new, AstromineTransportationsBlocks.ALTERNATOR);
	public static final BlockEntityType<AbstractConveyableBlockEntity> SPLITTER = register("splitter", SplitterBlockEntity::new, AstromineTransportationsBlocks.SPLITTER);
	public static final BlockEntityType<IncineratorBlockEntity> INCINERATOR = register("incinerator", IncineratorBlockEntity::new, AstromineTransportationsBlocks.INCINERATOR);
	public static final BlockEntityType<InserterBlockEntity> INSERTER = register("inserter", InserterBlockEntity::new, AstromineTransportationsBlocks.INSERTER, AstromineTransportationsBlocks.FAST_INSERTER);

	public static final BlockEntityType<ConveyorBlockEntity> CONVEYOR = register("conveyor", ConveyorBlockEntity::new, AstromineTransportationsBlocks.BASIC_CONVEYOR, AstromineTransportationsBlocks.ADVANCED_CONVEYOR, AstromineTransportationsBlocks.ELITE_CONVEYOR);
	public static final BlockEntityType<VerticalConveyorBlockEntity> VERTICAL_CONVEYOR = register("vertical_conveyor", VerticalConveyorBlockEntity::new, AstromineTransportationsBlocks.BASIC_VERTICAL_CONVEYOR, AstromineTransportationsBlocks.ADVANCED_VERTICAL_CONVEYOR,
		AstromineTransportationsBlocks.ELITE_VERTICAL_CONVEYOR);
	public static final BlockEntityType<DownVerticalConveyorBlockEntity> DOWNWARD_VERTICAL_CONVEYOR = register("downward_vertical_conveyor", DownVerticalConveyorBlockEntity::new, AstromineTransportationsBlocks.BASIC_DOWNWARD_VERTICAL_CONVEYOR,
		AstromineTransportationsBlocks.ADVANCED_DOWNWARD_VERTICAL_CONVEYOR, AstromineTransportationsBlocks.ELITE_DOWNWARD_VERTICAL_CONVEYOR);

	public static void initialize() {

	}
}
